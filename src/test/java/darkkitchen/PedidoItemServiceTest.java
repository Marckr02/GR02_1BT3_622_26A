package darkkitchen;

import model.Insumo;
import model.ItemMenu;
import model.DetalleInsumoMenu;
import model.PedidoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.PedidoItemService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoItemServiceTest {

    PedidoItemService service;

    @BeforeEach
    void setUp() {
        service = new PedidoItemService();
        System.out.println("setUp()");
    }

    // ── Test 1: Validación al agregar platos ─────────────────────────────────

    @Test
    void given_plato_activo_con_stock_when_agregar_then_true() {
        Insumo insumo = new Insumo();
        insumo.setNombre("Arroz");
        insumo.setCantidad(10.0);

        DetalleInsumoMenu detalle = new DetalleInsumoMenu();
        detalle.setInsumo(insumo);
        detalle.setCantidadRequerida(2.0);

        ItemMenu plato = new ItemMenu();
        plato.setNombre("Sushi Roll");
        plato.setActivo(true);
        plato.getInsumosRequeridos().add(detalle);

        boolean resultado = service.agregarPlato(plato);

        assertTrue(resultado);
        System.out.println("Test 1");
    }

    @Test
    void given_plato_inactivo_when_agregar_then_false() {
        ItemMenu plato = new ItemMenu();
        plato.setNombre("Plato Bloqueado");
        plato.setActivo(false);

        boolean resultado = service.agregarPlato(plato);

        assertFalse(resultado);
        System.out.println("Test 2");
    }

    // ── Test 2: Cálculo del total del pedido ────────────────────────────────

    @Test
    void given_dos_items_when_calcularTotal_then_suma_correcta() {
        ItemMenu plato1 = new ItemMenu();
        plato1.setNombre("Hamburguesa");

        ItemMenu plato2 = new ItemMenu();
        plato2.setNombre("Papas");

        List<PedidoItem> items = new ArrayList<>();
        items.add(new PedidoItem(plato1, 2, 5.50));
        items.add(new PedidoItem(plato2, 3, 2.00));

        double total = service.calcularTotal(items);

        assertEquals(17.0, total);
        System.out.println("Test 3");
    }

    @Test
    void given_lista_vacia_when_calcularTotal_then_cero() {
        List<PedidoItem> items = new ArrayList<>();

        double total = service.calcularTotal(items);

        assertEquals(0.0, total);
        System.out.println("Test 4");
    }
}