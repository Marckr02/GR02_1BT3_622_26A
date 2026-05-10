package servlet;

import model.Proveedor;
import service.ProveedorService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet de frontera para la gestión de proveedores.
 *
 * Trazabilidad – TAREA 3.1 HU3 (Álvaro)
 *
 * GET  /proveedores/lista  → lista todos los proveedores
 * POST /proveedores/lista  → registra un nuevo proveedor
 *
 * Refactorización: la lógica de forward se extrae al método privado
 * despacharVista() para evitar duplicación entre doGet y el bloque POST.
 *
 * CORRECCIÓN 1: se eliminó @WebServlet para evitar conflicto con web.xml.
 * CORRECCIÓN 2: se mantiene la vista de HU3 cu5-proveedores.jsp.
 */
public class ProveedorServlet extends HttpServlet {

    private ProveedorService proveedorService;

    @Override
    public void init() throws ServletException {
        proveedorService = new ProveedorService();
    }

    // ── GET: listar proveedores ───────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Proveedor> proveedores = proveedorService.listar();
        req.setAttribute("proveedores", proveedores);
        despacharVista(req, resp);
    }

    // ── POST: registrar proveedor ─────────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String nombre   = req.getParameter("nombre");
        String telefono = req.getParameter("telefono");
        String correo   = req.getParameter("correo");

        try {
            proveedorService.registrar(nombre, telefono, correo);
            req.setAttribute("mensaje", "Proveedor registrado correctamente.");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
        }

        List<Proveedor> proveedores = proveedorService.listar();
        req.setAttribute("proveedores", proveedores);
        despacharVista(req, resp);
    }

    // ── Auxiliar (refactorización) ────────────────────────────────────────────

    /**
     * Centraliza el forward a la vista JSP.
     * Evita duplicar la ruta de la vista en doGet y doPost.
     */
    private void despacharVista(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/cu5-proveedores.jsp")
                .forward(req, resp);
    }
}
