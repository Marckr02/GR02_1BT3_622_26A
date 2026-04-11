package service;

import dao.InsumoDao;
import dao.InsumoDaoHibernate;
import dao.ItemMenuDao;
import dao.ItemMenuDaoHibernate;
import model.Insumo;
import model.ItemMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para el CU4 – Bloqueo automático de menú por falta de stock.
 *
 * Flujo (basado en el diagrama de robustez CU4):
 *   1. MonitorDeInventario  → detecta insumos con nivel crítico de stock.
 *   2. AnalistaDePlatillos  → identifica los ItemMenu que usan esos insumos.
 *   3. GestorDeBloqueo      → desactiva los platos afectados.
 *   4. API de Notificaciones → emite alerta de reposición urgente.
 */
public class MenuService {

    private final InsumoDao  insumoDao;
    private final ItemMenuDao itemMenuDao;

    public MenuService() {
        this.insumoDao   = new InsumoDaoHibernate();
        this.itemMenuDao = new ItemMenuDaoHibernate();
        inicializarDatosDemo();
    }

    // ── CU4 Paso 1: Monitor de Inventario ────────────────────────────────────
    public List<Insumo> detectarInsumosCriticos() {
        return insumoDao.findAll().stream()
                .filter(i -> i.getCantidad() <= i.getStockMinimo())
                .collect(Collectors.toList());
    }

    // ── CU4 Paso 2: Analista de platillos afectados ───────────────────────────
    public List<ItemMenu> identificarPlatosAfectados(Insumo insumo) {
        return itemMenuDao.findAll().stream()
                .filter(ItemMenu::isActivo)
                .filter(item -> item.getInsumosRequeridos().stream()
                        .anyMatch(d -> d.getInsumo().getId().equals(insumo.getId())))
                .collect(Collectors.toList());
    }

    // ── CU4 Paso 3: Gestor de bloqueo de platillos ───────────────────────────
    public List<ItemMenu> bloquearPlatosAfectados(List<ItemMenu> platosAfectados) {
        List<ItemMenu> bloqueados = new ArrayList<>();
        for (ItemMenu item : platosAfectados) {
            if (item.isActivo()) {
                item.setActivo(false);
                itemMenuDao.update(item);
                bloqueados.add(item);
            }
        }
        return bloqueados;
    }

    // ── CU4 Orquestador: ejecuta ciclo completo ───────────────────────────────
    public ResultadoBloqueo ejecutarCicloDeBloqueo() {
        List<Insumo> criticos    = detectarInsumosCriticos();
        List<ItemMenu> afectados = new ArrayList<>();

        for (Insumo insumo : criticos) {
            afectados.addAll(identificarPlatosAfectados(insumo));
        }

        List<ItemMenu> sinDuplicados = afectados.stream()
                .distinct()
                .collect(Collectors.toList());

        List<ItemMenu> bloqueados = bloquearPlatosAfectados(sinDuplicados);

        return new ResultadoBloqueo(criticos, sinDuplicados, bloqueados);
    }

    // ── Lecturas de soporte ───────────────────────────────────────────────────
    public List<Insumo>   listarTodosInsumos() { return insumoDao.findAll(); }
    public List<ItemMenu> listarTodosItems()   { return itemMenuDao.findAll(); }

    // ── Demo: inicializa datos de ejemplo si la BD está vacía ─────────────────
    private void inicializarDatosDemo() {
        if (!insumoDao.findAll().isEmpty()) return;

        Insumo tomate = crearInsumo("Tomate",  2.0,  5.0, "kg");   // crítico
        Insumo queso  = crearInsumo("Queso",   8.0,  3.0, "kg");   // ok
        Insumo harina = crearInsumo("Harina",  1.5, 10.0, "kg");   // crítico
        Insumo arroz  = crearInsumo("Arroz",  15.0,  5.0, "kg");   // ok
        Insumo salmon = crearInsumo("Salmón",  0.5,  2.0, "kg");   // crítico

        dao.MarcaDao marcaDao = new dao.MarcaDaoHibernate();
        model.Marca marca = marcaDao.findAll().stream().findFirst().orElseGet(() -> {
            model.Marca m = new model.Marca();
            m.setNombre("Demo Brand");
            return marcaDao.save(m);
        });

        crearItemMenu("Margherita",     true, marca, tomate, queso);
        crearItemMenu("Pizza Hawaina",  true, marca, harina, queso);
        crearItemMenu("Sushi Salmón",   true, marca, salmon, arroz);
        crearItemMenu("Ensalada César", true, marca, tomate);
        crearItemMenu("Arroz Chaufa",   true, marca, arroz);
    }

    private Insumo crearInsumo(String nombre, double cantidad, double stockMinimo, String unidad) {
        Insumo i = new Insumo();
        i.setNombre(nombre);
        i.setCantidad(cantidad);
        i.setStockMinimo(stockMinimo);
        i.setUnidad(unidad);
        return insumoDao.save(i);
    }

    private void crearItemMenu(String nombre, boolean activo, model.Marca marca, Insumo... insumos) {
        ItemMenu item = new ItemMenu();
        item.setNombre(nombre);
        item.setActivo(activo);
        item.setMarca(marca);
        ItemMenu saved = itemMenuDao.save(item);

        for (Insumo insumo : insumos) {
            model.DetalleInsumoMenu d = new model.DetalleInsumoMenu();
            d.setInsumo(insumo);
            d.setItemMenu(saved);
            d.setCantidadRequerida(1.0);
            saved.getInsumosRequeridos().add(d);
        }
        itemMenuDao.update(saved);
    }

    // ── DTO de resultado ──────────────────────────────────────────────────────
    public static class ResultadoBloqueo {
        private final List<Insumo>   insumosCriticos;
        private final List<ItemMenu> platosAfectados;
        private final List<ItemMenu> platosBloqueados;

        public ResultadoBloqueo(List<Insumo> insumosCriticos,
                                List<ItemMenu> platosAfectados,
                                List<ItemMenu> platosBloqueados) {
            this.insumosCriticos  = insumosCriticos;
            this.platosAfectados  = platosAfectados;
            this.platosBloqueados = platosBloqueados;
        }

        public List<Insumo>   getInsumosCriticos()  { return insumosCriticos;  }
        public List<ItemMenu> getPlatosAfectados()  { return platosAfectados;  }
        public List<ItemMenu> getPlatosBloqueados() { return platosBloqueados; }
        public boolean        hayAlertaUrgente()    { return !platosBloqueados.isEmpty(); }
    }
}
