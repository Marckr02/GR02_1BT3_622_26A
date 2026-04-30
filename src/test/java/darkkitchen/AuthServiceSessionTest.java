package darkkitchen;

import model.Rol;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;

import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceSessionTest {

    private HttpSession sessionMock;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        sessionMock = mock(HttpSession.class);
        authService = new AuthService(sessionMock);
    }

    @Test
    void given_sesion_con_usuario_when_haySesionActiva_then_retorna_true() {
        Usuario usuario = new Usuario("chef01", "hash", Rol.COCINERO);
        when(sessionMock.getAttribute(AuthService.SESSION_KEY)).thenReturn(usuario);

        assertTrue(authService.haySesionActiva());

        verify(sessionMock, times(1)).getAttribute(AuthService.SESSION_KEY);
        verifyNoMoreInteractions(sessionMock);
    }

    @Test
    void given_sesion_sin_usuario_when_haySesionActiva_then_retorna_false() {
        when(sessionMock.getAttribute(AuthService.SESSION_KEY)).thenReturn(null);

        assertFalse(authService.haySesionActiva());

        verify(sessionMock, times(1)).getAttribute(AuthService.SESSION_KEY);
        verifyNoMoreInteractions(sessionMock);
    }

    @Test
    void given_sesion_nula_when_haySesionActiva_then_retorna_false() {
        AuthService authConSesionNula = new AuthService(null);

        assertFalse(authConSesionNula.haySesionActiva());
    }

    @Test
    void given_sesion_activa_when_getUsuarioActivo_then_retorna_usuario_sin_tocar_bd() {
        Usuario usuario = new Usuario("admin01", "hash", Rol.ADMIN_BODEGA);
        when(sessionMock.getAttribute(AuthService.SESSION_KEY)).thenReturn(usuario);

        Usuario resultado = authService.getUsuarioActivo();

        assertNotNull(resultado);
        assertEquals("admin01", resultado.getUsername());
        verify(sessionMock, atLeastOnce()).getAttribute(AuthService.SESSION_KEY);
    }
}
