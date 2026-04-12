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
import service.MenuService;

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
 * Diagrama de Robustez (CU3):
 *   Control "Validador de Orden de Compra"    → validarOrdenDeCompra()
 *   Control "Gestor de Inventario"            → sumarStock()
 *   Control "Generador de Comprobantes"       → generarComprobante()
 *   Entidad "Stock"                           → Insumo.cantidad actualizado en BD
 *   Entidad "Orden de Compra"                 → OrdenDeCompra persistida en BD
 *
 * Diagrama de Secuencia (CU3 implícito):
 *   AdministradorBodega → InsumoEntradaServlet → InsumoService.registrarEntradaInsumos()
 *                       → [validar] → [sumarStock en loop por detalle] → [generarComprobante]
 *                       → PlataformaDelivery (actualización stock disponible)
 */
public class InsumoService {

    private final InsumoDaoHibernate insumoDao;
    private final OrdenDeCompraDaoHibernate ordenDao;
    private final MenuService menuService;

    public InsumoService() {
        this.insumoDao   = new InsumoDaoHibernate();
        this.ordenDao    = new OrdenDeCompraDaoHibernate();
        inicializarInsumosBase();
        this.menuService = new MenuService();
        // Sincronizar estado inicial del menu con el stock actual
        menuService.sincronizarDisponibilidadMenu();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INICIALIZACIÓN DE DATOS BASE (datos "quemados" para el demo)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Inserta insumos de muestra compatibles con las marcas ya existentes
     * (Sushi, Hamburguesas, etc.) si la tabla está vacía.
     */
    private void inicializarInsumosBase() {
        List<Insumo> existentes = insumoDao.findAll();
        if (!existentes.isEmpty()) return;

        Object[][] datos = {
            // nombre,                unidad,  stockActual, stockMinimo
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

    /**
     * Devuelve todos los insumos del inventario centralizado.
     * Trazabilidad → Diagrama de Actividades paso "listar inventario centralizado".
     */
    public List<Insumo> listarTodosInsumos() {
        return insumoDao.findAll();
    }

    /**
     * Método principal del CU3. Recibe los datos de la factura + los ítems
     * comprados, valida la orden y suma el stock de cada insumo.
     *
     * Trazabilidad → Diagrama de Actividades pasos 1-7 del CU3.
     * Trazabilidad → Diagrama de Secuencia: mensaje registrarEntrada() →
     *                validarOrden() → sumarStock() → generarComprobante().
     *
     * @param numeroFactura   Número de la factura del proveedor.
     * @param nombreProveedor Nombre del proveedor.
     * @param fechaFactura    Fecha de emisión de la factura.
     * @param insumoIds       IDs de insumos comprados.
     * @param cantidadesPedidas    Cantidades indicadas en la factura.
     * @param cantidadesRecibidas  Cantidades físicamente recibidas en bodega.
     * @param preciosUnitarios     Precio unitario de cada insumo en esta compra.
     * @return La OrdenDeCompra persistida (con estado PENDIENTE o DISCREPANCIA).
     * @throws IllegalArgumentException si los datos son inválidos.
     */
    public OrdenDeCompra registrarEntradaInsumos(
            String numeroFactura,
            String nombreProveedor,
            LocalDate fechaFactura,
            Long[] insumoIds,
            Double[] cantidadesPedidas,
            Double[] cantidadesRecibidas,
            Double[] preciosUnitarios) {

        // ── Paso 1: Validar datos de la orden de compra (Control: Validador) ──
        validarOrdenDeCompra(numeroFactura, nombreProveedor, fechaFactura,
                             insumoIds, cantidadesPedidas, cantidadesRecibidas, preciosUnitarios);

        // ── Paso 2: Crear la OrdenDeCompra con estado inicial PENDIENTE ────────
        OrdenDeCompra orden = new OrdenDeCompra();
        orden.setNumeroFactura(numeroFactura.trim());
        orden.setNombreProveedor(nombreProveedor.trim());
        orden.setFechaFactura(fechaFactura);
        orden.setEstado(EstadoOrden.PENDIENTE);

        boolean hayDiscrepanciaGlobal = false;

        // ── Paso 3-4: Construir detalles y sumar stock (Gestor de Inventario) ──
        for (int i = 0; i < insumoIds.length; i++) {
            Insumo insumo = insumoDao.findById(insumoIds[i])
                .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado."));

            DetalleOrden detalle = new DetalleOrden();
            detalle.setOrdenDeCompra(orden);
            detalle.setInsumo(insumo);
            detalle.setCantidadPedida(cantidadesPedidas[i]);
            detalle.setCantidadRecibida(cantidadesRecibidas[i]);
            detalle.setPrecioUnitario(preciosUnitarios[i]);
            orden.getDetalles().add(detalle);

            if (detalle.hayDiscrepancia()) {
                hayDiscrepanciaGlobal = true;
            }

            // Sumar al inventario centralizado con la cantidad RECIBIDA físicamente
            sumarStock(insumo, cantidadesRecibidas[i]);
        }

        // ── Paso 5: Detectar discrepancias (extend del CU3) ───────────────────
        if (hayDiscrepanciaGlobal) {
            orden.setEstado(EstadoOrden.DISCREPANCIA);
        }

        // ── Paso 6: Persistir la orden (Generador de Comprobantes) ────────────
        ordenDao.save(orden);

        return orden; // El comprobante lo renderiza la JSP usando este objeto
    }

    /**
     * Suma stock de un insumo en el inventario centralizado.
     *
     * Trazabilidad → Diagrama de Clases: método sumarStock(cantidad) en Insumo.
     * Trazabilidad → Diagrama de Actividades: paso "Sumar existencias al inventario centralizado".
     */
    public void sumarStock(Insumo insumo, double cantidad) {
        insumo.setCantidad(insumo.getCantidad() + cantidad);
        insumoDao.update(insumo);
        // CU4: reevaluar disponibilidad del menu tras cambio de stock
        menuService.sincronizarDisponibilidadMenu();
    }

    /**
     * Reduce stock de un insumo (usado por el botón "Reducir" en la vista).
     *
     * Trazabilidad → Diagrama de Clases: método descontarStock(cantidad) en Insumo.
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
        // CU4: reevaluar disponibilidad del menu tras cambio de stock
        menuService.sincronizarDisponibilidadMenu();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MÉTODOS DE VALIDACIÓN (Control: Validador de Orden de Compra)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Valida que todos los campos obligatorios estén presentes y sean coherentes.
     * Trazabilidad → Diagrama de Robustez CU3: Control "Validador de Orden de Compra".
     */
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
}
