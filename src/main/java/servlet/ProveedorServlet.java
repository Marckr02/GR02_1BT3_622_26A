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
 * ProveedorServlet — Boundary del módulo de Gestión de Proveedores.
 *
 * ─── TRAZABILIDAD ─────────────────────────────────────────────────────────────
 *
 * Diagrama de Actividades (HU1):
 *   GET  /proveedores  → muestra el formulario de registro y el listado de proveedores.
 *   POST /proveedores  → valida campos y registra el nuevo proveedor vía ProveedorService.
 *
 * Diagrama de Robustez (HU1):
 *   Este servlet actúa como "Interfaz de Registro de Proveedor" (Boundary)
 *   que interactúa con el control "ProveedorService" y con la entidad "Proveedor".
 *
 * Criterio de Aceptación:
 *   Escenario 1 — campos completos  : redirige a GET con mensaje de éxito.
 *   Escenario 2 — campos incompletos: reenvía la vista con mensaje de error.
 *
 * Tarea T1.3 — HU1, Iteración 1
 * Rol: ADMIN_BODEGA (protegido por AuthFilter + web.xml)
 */
public class ProveedorServlet extends HttpServlet {

    private ProveedorService proveedorService;

    @Override
    public void init() {
        proveedorService = new ProveedorService();
    }

    // ── GET: mostrar formulario de registro + listado ─────────────────────

    /**
     * Diagrama de Actividades → paso "mostrar formulario y listado de proveedores".
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Proveedor> proveedores = proveedorService.listarProveedores();
        req.setAttribute("proveedores", proveedores);

        req.getRequestDispatcher("/WEB-INF/views/cu5-proveedores.jsp")
                .forward(req, resp);
    }

    // ── POST: registrar nuevo proveedor ──────────────────────────────────

    /**
     * Diagrama de Actividades → paso "administrador presiona Guardar".
     *
     * Flujo:
     *   1. Leer parámetros del formulario.
     *   2. Llamar a ProveedorService.registrarProveedor().
     *   3a. Éxito → redirigir a GET con param "registrado=ok" (PRG pattern).
     *   3b. Error → reenviar la vista con atributo "error" y mensaje.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String nombre   = req.getParameter("nombre");
        String telefono = req.getParameter("telefono");
        String correo   = req.getParameter("correo");

        try {
            Proveedor nuevo = new Proveedor(nombre, telefono, correo);
            proveedorService.registrarProveedor(nuevo);

            // Escenario 1: registro exitoso — PRG para evitar reenvío del form
            resp.sendRedirect(req.getContextPath() + "/proveedores?registrado=ok");

        } catch (IllegalArgumentException e) {
            // Escenario 2: campos vacíos — mostrar error en la misma vista
            req.setAttribute("error", e.getMessage());
            req.setAttribute("nombre",   nombre);
            req.setAttribute("telefono", telefono);
            req.setAttribute("correo",   correo);

            List<Proveedor> proveedores = proveedorService.listarProveedores();
            req.setAttribute("proveedores", proveedores);

            req.getRequestDispatcher("/WEB-INF/views/cu5-proveedores.jsp")
                    .forward(req, resp);
        }
    }
}
