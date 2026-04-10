<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Insumo" %>
<%@ page import="model.OrdenDeCompra" %>
<%@ page import="model.DetalleOrden" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CU3 - Entrada de Insumos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <style>
        /* ── Inventario ─────────────────────────────────────────── */
        .inventario-table { width: 100%; border-collapse: collapse; margin: 1rem 0; border: 1px solid #cbd5e1; border-radius: 8px; overflow: hidden; }
        .inventario-table th, .inventario-table td {
            padding: 0.65rem 1rem; text-align: left;
            border: 1px solid #cbd5e1;
        }
        .inventario-table th { background: #e2e8f0; font-weight: 700; color: #1e293b; font-size: .88rem; }
        .inventario-table tbody tr:nth-child(even) td { background: #f8fafc; }
        .inventario-table tbody tr:hover td { background: #eff6ff; }

        .badge-ok   { background:#dcfce7; color:#166534; padding:2px 8px; border-radius:12px; font-size:.8rem; }
        .badge-bajo { background:#fef9c3; color:#92400e; padding:2px 8px; border-radius:12px; font-size:.8rem; }

        /* ── Botones ─────────────────────────────────────────────── */
        .btn { padding:0.45rem 1.1rem; border:none; border-radius:6px; cursor:pointer; font-size:.9rem; }
        .btn-primary   { background:#2563eb; color:#fff; }
        .btn-primary:hover { background:#1d4ed8; }
        .btn-danger    { background:#dc2626; color:#fff; }
        .btn-danger:hover { background:#b91c1c; }
        .btn-secondary { background:#6b7280; color:#fff; }
        .btn-secondary:hover { background:#4b5563; }
        .btn-sm { padding:0.3rem 0.7rem; font-size:.8rem; }

        /* ── Formulario entrada ──────────────────────────────────── */
        #formularioEntrada { margin-top:1.5rem; border:1px solid #dbe2ea; border-radius:8px; padding:1.2rem; background:#f8fafc; }
        .form-grid { display:grid; grid-template-columns:1fr 1fr; gap:1rem; }
        .form-group { display:flex; flex-direction:column; gap:0.3rem; }
        .form-group label { font-size:.85rem; font-weight:600; color:#374151; }
        .form-group input, .form-group select {
            padding:0.45rem 0.6rem; border:1px solid #d1d5db; border-radius:5px; font-size:.9rem;
        }
        .form-group input:focus, .form-group select:focus { outline:2px solid #2563eb; }

        /* ── Filas de ítems ──────────────────────────────────────── */
        .item-row {
            display:grid; grid-template-columns:2fr 1fr 1fr 1fr auto;
            gap:0.6rem; align-items:center;
            background:#fff; border:1px solid #e5e7eb; border-radius:6px;
            padding:0.6rem 0.8rem; margin-bottom:0.5rem;
        }
        .item-row label { font-size:.75rem; color:#6b7280; margin-bottom:2px; display:block; }
        .item-row select, .item-row input { width:100%; box-sizing:border-box; }

        /* ── Comprobante ─────────────────────────────────────────── */
        .comprobante {
            margin-top:1.5rem; border:2px solid #22c55e; border-radius:8px;
            padding:1.2rem; background:#f0fdf4;
        }
        .comprobante h3 { color:#166534; margin-top:0; }
        .comprobante table { width:100%; border-collapse:collapse; margin-top:0.8rem; }
        .comprobante th, .comprobante td { padding:0.4rem 0.6rem; border:1px solid #bbf7d0; font-size:.85rem; }
        .comprobante th { background:#dcfce7; }
        .disc { color:#dc2626; font-weight:bold; }

        /* ── Alertas ─────────────────────────────────────────────── */
        .alert-error { background:#fee2e2; border:1px solid #fca5a5; color:#991b1b; padding:0.8rem 1rem; border-radius:6px; margin:1rem 0; }
        .alert-ok    { background:#dcfce7; border:1px solid #86efac; color:#166534; padding:0.8rem 1rem; border-radius:6px; margin:1rem 0; }

        /* ── Modal ───────────────────────────────────────────────── */
        #modalReducir {
            display:none; position:fixed; inset:0; background:rgba(0,0,0,.5);
            align-items:center; justify-content:center; z-index:999;
        }
        #modalReducir.activo { display:flex; }
        .modal-box {
            background:#fff; border-radius:10px; padding:1.8rem;
            width:360px; box-shadow:0 8px 24px rgba(0,0,0,.18);
        }
        .modal-box h3 { margin-top:0; color:#dc2626; }
        .modal-box .form-group { margin-top:1rem; }
        .modal-footer { display:flex; gap:0.6rem; justify-content:flex-end; margin-top:1.2rem; }
        .msg-max { font-size:.8rem; color:#6b7280; margin-top:3px; }
    </style>
</head>
<body>
<div class="container">

    <%-- Navegación --%>
    <p style="margin-top:0">
        <a href="${pageContext.request.contextPath}/index.jsp">← Volver al inicio</a>
    </p>

    <h2>CU3 - Registro de Entrada de Insumos Compartidos</h2>

    <%-- ── Alertas ──────────────────────────────────────────────────────── --%>
    <% if (request.getParameter("reducidoOk") != null) { %>
        <div class="alert-ok">Stock reducido correctamente.</div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert-error"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if (request.getAttribute("errorReduccion") != null) { %>
        <div class="alert-error"><%= request.getAttribute("errorReduccion") %></div>
    <% } %>

    <%-- ── Comprobante de recepción ────────────────────────────────────── --%>
    <%
        OrdenDeCompra comprobante = (OrdenDeCompra) request.getAttribute("comprobante");
        if (comprobante != null) {
    %>
    <div class="comprobante">
        <h3>Comprobante de Recepcion</h3>
        <p>
            <strong>Factura:</strong> <%= comprobante.getNumeroFactura() %> &nbsp;|&nbsp;
            <strong>Proveedor:</strong> <%= comprobante.getNombreProveedor() %> &nbsp;|&nbsp;
            <strong>Fecha:</strong> <%= comprobante.getFechaFactura() %> &nbsp;|&nbsp;
            <strong>Estado:</strong> <%= comprobante.getEstado() %>
        </p>
        <table>
            <thead>
                <tr>
                    <th>Insumo</th>
                    <th>Cant. Pedida</th>
                    <th>Cant. Recibida</th>
                    <th>Precio Unit.</th>
                    <th>Discrepancia</th>
                </tr>
            </thead>
            <tbody>
                <% for (DetalleOrden d : comprobante.getDetalles()) { %>
                <tr>
                    <td><%= d.getInsumo().getNombre() %></td>
                    <td><%= d.getCantidadPedida() %> <%= d.getInsumo().getUnidad() %></td>
                    <td><%= d.getCantidadRecibida() %> <%= d.getInsumo().getUnidad() %></td>
                    <td>$<%= String.format("%.2f", d.getPrecioUnitario()) %></td>
                    <td>
                        <% if (d.hayDiscrepancia()) { %>
                            <span class="disc">Si</span>
                        <% } else { %>
                            <span style="color:#166534">No</span>
                        <% } %>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
    <% } %>

    <%-- ── Tabla de inventario centralizado ──────────────────────────────── --%>

    <%
        List<Insumo> insumos = (List<Insumo>) request.getAttribute("insumos");
        if (insumos == null || insumos.isEmpty()) {
    %>
        <p style="color:#6b7280">No hay insumos registrados aún.</p>
    <% } else { %>
    <table class="inventario-table">
        <thead>
            <tr>
                <th>#</th>
                <th>Nombre</th>
                <th>Cantidad Actual</th>
                <th>Unidad</th>
                <th>Stock Mínimo</th>
                <th>Estado</th>
                <th>Acción</th>
            </tr>
        </thead>
        <tbody>
        <% for (Insumo ins : insumos) {
               boolean bajo = ins.getCantidad() <= ins.getStockMinimo();
        %>
            <tr>
                <td><%= ins.getId() %></td>
                <td><strong><%= ins.getNombre() %></strong></td>
                <td><%= ins.getCantidad() %></td>
                <td><%= ins.getUnidad() %></td>
                <td><%= ins.getStockMinimo() %></td>
                <td>
                    <% if (bajo) { %>
                        <span class="badge-bajo">Stock bajo</span>
                    <% } else { %>
                        <span class="badge-ok">OK</span>
                    <% } %>
                </td>
                <td>
                    <button class="btn btn-danger btn-sm"
                        onclick="abrirModalReducir(
                            '<%= ins.getId() %>',
                            '<%= ins.getNombre().replace("'", "\\'") %>',
                            '<%= ins.getCantidad() %>',
                            '<%= ins.getUnidad() %>'
                        )">
                        Reducir stock
                    </button>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>

    <%-- ── Botón abrir formulario ──────────────────────────────────────── --%>
    <div style="margin-top:1rem">
        <button class="btn btn-primary" onclick="toggleFormulario()">
            Registrar entrada de insumos
        </button>
    </div>

    <%-- ── Formulario de registro ──────────────────────────────────────── --%>
    <div id="formularioEntrada" style="display:none">
        <h3 style="margin-top:0">Nueva Entrada (Factura de Compra)</h3>
        <form method="post" action="${pageContext.request.contextPath}/insumos/entrada"
              onsubmit="return prepararFormulario()">

            <%-- Datos de la factura --%>
            <div class="form-grid">
                <div class="form-group">
                    <label for="numeroFactura">Número de Factura *</label>
                    <input type="text" id="numeroFactura" name="numeroFactura"
                           placeholder="Ej: FAC-2024-001" required>
                </div>
                <div class="form-group">
                    <label for="nombreProveedor">Proveedor / Negocio *</label>
                    <input type="text" id="nombreProveedor" name="nombreProveedor"
                           placeholder="Ej: Distribuidora La Cosecha" required>
                </div>
                <div class="form-group">
                    <label for="fechaFactura">Fecha de Factura *</label>
                    <input type="date" id="fechaFactura" name="fechaFactura" required>
                </div>
            </div>

            <%-- Ítems de insumos --%>
            <h4 style="margin-bottom:0.5rem">Insumos comprados</h4>
            <div id="contenedorItems">
                <%-- Fila plantilla (item-0) --%>
                <div class="item-row" id="item-0">
                    <div>
                        <label>Insumo *</label>
                        <%-- input oculto con el id seleccionado; name se rellena por JS --%>
                        <input type="hidden" class="input-insumo-id" name="insumoId">
                        <select class="select-insumo" required
                                onchange="this.previousElementSibling.value=this.value">
                            <option value="">-- Seleccione insumo --</option>
                            <% if (insumos != null) {
                                   for (Insumo ins : insumos) { %>
                            <option value="<%= ins.getId() %>">
                                <%= ins.getNombre() %> (Stock: <%= ins.getCantidad() %> <%= ins.getUnidad() %>)
                            </option>
                            <%     }
                               } %>
                        </select>
                    </div>
                    <div>
                        <label>Cant. Pedida *</label>
                        <input type="number" class="input-pedida" name="cantidadPedida_DYN"
                               min="0.01" step="0.01" placeholder="0" required>
                    </div>
                    <div>
                        <label>Cant. Recibida *</label>
                        <input type="number" class="input-recibida" name="cantidadRecibida_DYN"
                               min="0" step="0.01" placeholder="0" required>
                    </div>
                    <div>
                        <label>Precio Unit. ($) *</label>
                        <input type="number" class="input-precio" name="precioUnitario_DYN"
                               min="0" step="0.01" placeholder="0.00" required>
                    </div>
                    <div style="padding-top:1.2rem">
                        <button type="button" class="btn btn-secondary btn-sm"
                                onclick="eliminarItem(this)">✕</button>
                    </div>
                </div>
            </div>

            <div style="margin:0.6rem 0 1rem">
                <button type="button" class="btn btn-secondary btn-sm" onclick="agregarItem()">
                    + Añadir otro insumo
                </button>
            </div>

            <div style="display:flex; gap:0.8rem">
                <button type="submit" class="btn btn-primary">Registrar entrada</button>
                <button type="button" class="btn btn-secondary" onclick="toggleFormulario()">Cancelar</button>
            </div>
        </form>
    </div>
</div><%-- /container --%>

<%-- ── Modal reducción de stock ──────────────────────────────────────── --%>
<div id="modalReducir">
    <div class="modal-box">
        <h3>Reducir Stock</h3>
        <p>Insumo: <strong id="modalNombreInsumo"></strong></p>
        <p>Stock actual: <strong id="modalStockActual"></strong>
           <span id="modalUnidadInsumo"></span></p>

        <form method="post" action="${pageContext.request.contextPath}/insumos/entrada">
            <input type="hidden" name="accion" value="reducir">
            <input type="hidden" id="modalInsumoId" name="insumoId">

            <div class="form-group">
                <label for="cantidadReducir">Cantidad a reducir *</label>
                <input type="number" id="cantidadReducir" name="cantidadReducir"
                       min="0.01" step="0.01" placeholder="0" required>
                <span class="msg-max" id="msgMaxReducir"></span>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="cerrarModal()">Cancelar</button>
                <button type="submit" class="btn btn-danger">Reducir</button>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/insumos.js"></script>
<script>
    // Sobrescribe las funciones del modal para forzar display:flex al abrir
    function abrirModalReducir(id, nombre, stockActual, unidad) {
        document.getElementById('modalInsumoId').value            = id;
        document.getElementById('modalNombreInsumo').textContent  = nombre;
        document.getElementById('modalStockActual').textContent   = stockActual;
        document.getElementById('modalUnidadInsumo').textContent  = unidad;

        var input = document.getElementById('cantidadReducir');
        input.max   = stockActual;
        input.value = '';

        document.getElementById('msgMaxReducir').textContent =
            'Maximo permitido: ' + stockActual + ' ' + unidad;

        document.getElementById('modalReducir').style.display = 'flex';
    }

    function cerrarModal() {
        document.getElementById('modalReducir').style.display = 'none';
    }
</script>
</body>
</html>
