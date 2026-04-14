package service;

import dao.InsumoDao;
import dao.InsumoDaoHibernate;
import dao.ItemMenuDao;
import dao.ItemMenuDaoHibernate;
import dao.MarcaDao;
import dao.MarcaDaoHibernate;
import model.DetalleInsumoMenu;
import model.Insumo;
import model.ItemMenu;
import model.Marca;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para el CU4 - Bloqueo automatico de menu por falta de stock.
 *
 * Este servicio es invocado automaticamente por InsumoService cada vez que
 * el stock cambia (sumarStock o reducirStock). No requiere interaccion manual.
 *
 * Logica de bloqueo:
 *   Un plato se BLOQUEA si al menos uno de sus insumos tiene stock < cantidadRequerida.
 *   Un plato se REACTIVA si TODOS sus insumos tienen stock >= cantidadRequerida.
 *
 * Esto garantiza consistencia en ambas direcciones: reducir bloquea, reponer reactiva.
 */
public class MenuService {

    private final InsumoDao   insumoDao;
    private final ItemMenuDao itemMenuDao;
    private final MarcaDao    marcaDao;

    public MenuService() {
        this.insumoDao   = new InsumoDaoHibernate();
        this.itemMenuDao = new ItemMenuDaoHibernate();
        this.marcaDao    = new MarcaDaoHibernate();
        inicializarPlatosDemo();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Punto de entrada: llamado por InsumoService tras cualquier cambio de stock
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Evalua todos los platos del menu y actualiza su disponibilidad segun el
     * stock actual de sus insumos requeridos. Bloquea los que no pueden prepararse
     * y reactiva los que vuelven a tener stock suficiente.
     *
     * Este metodo es el nucleo del CU4 y debe llamarse despues de cualquier
     * modificacion de stock en InsumoService.
     */
    public void sincronizarDisponibilidadMenu() {
        List<ItemMenu> todosLosPlatos = itemMenuDao.findAllWithInsumos();

        for (ItemMenu plato : todosLosPlatos) {
            boolean puedePrepararseAhora = plato.puedePrepararse();

            if (!puedePrepararseAhora && plato.isActivo()) {
                // Bloquear: le falta al menos un insumo
                plato.setActivo(false);
                itemMenuDao.update(plato);

            } else if (puedePrepararseAhora && !plato.isActivo()) {
                // Reactivar: todos los insumos tienen stock suficiente
                plato.setActivo(true);
                itemMenuDao.update(plato);
            }
            // Si el estado no cambio, no hacer nada
        }
    }

    /**
     * Para cada plato bloqueado, devuelve cuales insumos son la causa.
     * Usado en la vista para mostrar el motivo de cada bloqueo.
     *
     * Delegado a ItemMenu.obtenerMotivosBloqueo() (Move Method Refactoring).
     */
    public List<String> obtenerMotivosBloqueo(ItemMenu plato) {
        return plato.obtenerMotivosBloqueo();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lecturas para la vista del CU4
    // ─────────────────────────────────────────────────────────────────────────

    public List<ItemMenu> listarTodosItems()   { return itemMenuDao.findAllWithInsumos(); }
    public List<Insumo>   listarTodosInsumos() { return insumoDao.findAll(); }

    /**
     * Devuelve los insumos que estan por debajo de su stockMinimo.
     * Usado en la vista para mostrar alertas informativas.
     */
    public List<Insumo> detectarInsumosCriticos() {
        return insumoDao.findAll().stream()
                .filter(i -> i.getCantidad() <= i.getStockMinimo())
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Demo: platos con cantidades requeridas reales vinculados a los insumos CU3
    // ─────────────────────────────────────────────────────────────────────────

    private void inicializarPlatosDemo() {
        if (!itemMenuDao.findAll().isEmpty()) return;

        List<Insumo> insumos = insumoDao.findAll();
        if (insumos.isEmpty()) return;

        Marca marca = marcaDao.findAll().stream().findFirst().orElseGet(() -> {
            Marca m = new Marca();
            m.setNombre("Demo Brand");
            return marcaDao.save(m);
        });

        // Crear platos con cantidades requeridas reales (no 1.0 fijo)
        // Esto permite que el bloqueo funcione correctamente al reducir stock
        for (int i = 0; i + 1 < insumos.size() && i < 8; i += 2) {
            Insumo ins1 = insumos.get(i);
            Insumo ins2 = insumos.get(i + 1);

            // Requerir el 40% del stock actual como cantidad por porcion
            double req1 = Math.max(0.5, Math.round(ins1.getCantidad() * 0.4 * 10.0) / 10.0);
            double req2 = Math.max(0.5, Math.round(ins2.getCantidad() * 0.4 * 10.0) / 10.0);

            crearPlato("Plato " + (char)('A' + i / 2), marca,
                    new double[]{req1, req2}, ins1, ins2);
        }
    }

    private void crearPlato(String nombre, Marca marca, double[] cantidades, Insumo... insumos) {
        ItemMenu item = new ItemMenu();
        item.setNombre(nombre);
        item.setActivo(true);
        item.setMarca(marca);
        ItemMenu saved = itemMenuDao.save(item);

        for (int i = 0; i < insumos.length; i++) {
            DetalleInsumoMenu d = new DetalleInsumoMenu();
            d.setInsumo(insumos[i]);
            d.setItemMenu(saved);
            d.setCantidadRequerida(cantidades[i]);
            saved.getInsumosRequeridos().add(d);
        }
        itemMenuDao.update(saved);
    }
}