package servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Insumo;
import model.OrdenDeCompra;
import service.InsumoService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Servlet para CU3 – Registrar entrada de insumos compartidos.
 *
 * ─── TRAZABILIDAD ──────────────────────────────────────────────────────────
 *
 * Diagrama de Actividades (CU3):
 *   GET /insumos/entrada           → muestra inventario + formulario de nueva entrada
 *   POST /insumos/entrada          → registra la orden de compra y suma stock
 *   POST /insumos/reducir          → reduce stock de un insumo específico
 *
 * Diagrama de Robustez (CU3):
 *   Este servlet actúa como "Interfaz de Registro de Compras" (Boundary)
 *   que interactúa con el control "Validador de Orden de Compra" y
 *   con la entidad "Generador de Comprobantes".
 *
 * Diagrama de Secuencia:
 *   AdministradorBodega → [doGet] → carga insumos → JSP cu3-insumos-entrada
 *   AdministradorBodega → [doPost registrar] → InsumoService.registrarEntradaInsumos()
 *   AdministradorBodega → [doPost reducir]   → InsumoService.reducirStock()
 */
public class InsumoEntradaServlet extends HttpServlet {

    private InsumoService insumoService;

    @Override
    public void init() {
        insumoService = new InsumoService();
    }

    // ── GET: mostrar inventario centralizado ────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Trazabilidad → DA paso "listar insumos del inventario centralizado"
        List<Insumo> insumos = insumoService.listarTodosInsumos();
        req.setAttribute("insumos", insumos);

        req.getRequestDispatcher("/WEB-INF/views/cu3-insumos-entrada.jsp")
                .forward(req, resp);
    }

    // ── POST: ramificar según acción ─────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");

        if ("reducir".equals(accion)) {
            procesarReduccion(req, resp);
        } else {
            procesarRegistroEntrada(req, resp);
        }
    }

    // ── Procesar registro de entrada de insumos (CU3 flujo principal) ────────
    private void procesarRegistroEntrada(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String numeroFactura   = req.getParameter("numeroFactura");
        String nombreProveedor = req.getParameter("nombreProveedor");
        String fechaParam      = req.getParameter("fechaFactura");
        String[] insumoIdsStr  = req.getParameterValues("insumoId");

        try {
            if (insumoIdsStr == null || insumoIdsStr.length == 0) {
                throw new IllegalArgumentException("Debe seleccionar al menos un insumo.");
            }

            LocalDate fechaFactura = LocalDate.parse(fechaParam);

            int n = insumoIdsStr.length;
            Long[]   insumoIds         = new Long[n];
            Double[] cantidadesPedidas  = new Double[n];
            Double[] cantidadesRecibidas= new Double[n];
            Double[] preciosUnitarios   = new Double[n];

            for (int i = 0; i < n; i++) {
                insumoIds[i]          = Long.parseLong(insumoIdsStr[i]);
                cantidadesPedidas[i]  = Double.parseDouble(
                        req.getParameter("cantidadPedida_" + insumoIds[i]));
                cantidadesRecibidas[i]= Double.parseDouble(
                        req.getParameter("cantidadRecibida_" + insumoIds[i]));
                preciosUnitarios[i]   = Double.parseDouble(
                        req.getParameter("precioUnitario_" + insumoIds[i]));
            }

            // Trazabilidad → DA: "registrar entrada" → service → sumar stock → comprobante
            OrdenDeCompra orden = insumoService.registrarEntradaInsumos(
                    numeroFactura, nombreProveedor, fechaFactura,
                    insumoIds, cantidadesPedidas, cantidadesRecibidas, preciosUnitarios);

            // Comprobante de recepción (Generador de Comprobantes del diagrama de robustez)
            req.setAttribute("comprobante", orden);
            req.setAttribute("insumos", insumoService.listarTodosInsumos());
            req.getRequestDispatcher("/WEB-INF/views/cu3-insumos-entrada.jsp")
                    .forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("insumos", insumoService.listarTodosInsumos());
            req.getRequestDispatcher("/WEB-INF/views/cu3-insumos-entrada.jsp")
                    .forward(req, resp);
        }
    }

    // ── Procesar reducción de stock ──────────────────────────────────────────
    private void procesarReduccion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String insumoIdStr = req.getParameter("insumoId");
        String cantidadStr = req.getParameter("cantidadReducir");

        try {
            Long   insumoId = Long.parseLong(insumoIdStr);
            Double cantidad  = Double.parseDouble(cantidadStr);

            // Trazabilidad → Diagrama de Clases: método descontarStock(cantidad) en Insumo
            insumoService.reducirStock(insumoId, cantidad);

            resp.sendRedirect(req.getContextPath() + "/insumos/entrada?reducidoOk=1");

        } catch (Exception e) {
            req.setAttribute("errorReduccion", e.getMessage());
            req.setAttribute("insumos", insumoService.listarTodosInsumos());
            req.getRequestDispatcher("/WEB-INF/views/cu3-insumos-entrada.jsp")
                    .forward(req, resp);
        }
    }
}
