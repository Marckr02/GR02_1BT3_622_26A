package darkkitchen;

import dao.UsuarioDao;
import model.Rol;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UsuarioService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UsuarioServiceTest — Pruebas Unitarias 5 y 6 del Incremento 3.
 * HU8: Autenticar usuario según su rol en el sistema.
 *
 * Se usa un mock de UsuarioDao para aislar completamente la lógica
 * de UsuarioService de la base de datos.
 *
 * FASE RED: ambos tests FALLAN con el stub (autenticar retorna null).
 * FASE GREEN: ambos tests PASAN con la implementación real.
 * FASE REFACTOR: mismos tests, lógica interna mejorada.
 */
class UsuarioServiceTest {

    // Mock: simula el DAO sin tocar la BD
    UsuarioDao usuarioDaoMock;

    // Sistema bajo prueba
    UsuarioService service;

    // Usuario de prueba preconstruido
    Usuario usuarioChef;

    @BeforeEach
    void setUp() {
        usuarioDaoMock = mock(UsuarioDao.class);
        service        = new UsuarioService(usuarioDaoMock);

        // Construir usuario con hash real de "pass123"
        String hashCorrecto = UsuarioService.hashSHA256("pass123");
        usuarioChef = new Usuario("chef01", hashCorrecto, Rol.COCINERO);

        System.out.println("setUp() — UsuarioServiceTest");
    }

    // ── Test Unitario 5 ───────────────────────────────────────────────────

    /**
     * Prueba Unitaria 5: Autenticación exitosa con credenciales correctas.
     *
     * Dado:  existe "chef01" en BD con contraseña "pass123" (hasheada).
     * Cuando: se llama a autenticar("chef01", "pass123").
     * Entonces: retorna el objeto Usuario con rol COCINERO, sin excepción.
     */
    @Test
    void given_credenciales_validas_when_autenticar_then_retorna_usuario() {
        // Arrange — el mock devuelve el usuario cuando se busca "chef01"
        when(usuarioDaoMock.findByUsername("chef01"))
                .thenReturn(Optional.of(usuarioChef));

        // Act
        Usuario resultado = service.autenticar("chef01", "pass123");

        // Assert
        assertNotNull(resultado,
                "Debe retornar un Usuario, no null");
        assertEquals("chef01", resultado.getUsername(),
                "El username debe coincidir");
        assertEquals(Rol.COCINERO, resultado.getRol(),
                "El rol debe ser COCINERO");

        // Verificar que el DAO fue consultado exactamente una vez
        verify(usuarioDaoMock, times(1)).findByUsername("chef01");

        System.out.println("Test 5 OK — autenticacion exitosa");
    }

    // ── Test Unitario 6 ───────────────────────────────────────────────────

    /**
     * Prueba Unitaria 6: Excepción al ingresar contraseña incorrecta.
     *
     * Dado:  existe "chef01" en BD con contraseña "pass123" (hasheada).
     * Cuando: se llama a autenticar("chef01", "wrongpass").
     * Entonces: lanza SecurityException con mensaje claro, sin retornar usuario.
     */
    @Test
    void given_password_incorrecta_when_autenticar_then_lanza_excepcion() {
        // Arrange — el mock devuelve el usuario (existe en BD)
        when(usuarioDaoMock.findByUsername("chef01"))
                .thenReturn(Optional.of(usuarioChef));

        // Act & Assert — debe lanzar SecurityException
        SecurityException ex = assertThrows(
                SecurityException.class,
                () -> service.autenticar("chef01", "wrongpass"),
                "Debe lanzar SecurityException con password incorrecta"
        );

        assertTrue(ex.getMessage().contains("Credenciales incorrectas"),
                "El mensaje debe indicar credenciales incorrectas");

        // El DAO fue consultado (existe el usuario) pero falló el hash
        verify(usuarioDaoMock, times(1)).findByUsername("chef01");

        System.out.println("Test 6 OK — excepcion por password incorrecta");
    }
}
