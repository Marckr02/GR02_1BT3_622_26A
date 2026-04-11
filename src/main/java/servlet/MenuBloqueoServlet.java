package servlet;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Insumo;
import model.ItemMenu;
import service.MenuService;
import service.MenuService.ResultadoBloqueo;

/**
 * Servlet para CU4 – Bloqueo automático de menú por falta de stock.
 *
 * GET  /menu/bloqueo → muestra el estado actual del inventario y el menú.
 * POST /menu/bloqueo → ejecuta el ciclo de bloqueo automático y redirige (PRG).
 */
public class MenuBloqueoServlet extends HttpServlet {

    private MenuService menuService;

    @Override
    public void init() {
        menuService = new MenuService();
    }

    // ── GET: dashboard de inventario y menú ──────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Insumo>   todosInsumos = menuService.listarTodosInsumos();
        List<ItemMenu> todosItems   = menuService.listarTodosItems();
        List<Insumo>   criticos     = menuService.detectarInsumosCriticos();

        req.setAttribute("todosInsumos", todosInsumos);
        req.setAttribute("todosItems",   todosItems);
        req.setAttribute("criticos",     criticos);

        req.getRequestDispatcher("/WEB-INF/views/cu4-menu-bloqueo.jsp")
                .forward(req, resp);
    }

    // ── POST: ejecutar ciclo de bloqueo automático ────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            ResultadoBloqueo resultado = menuService.ejecutarCicloDeBloqueo();

            // Pasa resumen de la operación como parámetros de query (PRG)
            String msg = resultado.hayAlertaUrgente()
                    ? "bloqueados=" + resultado.getPlatosBloqueados().size()
                      + "&criticos=" + resultado.getInsumosCriticos().size()
                    : "sinCambios=1";

            resp.sendRedirect(req.getContextPath() + "/menu/bloqueo?" + msg);

        } catch (Exception e) {
            req.setAttribute("errorBloqueo", e.getMessage());
            doGet(req, resp);
        }
    }
}
