package servlet;

import model.Usuario;
import service.UsuarioService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LoginServlet — Tarea 3 de HU8 (Diagrama de Robustez: Control).
 *
 * GET  /login → reenvía a login.jsp (Frontera de entrada).
 * POST /login → llama a UsuarioService.autenticar():
 *   - Éxito: guarda Usuario en HttpSession, redirige según rol.
 *   - Fallo: coloca mensaje de error en request, vuelve a login.jsp.
 *
 * Trazabilidad Diagrama de Robustez HU8:
 *   Actor (Cocinero/Admin) → [login.jsp] → [LoginServlet] → [UsuarioService] → [Usuario]
 */

public class LoginServlet extends HttpServlet {

    private UsuarioService usuarioService;

    @Override
    public void init() {
        this.usuarioService = new UsuarioService();
    }

    /**
     * GET — muestra el formulario de login.
     * Si ya hay sesión activa, redirige directo al dashboard.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuarioActivo") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    /**
     * POST — procesa credenciales.
     * Llama a UsuarioService.autenticar() y gestiona éxito/fallo.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            // Control → llama al servicio (autenticar implementado con TDD)
            Usuario usuario = usuarioService.autenticar(username, password);

            // Éxito: crear sesión y guardar usuario autenticado
            HttpSession session = req.getSession(true);
            session.setAttribute("usuarioActivo", usuario);
            session.setAttribute("rolActivo", usuario.getRol().name());

            // Redirigir según rol
            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (SecurityException e) {
            // Fallo: volver al formulario con mensaje de error
            req.setAttribute("mensajeError", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
