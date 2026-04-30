package servlet;

import model.Rol;
import model.Usuario;
import service.AuthService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthFilter implements Filter {

    private static final Map<String, Rol> PERMISOS_RUTA = new HashMap<>();

    static {
        PERMISOS_RUTA.put("/pedidos/recibir", Rol.COCINERO);
        PERMISOS_RUTA.put("/pedidos/kanban",  Rol.COCINERO);
        PERMISOS_RUTA.put("/insumos/entrada", Rol.ADMIN_BODEGA);
        PERMISOS_RUTA.put("/menu/bloqueo",    Rol.ADMIN_BODEGA);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        AuthService auth = new AuthService(session);

        if (!auth.haySesionActiva()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String pathInfo = req.getServletPath();
        Rol rolRequerido = PERMISOS_RUTA.get(pathInfo);

        if (rolRequerido != null && !auth.tienePermiso(rolRequerido)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permiso para acceder a esta vista.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
