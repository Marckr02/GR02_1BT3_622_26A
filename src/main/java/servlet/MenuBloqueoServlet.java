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

/**
 * Servlet para CU4 - Monitor de disponibilidad del menu.
 *
 * Solo GET: muestra el estado actual del menu y del inventario.
 * El bloqueo/reactivacion ocurre automaticamente en InsumoService
 * cada vez que el stock cambia (CU3). Este servlet es unicamente
 * una vista de monitoreo, no realiza acciones.
 */
public class MenuBloqueoServlet extends HttpServlet {

    private MenuService menuService;

    @Override
    public void init() {
        menuService = new MenuService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<ItemMenu> todosItems   = menuService.listarTodosItems();
        List<Insumo>   todosInsumos = menuService.listarTodosInsumos();
        List<Insumo>   criticos     = menuService.detectarInsumosCriticos();

        req.setAttribute("todosItems",   todosItems);
        req.setAttribute("todosInsumos", todosInsumos);
        req.setAttribute("criticos",     criticos);
        req.setAttribute("menuService",  menuService);

        req.getRequestDispatcher("/WEB-INF/views/cu4-menu-bloqueo.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // No hay acciones manuales en el CU4
        doGet(req, resp);
    }
}
