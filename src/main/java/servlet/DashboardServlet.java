package servlet;

import model.Rol;
import model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * DashboardServlet — Tarea 3 de HU8.
 *
 * Redirige al panel correcto según el rol del usuario en sesión:
 *   COCINERO      → index.jsp (tablero Kanban, CU1/CU2)
 *   ADMIN_BODEGA  → index.jsp con vista de inventario (CU3/CU4)
 *
 * Si no hay sesión activa → redirige a /login.
 */

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("usuarioActivo") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioActivo");

        // Pasa el rol a la vista para que index.jsp renderice solo lo que corresponde
        req.setAttribute("usuario", usuario);
        req.setAttribute("rol", usuario.getRol());
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
