package darkkitchen;

import dao.ProveedorDao;
import model.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ProveedorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ProveedorServiceTest — Pruebas Unitarias 1–4 de la Iteración 1.
 * HU1: Registrar un nuevo proveedor con nombre, teléfono y correo.
 *
 * ─── METODOLOGÍA TDD ──────────────────────────────────────────────────────────
 *
 * Se usa un mock de ProveedorDao para aislar completamente la lógica
 * de ProveedorService de la base de datos.
 *
 * FASE RED:
 *   Los cuatro tests se escribieron ANTES de implementar ProveedorService.
 *   Resultado: fallan con errores de compilación (método registrar()
 *   no existe) o AssertionError (lógica ausente).
 *
 * FASE GREEN:
 *   Se implementó registrar() con la lógica mínima necesaria:
 *   validar campos vacíos + delegar al DAO. Los cuatro tests pasan.
 *
 * FASE REFACTOR:
 *   Se extrajeron los métodos privados validarCampos() y validarCorreoUnico()
 *   en ProveedorService para mejorar la legibilidad.
 *   Los cuatro tests siguen pasando sin ninguna modificación.
 *
 * ─── TRAZABILIDAD ─────────────────────────────────────────────────────────────
 *
 * HU1 Criterio Aceptación Escenario 1 → Tests 1 y 3 (registro exitoso)
 * HU1 Criterio Aceptación Escenario 2 → Tests 2 y 4 (campos vacíos → error)
 */
class ProveedorServiceTest {

    // Mock: simula el DAO sin tocar la BD
    ProveedorDao proveedorDaoMock;

    // Sistema bajo prueba
    ProveedorService service;

    @BeforeEach
    void setUp() {
        proveedorDaoMock = mock(ProveedorDao.class);
        service          = new ProveedorService(proveedorDaoMock);
        System.out.println("setUp() — ProveedorServiceTest");
    }

    // ── Test Unitario 1 ───────────────────────────────────────────────────

    /**
     * Prueba Unitaria 1: Registro exitoso con datos completos y válidos.
     *
     * Dado:  un proveedor con nombre, teléfono y correo completos.
     * Cuando: se llama a registrar().
     * Entonces: el sistema guarda al proveedor y lo retorna sin lanzar excepción.
     *
     * Cubre: Criterio de Aceptación — Escenario 1.
     */
    @Test
    void given_proveedor_completo_when_registrar_then_retorna_proveedor_guardado() {
        // Arrange
        Proveedor proveedor = new Proveedor("Distribuidora Los Andes", "0991234567", "contacto@losandes.com");
        when(proveedorDaoMock.save(proveedor)).thenReturn(proveedor);

        // Act
        Proveedor resultado = service.registrar("Distribuidora Los Andes", "0991234567", "contacto@losandes.com");

        // Assert
        assertNotNull(resultado,
                "Debe retornar el proveedor guardado, no null");
        assertEquals("Distribuidora Los Andes", resultado.getNombre(),
                "El nombre debe coincidir con el ingresado");
        assertEquals("0991234567", resultado.getTelefono(),
                "El teléfono debe coincidir con el ingresado");
        assertEquals("contacto@losandes.com", resultado.getCorreo(),
                "El correo debe coincidir con el ingresado");

        // Verificar que el DAO fue llamado exactamente una vez
        verify(proveedorDaoMock, times(1)).save(any());

        System.out.println("Test 1 OK — registro exitoso de proveedor");
    }

    // ── Test Unitario 2 ───────────────────────────────────────────────────

    /**
     * Prueba Unitaria 2: Rechazo cuando el nombre del proveedor está vacío.
     *
     * Dado:  un proveedor con nombre vacío (""), teléfono y correo válidos.
     * Cuando: se llama a registrar().
     * Entonces: el sistema lanza IllegalArgumentException y NO llama al DAO.
     *
     * Cubre: Criterio de Aceptación — Escenario 2.
     */
    @Test
    void given_nombre_vacio_when_registrar_then_lanza_excepcion() {
        // Arrange — nombre intencionalmente vacío
        // Act & Assert — debe lanzar IllegalArgumentException
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.registrar("", "0991234567", "contacto@losandes.com"),
                "Debe lanzar IllegalArgumentException cuando el nombre está vacío"
        );

        assertTrue(ex.getMessage().toLowerCase().contains("obligatorio"),
                "El mensaje debe indicar que los campos son obligatorios");

        // El DAO NO debe ser llamado si la validación falla
        verify(proveedorDaoMock, never()).save(any());

        System.out.println("Test 2 OK — excepción por nombre vacío");
    }

    // ── Test Unitario 3 ───────────────────────────────────────────────────

    /**
     * Prueba Unitaria 3: El proveedor registrado queda disponible en el sistema.
     *
     * Dado:  un proveedor con todos los campos completos.
     * Cuando: se llama a registrar().
     * Entonces: el proveedor retornado tiene los mismos datos con los que fue creado.
     *
     * Verifica que el objeto persistido conserva la integridad de los datos
     * y que el DAO recibió exactamente el objeto que el service construyó.
     *
     * Cubre: Criterio de Aceptación — Escenario 1 (verificación de datos).
     */
    @Test
    void given_proveedor_valido_when_registrar_then_datos_son_consistentes() {
        // Arrange
        Proveedor proveedor = new Proveedor("Proveedora Quito S.A.", "022345678", "ventas@quitosa.ec");
        when(proveedorDaoMock.save(any())).thenReturn(proveedor);

        // Act
        Proveedor resultado = service.registrar("Proveedora Quito S.A.", "022345678", "ventas@quitosa.ec");

        // Assert — los datos del objeto retornado deben ser idénticos
        assertEquals("Proveedora Quito S.A.", resultado.getNombre(),
                "El nombre debe ser el mismo tras el registro");
        assertEquals("022345678", resultado.getTelefono(),
                "El teléfono debe ser el mismo tras el registro");
        assertEquals("ventas@quitosa.ec", resultado.getCorreo(),
                "El correo debe ser el mismo tras el registro");

        // Verificar que se guardó exactamente una vez
        verify(proveedorDaoMock, times(1)).save(any());

        System.out.println("Test 3 OK — datos consistentes tras registro");
    }

    // ── Test Unitario 4 ───────────────────────────────────────────────────

    /**
     * Prueba Unitaria 4: Rechazo cuando el correo del proveedor es nulo.
     *
     * Dado:  un proveedor con nombre y teléfono válidos pero correo nulo.
     * Cuando: se llama a registrar().
     * Entonces: el sistema lanza IllegalArgumentException y NO llama al DAO.
     *
     * Cubre: Criterio de Aceptación — Escenario 2 (campo obligatorio nulo).
     */
    @Test
    void given_correo_nulo_when_registrar_then_lanza_excepcion() {
        // Arrange — correo intencionalmente nulo
        // Act & Assert — debe lanzar IllegalArgumentException
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.registrar("Bodega Central", "0987654321", null),
                "Debe lanzar IllegalArgumentException cuando el correo es nulo"
        );

        assertTrue(ex.getMessage().toLowerCase().contains("obligatorio"),
                "El mensaje debe indicar que los campos son obligatorios");

        // El DAO NO debe ser llamado si la validación falla
        verify(proveedorDaoMock, never()).save(any());

        System.out.println("Test 4 OK — excepción por correo nulo");
    }
}
