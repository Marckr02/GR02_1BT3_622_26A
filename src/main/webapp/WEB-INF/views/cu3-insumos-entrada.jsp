<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Insumo, model.OrdenDeCompra, model.DetalleOrden" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CU3 – Entrada de Insumos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <style>
        .form-entrada {
            background: var(--bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 1.25rem;
            margin-top: 1.25rem;
            display: none;
        }
        .form-entrada h3 { font-size: 0.95rem; font-weight: 600; margin-bottom: 1rem; color: var(--text); }
        .item-row {
            display: grid;
            grid-template-columns: 2.5fr 1fr 1fr 1fr auto;
            gap: 0.6rem;
            align-items: end;
            background: var(--bg2);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 0.65rem 0.85rem;
            margin-bottom: 0.5rem;
        }
        .item-row label { font-size: 0.75rem; font-weight: 600; color: var(--text-muted); margin-bottom: 0.25rem; display: block; }
        .item-row select, .item-row input { width: 100%; }
        .btn-remove {
            padding: 0.5rem 0.65rem;
            background: var(--bg3);
            border: 1px solid var(--border2);
            border-radius: var(--radius);
            cursor: pointer;
            font-size: 0.8rem;
            color: var(--text-muted);
        }
        .btn-remove:hover { background: var(--red-bg); color: var(--red); border-color: #f5c2c7; }
        .comprobante {
            margin-top: 1.25rem;
            border: 1px solid #b7e4c7;
            border-radius: var(--radius);
            padding: 1.25rem;
            background: var(--green-bg);
        }
        .comprobante h3 { font-size: 0.95rem; font-weight: 700; color: var(--green); margin-bottom: 0.75rem; }
        .comprobante p  { font-size: 0.875rem; color: var(--text); margin-bottom: 0.4rem; }
        .disc { color: var(--red); font-weight: 700; }
        /* Modal */
        #modalReducir {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.35);
            align-items: center;
            justify-content: center;
            z-index: 999;
        }
        #modalReducir.activo { display: flex; }
        .modal-box {
            background: var(--bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 1.5rem;
            width: 360px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
        }
        .modal-box h3 { font-size: 1rem; font-weight: 700; margin-bottom: 1rem; color: var(--text); }
        .modal-footer { display: flex; gap: 0.6rem; justify-content: flex-end; margin-top: 1.25rem; }
        .msg-max { font-size: 0.76rem; color: var(--text-muted); margin-top: 0.25rem; display: block; }
        .form-group { margin-bottom: 0.85rem; }
        .form-group label { font-size: 0.8rem; font-weight: 600; color: var(--text-muted); display: block; margin-bottom: 0.3rem; }
        .form-group input, .form-group select {
            width: 100%;
            padding: 0.5rem 0.75rem;
            border: 1px solid var(--border2);
            border-radius: var(--radius);
            font-size: 0.875rem;
            font-family: 'Inter', sans-serif;
            background: var(--bg);
            color: var(--text);
        }
        .form-group input:focus { outline: none; border-color: var(--accent); box-shadow: 0 0 0 2px rgba(73,80,87,0.12); }
        .btn-toggle {
            padding: 0.5rem 1.2rem;
            border: 1px solid var(--border2);
            border-radius: var(--radius);
            background: var(--bg);
            color: var(--text);
            font-size: 0.875rem;
            font-family: 'Inter', sans-serif;
            font-weight: 500;
            cursor: pointer;
            margin-top: 0.5rem;
        }
        .btn-toggle:hover { background: var(--bg3); }
    </style>
</head>
<body>
<nav class="navbar">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">Dark Kitchen</a>
    <div class="navbar-links">
        <a href="${pageContext.request.contextPath}/pedidos/recibir">CU1 · Pedidos</a>
        <a href="${pageContext.request.contextPath}/pedidos/kanban">CU2 · Kanban</a>
        <a href="${pageContext.request.contextPath}/insumos/entrada" class="active">CU3 · Insumos</a>
        <a href="${pageContext.request.contextPath}/menu/bloqueo">CU4 · Bloqueo</a>
    </div>
</nav>

<div class="container">
    <div class="page-header">
        <h2>CU3 – Entrada de Insumos Compartidos</h2>
        <p>Registro de facturas de compra y actualizacion del inventario centralizado.</p>
    </div>

    <%-- Mensajes --%>
    <% if ("1".equals(request.getParameter("reducidoOk"))) { %>
    <div class="msg-ok">Stock reducido correctamente.</div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
    <div class="msg-error"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if (request.getAttribute("errorReduccion") != null) { %>
    <div class="msg-error"><%= request.getAttribute("errorReduccion") %></div>
    <% } %>

    <%-- Comprobante --%>
    <%
        OrdenDeCompra comprobante = (OrdenDeCompra) request.getAttribute("comprobante");
        if (comprobante != null) {
    %>
    <div class="comprobante">
        <h3>Comprobante de Recepcion Exitosa</h3>
        <p><strong>Factura:</strong> <%= comprobante.getNumeroFactura() %> &nbsp;|&nbsp;
           <strong>Proveedor:</strong> <%= comprobante.getNombreProveedor() %> &nbsp;|&nbsp;
           <strong>Fecha:</strong> <%= comprobante.getFechaFactura() %> &nbsp;|&nbsp;
           <strong>Estado:</strong> <%= comprobante.getEstado() %></p>
        <div class="table-wrap" style="margin-top:0.75rem;">
        <table>
            <thead>
                <tr>
                    <th>Insumo</th><th>Cant. Pedida</th><th>Cant. Recibida</th>
                    <th>Precio Unit.</th><th>Discrepancia</th>
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
                    <span style="color:var(--green)">No</span>
                    <% } %>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
        </div>
    </div>
    <% } %>

    <%-- Tabla de inventario --%>
    <%
        List<Insumo> insumos = (List<Insumo>) request.getAttribute("insumos");
    %>
    <% if (insumos == null || insumos.isEmpty()) { %>
    <p style="color:var(--text-muted); font-style:italic;">No hay insumos registrados.</p>
    <% } else { %>
    <div class="table-wrap">
    <table>
        <thead>
            <tr>
                <th>#</th><th>Nombre</th><th>Stock Actual</th><th>Unidad</th>
                <th>Stock Minimo</th><th>Estado</th><th>Accion</th>
            </tr>
        </thead>
        <tbody>
        <% for (Insumo ins : insumos) {
               boolean bajo = ins.getCantidad() <= ins.getStockMinimo(); %>
        <tr>
            <td style="color:var(--text-muted)"><%= ins.getId() %></td>
            <td><strong><%= ins.getNombre() %></strong></td>
            <td><%= ins.getCantidad() %></td>
            <td><%= ins.getUnidad() %></td>
            <td><%= ins.getStockMinimo() %></td>
            <td>
                <span class="badge <%= bajo ? "badge-critico" : "badge-ok" %>">
                    <%= bajo ? "Stock bajo" : "OK" %>
                </span>
            </td>
            <td>
                <button class="btn btn-danger" style="padding:0.3rem 0.7rem; font-size:0.78rem; margin-top:0;"
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
    </div>
    <% } %>

    <button class="btn-toggle" onclick="toggleFormulario()">Registrar entrada de insumos</button>

    <%-- Formulario de registro --%>
    <div class="form-entrada" id="formularioEntrada">
        <h3>Nueva Entrada — Factura de Compra</h3>
        <form method="post" action="${pageContext.request.contextPath}/insumos/entrada"
              onsubmit="return prepararFormulario()">

            <div class="form-grid">
                <div>
                    <label class="form-grid label">Numero de Factura</label>
                    <input type="text" name="numeroFactura" placeholder="FAC-2024-001" required>
                </div>
                <div>
                    <label>Proveedor</label>
                    <input type="text" name="nombreProveedor" placeholder="Distribuidora La Cosecha" required>
                </div>
                <div>
                    <label>Fecha de Factura</label>
                    <input type="date" name="fechaFactura" id="fechaFactura" required>
                </div>
            </div>

            <p style="font-size:0.85rem; font-weight:600; color:var(--text); margin:1rem 0 0.5rem;">Insumos comprados</p>
            <div id="contenedorItems">
                <div class="item-row" id="item-0">
                    <div>
                        <label>Insumo</label>
                        <input type="hidden" class="input-insumo-id" name="insumoId">
                        <select class="select-insumo" required
                                onchange="this.previousElementSibling.value=this.value">
                            <option value="">-- Seleccionar insumo --</option>
                            <% if (insumos != null) { for (Insumo ins : insumos) { %>
                            <option value="<%= ins.getId() %>">
                                <%= ins.getNombre() %> (Stock: <%= ins.getCantidad() %> <%= ins.getUnidad() %>)
                            </option>
                            <% } } %>
                        </select>
                    </div>
                    <div>
                        <label>Cant. Pedida</label>
                        <input type="number" class="input-pedida" name="cantidadPedida_DYN"
                               min="0.01" step="0.01" placeholder="0" required>
                    </div>
                    <div>
                        <label>Cant. Recibida</label>
                        <input type="number" class="input-recibida" name="cantidadRecibida_DYN"
                               min="0" step="0.01" placeholder="0" required>
                    </div>
                    <div>
                        <label>Precio Unit. ($)</label>
                        <input type="number" class="input-precio" name="precioUnitario_DYN"
                               min="0" step="0.01" placeholder="0.00" required>
                    </div>
                    <div>
                        <label>&nbsp;</label>
                        <button type="button" class="btn-remove" onclick="eliminarItem(this)">X</button>
                    </div>
                </div>
            </div>

            <div style="margin: 0.6rem 0 1rem;">
                <button type="button" class="btn-toggle" onclick="agregarItem()">+ Anadir insumo</button>
            </div>

            <div style="display:flex; gap:0.6rem;">
                <button type="submit" class="btn btn-primary" style="margin-top:0;">Registrar entrada</button>
                <button type="button" class="btn-toggle" onclick="toggleFormulario()">Cancelar</button>
            </div>
        </form>
    </div>

    <hr>
    <a href="${pageContext.request.contextPath}/menu/bloqueo">Ver CU4 – Bloqueo de Menu</a>
</div>

<%-- Modal reduccion --%>
<div id="modalReducir">
    <div class="modal-box">
        <h3>Reducir Stock</h3>
        <p style="font-size:0.875rem; color:var(--text-muted); margin-bottom:0.5rem;">
            Insumo: <strong id="modalNombreInsumo" style="color:var(--text)"></strong>
        </p>
        <p style="font-size:0.875rem; color:var(--text-muted);">
            Stock actual: <strong id="modalStockActual" style="color:var(--text)"></strong>
            <span id="modalUnidadInsumo"></span>
        </p>
        <form method="post" action="${pageContext.request.contextPath}/insumos/entrada">
            <input type="hidden" name="accion" value="reducir">
            <input type="hidden" id="modalInsumoId" name="insumoId">
            <div class="form-group" style="margin-top:1rem;">
                <label for="cantidadReducir">Cantidad a reducir</label>
                <input type="number" id="cantidadReducir" name="cantidadReducir"
                       min="0.01" step="0.01" placeholder="0" required>
                <span class="msg-max" id="msgMaxReducir"></span>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-toggle" onclick="cerrarModal()">Cancelar</button>
                <button type="submit" class="btn btn-danger" style="margin-top:0;">Reducir</button>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/insumos.js"></script>
</body>
</html>
