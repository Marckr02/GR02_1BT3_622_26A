package darkkitchen;

import model.Insumo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import service.InsumoService;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InsumoServiceTest {

    private InsumoService service;

    @BeforeAll
    void setUp() {
        // El constructor toca DAO/BD; se inicializa una sola vez para evitar ruido en los tests.
        service = new InsumoService();
    }

    //  Test 3: Insumo con stock suficiente
    @Test
    void given_insumo_con_stock_when_validar_then_true() {
        Insumo insumo = new Insumo();
        insumo.setCantidad(10.0);

        boolean resultado = service.tieneStock(insumo);

        assertTrue(resultado);
    }

    //  Test 4: Insumo sin stock
    @Test
    void given_insumo_sin_stock_when_validar_then_false() {
        Insumo insumo = new Insumo();
        insumo.setCantidad(0.0);

        boolean resultado = service.tieneStock(insumo);

        assertFalse(resultado);
    }

}