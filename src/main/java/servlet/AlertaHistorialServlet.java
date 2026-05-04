package servlet;

import model.AlertaStock;
import service.AlertaService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet de frontera para el historial de alertas de stock.
 *
 * Trazabilidad – Tarea 4:
 *   GET /alertas/historial → carga todas las alertas vía AlertaService
 *                          → pasa lista como atributo "alertas" al request
 *                          → despacha a cu-alertas.jsp
 */

public class AlertaHistorialServlet extends HttpServlet {

    private AlertaService alertaService;

    @Override
    public void init() throws ServletException {
        alertaService = new AlertaService();
    }

    /**
     * Carga el historial de alertas y lo delega a la vista JSP.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<AlertaStock> alertas = alertaService.listarHistorial();
        req.setAttribute("alertas", alertas);

        req.getRequestDispatcher("/WEB-INF/views/cu-alertas.jsp")
                .forward(req, resp);
    }
}