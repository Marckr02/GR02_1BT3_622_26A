package servlet;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Pedido;
import service.PedidoService;

public class PedidoKanbanServlet extends HttpServlet {

    private PedidoService pedidoService;

    @Override
    public void init() {
        this.pedidoService = new PedidoService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Pedido> pedidos = pedidoService.listarTodos();
        req.setAttribute("pedidos", pedidos);
        req.getRequestDispatcher("/WEB-INF/views/cu2-pedidos-kanban.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pedidoIdParam = req.getParameter("pedidoId");

        try {
            Long pedidoId = Long.parseLong(pedidoIdParam);
            pedidoService.avanzarEstado(pedidoId);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID de pedido inválido.");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
        }

        // Patrón PRG: redirigir al GET para refrescar el tablero
        resp.sendRedirect(req.getContextPath() + "/pedidos/kanban");
    }
}