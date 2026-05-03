package servlet;

import model.Usuario;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utilidad para manejo de sesiones en servlets.
 * Encapsula la lógica de obtener usuario activo y verificar sesión.
 */
public class SessionUtil {

    private static final String SESSION_KEY = "usuarioActivo";

    /**
     * Obtiene el usuario activo de la sesión actual.
     * @param request La solicitud HTTP.
     * @return El usuario activo, o null si no hay sesión o usuario.
     */
    public static Usuario getUsuarioActivo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (Usuario) session.getAttribute(SESSION_KEY);
    }

    /**
     * Verifica si hay una sesión activa con usuario autenticado.
     * @param request La solicitud HTTP.
     * @return true si hay sesión activa, false en caso contrario.
     */
    public static boolean haySesionActiva(HttpServletRequest request) {
        return getUsuarioActivo(request) != null;
    }
}
