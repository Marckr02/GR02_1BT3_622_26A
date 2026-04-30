package service;

import model.Rol;
import model.Usuario;

import javax.servlet.http.HttpSession;

public class AuthService {

    public static final String SESSION_KEY = "usuarioActivo";

    private final HttpSession session;

    public AuthService(HttpSession session) {
        this.session = session;
    }

    public boolean haySesionActiva() {
        if (session == null) return false;
        return session.getAttribute(SESSION_KEY) != null;
    }

    public Usuario getUsuarioActivo() {
        if (!haySesionActiva()) return null;
        return (Usuario) session.getAttribute(SESSION_KEY);
    }

    public Rol getRolActivo() {
        Usuario u = getUsuarioActivo();
        if (u == null) return null;
        return u.getRol();
    }

    public boolean tienePermiso(Rol rolRequerido) {
        Rol rolActual = getRolActivo();
        if (rolActual == null) return false;
        return rolActual == rolRequerido;
    }
}
