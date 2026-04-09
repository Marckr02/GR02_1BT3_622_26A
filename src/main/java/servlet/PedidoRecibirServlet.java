package servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Pedido;
import service.PedidoService;

import java.io.IOException;
import java.util.List;

/**
 * Servlet para CU1 – Recibir pedido de plataforma externa.
 *
 * GET  /pedidos/recibir  → muestra el formulario de recepción simulada
 *                          y la lista de pedidos ya recibidos.
 * POST /pedidos/recibir  → procesa el formulario, invoca PedidoService.recibirPedido()
 *                          y redirige al GET (PRG pattern).
 */
public class PedidoRecibirServlet extends HttpServlet {

    private PedidoService pedidoService;

    @Override
    public void init() {
        pedidoService = new PedidoService();
    }

    // ── GET: mostrar formulario + lista de pedidos recibidos ─────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Pedidos en estado RECIBIDO ordenados por prioridad
        List<Pedido> pedidos = pedidoService.listarPedidosRecibidos();
        req.setAttribute("pedidos", pedidos);

        // Marcas disponibles para el <select> del formulario
        req.setAttribute("marcas", pedidoService.listarMarcas());

        req.getRequestDispatcher("/WEB-INF/views/cu1-pedidos-recibir.jsp")
                .forward(req, resp);
    }

    // ── POST: procesar recepción de pedido (simulación de plataforma externa) ─
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String plataforma    = req.getParameter("plataformaOrigen");
        String cliente       = req.getParameter("nombreCliente");
        String marcaIdParam  = req.getParameter("marcaId");
        String tiempoParam   = req.getParameter("tiempoLimiteMin");

        try {
            Long marcaId       = Long.parseLong(marcaIdParam);
            int  tiempoLimite  = Integer.parseInt(tiempoParam);

            // Lógica del CU1: validar → asignar prioridad → persistir
            pedidoService.recibirPedido(plataforma, cliente, marcaId, tiempoLimite);

            // Mensaje de éxito mediante parámetro de query (PRG)
            resp.sendRedirect(req.getContextPath() + "/pedidos/recibir?ok=1");

        } catch (NumberFormatException e) {
            req.setAttribute("error", "Datos numéricos inválidos: " + e.getMessage());
            doGet(req, resp);   // vuelve a mostrar formulario con error

        } catch (IllegalArgumentException e) {
            // <<extend>> Notificar error de recepción del pedido
            req.setAttribute("error", e.getMessage());
            req.setAttribute("marcas", pedidoService.listarMarcas());
            req.setAttribute("pedidos", pedidoService.listarPedidosRecibidos());
            req.getRequestDispatcher("/WEB-INF/views/cu1-pedidos-recibir.jsp")
                    .forward(req, resp);
        }
    }
}
