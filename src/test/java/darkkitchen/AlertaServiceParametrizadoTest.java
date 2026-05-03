package darkkitchen;

import model.NivelAlerta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.AlertaService;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas parametrizadas del TDD para AlertaService.clasificarNivel().
 *
 * Trazabilidad – Tarea 2:
 *   @ParameterizedTest con 5+ casos que cubren las tres ramas del método.
 */
class AlertaServiceParametrizadoTest {

    AlertaService service;

    @BeforeEach
    void setUp() {
        // Usamos una subclase anónima que no necesita BD para las pruebas puras
        service = new AlertaService() {
            // Constructor sin DAO: sólo probamos clasificarNivel (sin persistencia)
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Casos parametrizados para clasificarNivel
    //
    // Formato de cada argumento:
    //   stockActual, stockMinimo, nivelEsperado (null = sin alerta)
    // ─────────────────────────────────────────────────────────────────────────

    static Stream<Arguments> proveerCasosClasificacion() {
        return Stream.of(
                // 1. Stock en cero → CRITICO siempre
                Arguments.of(0.0,   10.0, NivelAlerta.CRITICO),

                // 2. Stock por debajo del 50 % del mínimo → CRITICO
                Arguments.of(4.0,   10.0, NivelAlerta.CRITICO),   // 40 % del mínimo

                // 3. Stock exactamente en el 50 % del mínimo → ADVERTENCIA
                Arguments.of(5.0,   10.0, NivelAlerta.ADVERTENCIA), // exactamente 50 %

                // 4. Stock entre 50 % y 100 % del mínimo → ADVERTENCIA
                Arguments.of(7.5,   10.0, NivelAlerta.ADVERTENCIA), // 75 % del mínimo

                // 5. Stock exactamente igual al mínimo → ADVERTENCIA (borde)
                Arguments.of(10.0,  10.0, NivelAlerta.ADVERTENCIA),

                // 6. Stock ligeramente superior al mínimo → null (sin alerta)
                Arguments.of(10.1,  10.0, null),

                // 7. Stock muy por encima del mínimo → null
                Arguments.of(100.0, 10.0, null),

                // 8. Stock mínimo configurado en cero → null (no se pueden generar alertas)
                Arguments.of(0.0,   0.0,  null)
        );
    }

    @ParameterizedTest(name = "stock={0}, minimo={1} → nivel esperado={2}")
    @MethodSource("proveerCasosClasificacion")
    void given_stock_when_clasificarNivel_then_retorna_nivel_correcto(
            double stockActual,
            double stockMinimo,
            NivelAlerta nivelEsperado) {

        NivelAlerta resultado = service.clasificarNivel(stockActual, stockMinimo);

        assertEquals(nivelEsperado, resultado,
                String.format("clasificarNivel(%.1f, %.1f) debió retornar %s",
                        stockActual, stockMinimo, nivelEsperado));

        System.out.printf("clasificarNivel(%.1f, %.1f) → %s ✓%n",
                stockActual, stockMinimo, resultado);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Pruebas unitarias adicionales (sin parámetros)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void given_stockNegativo_when_clasificarNivel_then_retorna_CRITICO() {
        // Un stock negativo (error de datos) debe tratarse como CRITICO
        NivelAlerta resultado = service.clasificarNivel(-1.0, 10.0);
        assertEquals(NivelAlerta.CRITICO, resultado,
                "Stock negativo debe clasificarse como CRITICO");
    }

    @Test
    void given_stockMinimoNegativo_when_clasificarNivel_then_retorna_null() {
        // Mínimo negativo = configuración inválida → sin alerta
        NivelAlerta resultado = service.clasificarNivel(5.0, -1.0);
        assertNull(resultado,
                "Stock mínimo negativo debe retornar null (configuración inválida)");
    }
}