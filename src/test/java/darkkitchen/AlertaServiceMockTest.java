package darkkitchen;

import dao.AlertaDaoHibernate;
import model.Insumo;
import model.AlertaStock;
import model.NivelAlerta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.AlertaService;

import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * Pruebas con mocks para AlertaService.
 *
 * Trazabilidad – TAREA 3.4 HU3 (Jeremy)
 *
 * Mock 1: generarSiCritico() llama exactamente una vez a alertaDao.save()
 *         cuando el nivel es CRITICO y no existe alerta previa.
 *
 * Mock 2: resolverSiActiva() llama exactamente una vez a alertaDao.update()
 *         cuando existe una alerta activa y el stock supera el mínimo,
 *         y NO llama a update() cuando el stock sigue por debajo.
 *
 * Refactorización: setUp() centraliza la creación del mock y del service
 * para evitar duplicación en cada método de test.
 */
class AlertaServiceMockTest {

    AlertaDaoHibernate alertaDaoMock;
    AlertaService alertaService;

    // ── Setup (refactorización: centralizado) ─────────────────────────────────

    @BeforeEach
    void setUp() {
        alertaDaoMock = Mockito.mock(AlertaDaoHibernate.class);
        alertaService = new AlertaService(alertaDaoMock);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Insumo crearInsumo(double cantidad, double stockMinimo) {
        Insumo insumo = new Insumo();
        insumo.setNombre("Insumo Test");
        insumo.setCantidad(cantidad);
        insumo.setStockMinimo(stockMinimo);
        return insumo;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // MOCK 1: generarSiCritico()
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Dado un insumo con stock CRITICO y sin alerta previa,
     * cuando se llama a generarSiCritico(),
     * entonces el DAO debe recibir exactamente una llamada a save().
     */
    @Test
    void given_stockCritico_sinAlertaPrevia_when_generarSiCritico_then_saveEsLlamadoUnaVez() {
        // Arrange
        Insumo insumo = crearInsumo(1.0, 10.0); // 1.0 < 50% de 10.0 → CRITICO
        when(alertaDaoMock.findActivaByInsumo(insumo)).thenReturn(Optional.empty());

        // Act
        alertaService.generarSiCritico(insumo);

        // Assert
        verify(alertaDaoMock, times(1)).save(any(AlertaStock.class));
        verify(alertaDaoMock, never()).update(any(AlertaStock.class));

        System.out.println("generarSiCritico() con stock CRITICO → save() llamado 1 vez ✓");
    }

    /**
     * Dado un insumo con stock suficiente (sobre el mínimo),
     * cuando se llama a generarSiCritico(),
     * entonces el DAO NO debe recibir ninguna llamada a save().
     */
    @Test
    void given_stockSuficiente_when_generarSiCritico_then_saveNoEsLlamado() {
        // Arrange
        Insumo insumo = crearInsumo(15.0, 10.0); // 15.0 > 10.0 → sin alerta

        // Act
        alertaService.generarSiCritico(insumo);

        // Assert
        verify(alertaDaoMock, never()).save(any(AlertaStock.class));

        System.out.println("generarSiCritico() con stock suficiente → save() no llamado ✓");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // MOCK 2: resolverSiActiva()
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Dado un insumo con stock que supera el mínimo y una alerta activa existente,
     * cuando se llama a resolverSiActiva(),
     * entonces el DAO debe recibir exactamente una llamada a update().
     */
    @Test
    void given_stockSuperaMinimoYAlertaActiva_when_resolverSiActiva_then_updateEsLlamadoUnaVez() {
        // Arrange
        Insumo insumo = crearInsumo(15.0, 10.0); // stock > mínimo → resolver
        AlertaStock alertaActiva = new AlertaStock(insumo, NivelAlerta.CRITICO);
        when(alertaDaoMock.findActivaByInsumo(insumo)).thenReturn(Optional.of(alertaActiva));

        // Act
        alertaService.resolverSiActiva(insumo);

        // Assert
        verify(alertaDaoMock, times(1)).update(alertaActiva);

        System.out.println("resolverSiActiva() con stock sobre mínimo → update() llamado 1 vez ✓");
    }

    /**
     * Dado un insumo con stock por debajo del mínimo,
     * cuando se llama a resolverSiActiva(),
     * entonces el DAO NO debe recibir ninguna llamada a update().
     */
    @Test
    void given_stockBajoMinimo_when_resolverSiActiva_then_updateNoEsLlamado() {
        // Arrange
        Insumo insumo = crearInsumo(5.0, 10.0); // stock <= mínimo → no resolver

        // Act
        alertaService.resolverSiActiva(insumo);

        // Assert
        verify(alertaDaoMock, never()).update(any(AlertaStock.class));

        System.out.println("resolverSiActiva() con stock bajo mínimo → update() no llamado ✓");
    }
}