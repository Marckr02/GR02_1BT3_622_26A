package darkkitchen;

import dao.PedidoDao;
import dao.MarcaDao;
import model.EstadoPedido;
import model.Marca;
import model.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.PedidoService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class PedidoServiceTest {

    //  Mocks (simulan la BD sin tocarla)
    PedidoDao pedidoDaoMock;
    MarcaDao  marcaDaoMock;

    // Sistema bajo prueba
    PedidoService service;

    @BeforeEach
    void setUp() {
        pedidoDaoMock = mock(PedidoDao.class);
        marcaDaoMock  = mock(MarcaDao.class);

        // Usamos el constructor con inyección para pasar los mocks
        service = new PedidoService(pedidoDaoMock, marcaDaoMock);

        System.out.println("setUp()");
    }

    @Test
    void given_pedido_recibido_when_cancelar_then_estado_cancelado() {
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoPedido.RECIBIDO);
        when(pedidoDaoMock.findById(1L)).thenReturn(Optional.of(pedido));

        service.cancelarPedido(1L);

        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
        verify(pedidoDaoMock).update(pedido);

        System.out.println("Test 1");
    }

    @Test
    void given_pedido_en_prep_when_cancelar_then_lanza_excepcion() {
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoPedido.EN_PREP);
        when(pedidoDaoMock.findById(2L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalStateException.class,
                () -> service.cancelarPedido(2L));

        verify(pedidoDaoMock, never()).update(any());

        System.out.println("Test 2");
    }

    @Test
    void given_id_inexistente_when_cancelar_then_lanza_excepcion() {
        when(pedidoDaoMock.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.cancelarPedido(99L));

        verify(pedidoDaoMock, never()).update(any());

        System.out.println("Test 3");
    }
}