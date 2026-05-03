package service;

import config.HibernateUtil;
import dao.InsumoDaoHibernate;
import dao.OrdenDeCompraDaoHibernate;
import model.DetalleOrden;
import model.EstadoOrden;
import model.Insumo;
import model.OrdenDeCompra;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para el CU3 – Registrar entrada de insumos compartidos.
 *
 * ─── TRAZABILIDAD ──────────────────────────────────────────────────────────
 *
 * Diagrama de Actividades (CU3):
 *   1. Cargar datos de la orden de compra / factura       → registrarEntradaInsumos()
 *   2. Verificar orden de compra en los registros de BD   → validarOrdenDeCompra()
 *   3. Habilitar formulario de ingreso de insumos         → (JSP cu3)
 *   4. Registrar cantidades recibidas por ítem            → registrarEntradaInsumos()
 *   5. Sumar existencias al inventario centralizado       → sumarStock()
 *   6. Actualizar valores de costos de insumos            → sumarStock() actualiza precioUnitario
 *   7. Generar comprobante de recepción exitosa           → generarComprobante()
 *   (extend) Generar reporte de discrepancia              → detectarDiscrepancias()
 *
 * Tarea 3 (integración AlertaService):
 *   • reducirStock()            → llama alertaService.generarSiCritico() tras el descuento
 *   • registrarEntradaInsumos() → llama alertaService.resolverSiActiva() si stock supera mínimo
 */
public class InsumoService {

    private final InsumoDaoHibernate insumoDao;
    private final OrdenDeCompraDaoHibernate ordenDao;
    private final MenuService menuService;
    /** Tarea 3: integración con el sistema de alertas */
    private final AlertaService alertaService;

    public InsumoService() {
        this.insumoDao    = new InsumoDaoHibernate();
        this.ordenDao     = new OrdenDeCompraDaoHibernate();
        this.alertaService = new AlertaService();
        inicializarInsumosBase();
        this.menuService  = new MenuService();
        menuService.sincronizarDisponibilidadMenu();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INICIALIZACIÓN DE DATOS BASE
    // ─────────────────────────────────────────────────────────────────────────

    private void inicializarInsumosBase() {
        List<Insumo> existentes = insumoDao.findAll();
        if (!existentes.isEmpty()) return;

        Object[][] datos = {
                {"Arroz para sushi",      "kg",    15.0,  3.0},
                {"Alga nori",             "unid",  200.0, 50.0},
                {"Salmón fresco",         "kg",    8.0,   2.0},
                {"Atún fresco",           "kg",    6.0,   2.0},
                {"Aguacate",              "unid",  40.0,  10.0},
                {"Pepino",                "unid",  30.0,  8.0},
                {"Queso crema",           "kg",    4.0,   1.0},
                {"Salsa de soya",         "L",     5.0,   1.0},
                {"Carne de res molida",   "kg",    12.0,  3.0},
                {"Pan de hamburguesa",    "unid",  60.0,  15.0},
                {"Queso cheddar",         "kg",    5.0,   1.5},
                {"Lechuga",               "unid",  25.0,  5.0},
                {"Tomate",                "unid",  30.0,  8.0},
                {"Pepinillo",             "kg",    3.0,   0.5},
                {"Cebolla",               "kg",    4.0,   1.0},
                {"Papas para freír",      "kg",    20.0,  5.0},
                {"Aceite vegetal",        "L",     10.0,  2.0},
                {"Pollo (pechuga)",       "kg",    10.0,  3.0},
                {"Harina de trigo",       "kg",    8.0,   2.0},
                {"Huevos",                "unid",  48.0,  12.0},
        };

        for (Object[] d : datos) {
            Insumo insumo = new Insumo();
            insumo.setNombre((String) d[0]);
            insumo.setUnidad((String) d[1]);
            insumo.setCantidad((Double) d[2]);
            insumo.setStockMinimo((Double) d[3]);
            insumoDao.save(insumo);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CU3 – REGISTRAR ENTRADA DE INSUMOS COMPARTIDOS
    // ─────────────────────────────────────────────────────────────────────────

    public List<Insumo> listarTodosInsumos() {
        return insumoDao.findAll();
    }

    /**
     * Método principal del CU3.
     * Tarea 3: tras sumar el stock de cada insumo recibido, invoca
     * alertaService.resolverSiActiva(insumo) si el nuevo stock supera el mínimo.
     */
    public OrdenDeCompra registrarEntradaInsumos(
            String numeroFactura,
            String nombreProveedor,
            LocalDate fechaFactura,
            Long[] insumoIds,
            Double[] cantidadesPedidas,
            Double[] cantidadesRecibidas,
            Double[] preciosUnitarios) {

        validarOrdenDeCompra(numeroFactura, nombreProveedor, fechaFactura,
                insumoIds, cantidadesPedidas, cantidadesRecibidas, preciosUnitarios);

        OrdenDeCompra orden = new OrdenDeCompra();
        orden.setNumeroFactura(numeroFactura.trim());
        orden.setNombreProveedor(nombreProveedor.trim());
        orden.setFechaFactura(fechaFactura);
        orden.setEstado(EstadoOrden.PENDIENTE);

        boolean hayDiscrepanciaGlobal = false;

        for (int i = 0; i < insumoIds.length; i++) {
            if (crearDetalleYActualizarStock(
                    orden,
                    insumoIds[i],
                    cantidadesPedidas[i],
                    cantidadesRecibidas[i],
                    preciosUnitarios[i])) {
                hayDiscrepanciaGlobal = true;
            }
        }

        if (hayDiscrepanciaGlobal) {
            orden.setEstado(EstadoOrden.DISCREPANCIA);
        }

        ordenDao.save(orden);
        return orden;
    }

    private boolean crearDetalleYActualizarStock(OrdenDeCompra orden,
                                                 Long insumoId,
                                                 Double cantidadPedida,
                                                 Double cantidadRecibida,
                                                 Double precioUnitario) {
        Insumo insumo = insumoDao.findById(insumoId)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado."));

        DetalleOrden detalle = new DetalleOrden();
        detalle.setOrdenDeCompra(orden);
        detalle.setInsumo(insumo);
        detalle.setCantidadPedida(cantidadPedida);
        detalle.setCantidadRecibida(cantidadRecibida);
        detalle.setPrecioUnitario(precioUnitario);
        orden.getDetalles().add(detalle);

        sumarStock(insumo, cantidadRecibida);

        return detalle.hayDiscrepancia();
    }

    /**
     * Suma stock y notifica al servicio de alertas.
     * Tarea 3: llama alertaService.resolverSiActiva() si el stock supera el mínimo.
     */
    public void sumarStock(Insumo insumo, double cantidad) {
        insumo.setCantidad(insumo.getCantidad() + cantidad);
        insumoDao.update(insumo);
        menuService.sincronizarDisponibilidadMenu();

        // ── Tarea 3: resolver alerta activa si el stock ya superó el mínimo ──
        alertaService.resolverSiActiva(insumo);
    }

    /**
     * Reduce stock y genera alerta si el nivel resultante lo requiere.
     * Tarea 3: llama alertaService.generarSiCritico() tras el descuento.
     *
     * @throws IllegalArgumentException si la cantidad a reducir supera el stock actual.
     */
    public void reducirStock(Long insumoId, double cantidad) {
        Insumo insumo = insumoDao.findById(insumoId)
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado con id=" + insumoId));

        if (cantidad > insumo.getCantidad()) {
            throw new IllegalArgumentException(
                    "No se puede reducir " + cantidad + " " + insumo.getUnidad() +
                            ". Stock actual: " + insumo.getCantidad());
        }
        insumo.setCantidad(insumo.getCantidad() - cantidad);
        insumoDao.update(insumo);
        menuService.sincronizarDisponibilidadMenu();

        // ── Tarea 3: generar alerta si el stock resultante es crítico/advertencia ──
        alertaService.generarSiCritico(insumo);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VALIDACIONES
    // ─────────────────────────────────────────────────────────────────────────

    private void validarOrdenDeCompra(String numeroFactura, String nombreProveedor,
                                      LocalDate fechaFactura, Long[] insumoIds,
                                      Double[] cantidadesPedidas, Double[] cantidadesRecibidas,
                                      Double[] preciosUnitarios) {
        if (numeroFactura == null || numeroFactura.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de factura es obligatorio.");
        }
        if (nombreProveedor == null || nombreProveedor.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
        }
        if (fechaFactura == null) {
            throw new IllegalArgumentException("La fecha de la factura es obligatoria.");
        }
        if (fechaFactura.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de la factura no puede ser futura.");
        }
        if (insumoIds == null || insumoIds.length == 0) {
            throw new IllegalArgumentException("Debe seleccionar al menos un insumo.");
        }
        for (int i = 0; i < insumoIds.length; i++) {
            if (cantidadesPedidas[i] == null || cantidadesPedidas[i] <= 0) {
                throw new IllegalArgumentException("La cantidad pedida debe ser mayor a 0.");
            }
            if (cantidadesRecibidas[i] == null || cantidadesRecibidas[i] < 0) {
                throw new IllegalArgumentException("La cantidad recibida no puede ser negativa.");
            }
            if (preciosUnitarios[i] == null || preciosUnitarios[i] < 0) {
                throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
            }
        }
    }

    public boolean tieneStock(Insumo insumo) {
        return insumo != null && insumo.getCantidad() > 0;
    }
}