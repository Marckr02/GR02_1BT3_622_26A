package darkkitchen;

import model.Rol;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;

import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RolPermisoTest {

    private HttpSession sessionMock;

    @BeforeEach
    void setUp() {
        sessionMock = mock(HttpSession.class);
    }

    @Test
    void given_rol_cocinero_when_tienePermiso_CU1_then_retorna_true() {
        Usuario cocinero = new Usuario("chef01", "hash", Rol.COCINERO);
        when(sessionMock.getAttribute(AuthService.SESSION_KEY)).thenReturn(cocinero);
        AuthService auth = new AuthService(sessionMock);

        assertTrue(auth.tienePermiso(Rol.COCINERO),
                "COCINERO debe tener permiso sobre CU1 (rol COCINERO)");
    }

    @Test
    void given_rol_cocinero_when_tienePermiso_CU3_then_retorna_false() {
        Usuario cocinero = new Usuario("chef01", "hash", Rol.COCINERO);
        when(sessionMock.getAttribute(AuthService.SESSION_KEY)).thenReturn(cocinero);
        AuthService auth = new AuthService(sessionMock);

        assertFalse(auth.tienePermiso(Rol.ADMIN_BODEGA),
                "COCINERO NO debe tener permiso sobre CU3 (rol ADMIN_BODEGA)");
    }

    @Test
    void given_rol_admin_bodega_when_tienePermiso_CU3_then_retorna_true() {
        Usuario admin = new Usuario("admin01", "hash", Rol.ADMIN_BODEGA);
        when(sessionMock.getAttribute(AuthService.SESSION_KEY)).thenReturn(admin);
        AuthService auth = new AuthService(sessionMock);

        assertTrue(auth.tienePermiso(Rol.ADMIN_BODEGA),
                "ADMIN_BODEGA debe tener permiso sobre CU3 (rol ADMIN_BODEGA)");
    }

    @Test
    void given_rol_admin_bodega_when_tienePermiso_CU1_then_retorna_false() {
        Usuario admin = new Usuario("admin01", "hash", Rol.ADMIN_BODEGA);
        when(sessionMock.getAttribute(AuthService.SESSION_KEY)).thenReturn(admin);
        AuthService auth = new AuthService(sessionMock);

        assertFalse(auth.tienePermiso(Rol.COCINERO),
                "ADMIN_BODEGA NO debe tener permiso sobre CU1 (rol COCINERO)");
    }

    @Test
    void given_sin_sesion_when_tienePermiso_then_retorna_false() {
        AuthService auth = new AuthService(null);

        assertFalse(auth.tienePermiso(Rol.COCINERO));
        assertFalse(auth.tienePermiso(Rol.ADMIN_BODEGA));
    }
}
