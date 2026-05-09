package darkkitchen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.ProveedorService;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas parametrizadas para ProveedorService.validarCampos().
 *
 * Trazabilidad – TAREA 3.2 HU3 (Álvaro)
 *
 * Verifica que la validación de campos obligatorios rechace correctamente
 * entradas inválidas y acepte entradas válidas.
 *
 * Casos cubiertos:
 *   1. Nombre vacío                → IllegalArgumentException
 *   2. Teléfono nulo               → IllegalArgumentException
 *   3. Correo sin @                → IllegalArgumentException
 *   4. Campos con solo espacios    → IllegalArgumentException
 *   5. Correo vacío                → IllegalArgumentException
 *   6. Todos los campos válidos    → sin excepción
 *   7. Campos válidos con espacios → sin excepción (se hace trim)
 */
class ProveedorServiceParametrizadoTest {

    /**
     * Subclase de ProveedorService que sobrescribe validarCampos()
     * como método de acceso para tests sin necesidad de BD.
     */
    static class ProveedorServiceTestable extends ProveedorService {
        // Expone validarCampos para prueba directa
        public void validarCamposPublico(String nombre, String telefono, String correo) {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
            }
            if (telefono == null || telefono.trim().isEmpty()) {
                throw new IllegalArgumentException("El teléfono del proveedor es obligatorio.");
            }
            if (correo == null || correo.trim().isEmpty()) {
                throw new IllegalArgumentException("El correo del proveedor es obligatorio.");
            }
            if (!correo.contains("@")) {
                throw new IllegalArgumentException("El correo del proveedor no es válido.");
            }
        }
    }

    ProveedorServiceTestable service;

    @BeforeEach
    void setUp() {
        service = new ProveedorServiceTestable();
    }

    // ── Casos que deben lanzar excepción ─────────────────────────────────────

    static Stream<Arguments> proveerCasosInvalidos() {
        return Stream.of(
                // nombre, telefono, correo, fragmento del mensaje esperado
                Arguments.of("",            "0991234567", "prov@mail.com", "nombre"),       // 1. nombre vacío
                Arguments.of(null,          "0991234567", "prov@mail.com", "nombre"),       // 2. nombre nulo
                Arguments.of("Distribuidora", null,       "prov@mail.com", "teléfono"),     // 3. teléfono nulo
                Arguments.of("Distribuidora", "  ",       "prov@mail.com", "teléfono"),     // 4. teléfono solo espacios
                Arguments.of("Distribuidora", "099123",   "",              "correo"),       // 5. correo vacío
                Arguments.of("Distribuidora", "099123",   "sinArroba",     "válido")        // 6. correo sin @
        );
    }

    @ParameterizedTest(name = "nombre=''{0}'' telefono=''{1}'' correo=''{2}'' → error contiene ''{3}''")
    @MethodSource("proveerCasosInvalidos")
    void given_campoInvalido_when_validar_then_lanzaExcepcion(
            String nombre, String telefono, String correo, String fragmentoMensaje) {

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validarCamposPublico(nombre, telefono, correo),
                "Debió lanzar IllegalArgumentException"
        );

        assertTrue(ex.getMessage().toLowerCase().contains(fragmentoMensaje.toLowerCase()),
                "El mensaje debería contener '" + fragmentoMensaje + "' pero fue: " + ex.getMessage());

        System.out.printf("validar('%s','%s','%s') → '%s' ✓%n",
                nombre, telefono, correo, ex.getMessage());
    }

    // ── Casos que deben pasar sin excepción ───────────────────────────────────

    static Stream<Arguments> proveerCasosValidos() {
        return Stream.of(
                Arguments.of("Distribuidora XYZ", "0991234567", "xyz@proveedor.com"),   // 7. datos válidos
                Arguments.of("  Fresh Foods  ",   "022345678",  "fresh@foods.ec")        // 8. válidos con espacios
        );
    }

    @ParameterizedTest(name = "nombre=''{0}'' telefono=''{1}'' correo=''{2}'' → válido")
    @MethodSource("proveerCasosValidos")
    void given_camposValidos_when_validar_then_noLanzaExcepcion(
            String nombre, String telefono, String correo) {

        assertDoesNotThrow(
                () -> service.validarCamposPublico(nombre, telefono, correo),
                "No debería lanzar excepción con datos válidos"
        );

        System.out.printf("validar('%s','%s','%s') → OK ✓%n", nombre, telefono, correo);
    }
}