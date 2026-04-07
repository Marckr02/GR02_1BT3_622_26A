package darkkitchen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import model.EstadoPedido;
import org.junit.jupiter.api.Test;

class BaseStructureTest {

    @Test
    void enumEstadoPedidoDebeTenerValorRecibido() {
        assertEquals(EstadoPedido.RECIBIDO, EstadoPedido.valueOf("RECIBIDO"));
    }
}

