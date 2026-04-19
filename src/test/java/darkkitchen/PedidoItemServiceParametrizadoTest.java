package darkkitchen;

import model.ItemMenu;
import model.PedidoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.PedidoItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PedidoItemServiceParametrizadoTest {

    PedidoItemService service;

    @BeforeEach
    void setUp() {
        service = new PedidoItemService();
        System.out.println("setUp()");
    }

    static Stream<Arguments> proveerCasosDescuento() {
        return Stream.of(
                Arguments.of(0.0,  20.0, 20.0),   // 0% descuento → total sin cambio
                Arguments.of(10.0, 20.0, 18.0),   // 10% de descuento
                Arguments.of(25.0, 20.0, 15.0),   // 25% de descuento
                Arguments.of(50.0, 20.0, 10.0),   // 50% de descuento
                Arguments.of(100.0,20.0,  0.0)    // 100% descuento → gratis
        );
    }

    @ParameterizedTest(name = "Descuento {0}% sobre $20 → ${2}")
    @MethodSource("proveerCasosDescuento")
    void given_items_when_aplicarDescuento_then_total_correcto(
            double porcentaje,
            double totalEsperadoBase,
            double totalEsperadoConDescuento) {

        ItemMenu hamburguesa = new ItemMenu();
        hamburguesa.setNombre("Hamburguesa");

        ItemMenu papas = new ItemMenu();
        papas.setNombre("Papas");

        List<PedidoItem> items = new ArrayList<>();
        items.add(new PedidoItem(hamburguesa, 2, 5.0));
        items.add(new PedidoItem(papas, 2, 5.0));

        assertEquals(totalEsperadoBase, service.calcularTotal(items),
                "El total base debe ser $" + totalEsperadoBase);

        double resultado = service.aplicarDescuento(items, porcentaje);

        assertEquals(totalEsperadoConDescuento, resultado, 0.001,
                "Con " + porcentaje + "% de descuento el total debe ser $"
                        + totalEsperadoConDescuento);

        System.out.println("Descuento " + porcentaje + "% → $" + resultado);
    }

    @Test
    void given_porcentaje_invalido_when_aplicarDescuento_then_lanza_excepcion() {
        List<PedidoItem> items = new ArrayList<>();
        ItemMenu plato = new ItemMenu();
        plato.setNombre("Sushi");
        items.add(new PedidoItem(plato, 1, 10.0));

        assertThrows(IllegalArgumentException.class,
                () -> service.aplicarDescuento(items, 110.0));

        assertThrows(IllegalArgumentException.class,
                () -> service.aplicarDescuento(items, -5.0));

        System.out.println("Test excepción porcentaje inválido OK");
    }
}