<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Pedido, model.EstadoPedido" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>CU2 – Tablero Kanban</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <style>
        .kanban-column { vertical-align: top; }
        .kanban-card {
            background: #fff;
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 0.7rem 0.85rem;
            margin-bottom: 0.6rem;
            font-size: 0.85rem;
        }
        .kanban-card.prioridad-alta  { border-left: 3px solid var(--red); }
        .kanban-card.prioridad-media { border-left: 3px solid var(--yellow); }
        .kanban-card.prioridad-baja  { border-left: 3px solid var(--green); }
        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 0.4rem;
        }
        .card-id   { font-weight: 700; font-size: 0.82rem; color: var(--text-muted); }
        .card-body p { margin: 0.15rem 0; font-size: 0.83rem; color: var(--text); }
        .btn-avanzar {
            width: 100%;
            margin-top: 0.5rem;
            padding: 0.35rem 0;
            border: 1px solid var(--border2);
            border-radius: var(--radius);
            background: var(--bg2);
            color: var(--text);
            font-size: 0.8rem;
            font-family: 'Inter', sans-serif;
            cursor: pointer;
            transition: background 0.15s;
        }
        .btn-avanzar:hover { background: var(--bg3); border-color: var(--accent); }
        .col-count {
            font-size: 0.72rem;
            font-weight: 600;
            color: var(--text-muted);
            margin-left: 0.4rem;
        }
        .empty-col {
            color: var(--text-muted);
            font-size: 0.82rem;
            font-style: italic;
            padding: 0.5rem 0;
        }
    </style>
</head>
<body>
<nav class="navbar">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">Dark Kitchen</a>
    <div class="navbar-links">
        <a href="${pageContext.request.contextPath}/pedidos/recibir">CU1 · Pedidos</a>
        <a href="${pageContext.request.contextPath}/pedidos/kanban" class="active">CU2 · Kanban</a>
        <a href="${pageContext.request.contextPath}/insumos/entrada">CU3 · Insumos</a>
        <a href="${pageContext.request.contextPath}/menu/bloqueo">CU4 · Bloqueo</a>
    </div>
</nav>

<div class="container">
    <div class="page-header">
        <h2>CU2 – Tablero Kanban</h2>
        <p>Estado de preparacion de pedidos en la linea de produccion. Usa "Avanzar" para mover un pedido al siguiente estado.</p>
    </div>

    <% if (request.getAttribute("error") != null) { %>
    <div class="msg-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <%
        List<Pedido> pedidos = (List<Pedido>) request.getAttribute("pedidos");
        if (pedidos == null) pedidos = new java.util.ArrayList<>();

        List<Pedido> recibidos  = new java.util.ArrayList<>();
        List<Pedido> enPrep     = new java.util.ArrayList<>();
        List<Pedido> listos     = new java.util.ArrayList<>();
        List<Pedido> entregados = new java.util.ArrayList<>();

        for (Pedido p : pedidos) {
            switch (p.getEstado()) {
                case RECIBIDO:  recibidos.add(p);  break;
                case EN_PREP:   enPrep.add(p);     break;
                case LISTO:     listos.add(p);     break;
                case ENTREGADO: entregados.add(p); break;
            }
        }
    %>

    <div class="kanban-board">

        <%-- RECIBIDO --%>
        <div class="kanban-column">
            <h3>RECIBIDO <span class="col-count">(<%= recibidos.size() %>)</span></h3>
            <% if (recibidos.isEmpty()) { %><div class="empty-col">Sin pedidos</div><% } %>
            <% for (Pedido p : recibidos) { %>
            <div class="kanban-card <%= p.getPrioridadCss() %>">
                <div class="card-header">
                    <span class="card-id">P-<%= p.getId() %></span>
                    <span class="badge prioridad-<%= p.getPrioridadCss().replace("prioridad-","") %>"><%= p.getPrioridadLabel() %></span>
                </div>
                <div class="card-body">
                    <p><strong><%= p.getNombreCliente() %></strong></p>
                    <p><%= p.getMarca().getNombre() %></p>
                    <p style="color:var(--text-muted)"><%= p.getPlataformaOrigen() %></p>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/pedidos/kanban">
                    <input type="hidden" name="pedidoId" value="<%= p.getId() %>">
                    <button type="submit" class="btn-avanzar">Avanzar a En Preparacion</button>
                </form>
            </div>
            <% } %>
        </div>

        <%-- EN PREPARACION --%>
        <div class="kanban-column">
            <h3>EN PREPARACION <span class="col-count">(<%= enPrep.size() %>)</span></h3>
            <% if (enPrep.isEmpty()) { %><div class="empty-col">Sin pedidos</div><% } %>
            <% for (Pedido p : enPrep) { %>
            <div class="kanban-card <%= p.getPrioridadCss() %>">
                <div class="card-header">
                    <span class="card-id">P-<%= p.getId() %></span>
                    <span class="badge prioridad-<%= p.getPrioridadCss().replace("prioridad-","") %>"><%= p.getPrioridadLabel() %></span>
                </div>
                <div class="card-body">
                    <p><strong><%= p.getNombreCliente() %></strong></p>
                    <p><%= p.getMarca().getNombre() %></p>
                    <p style="color:var(--text-muted)"><%= p.getPlataformaOrigen() %></p>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/pedidos/kanban">
                    <input type="hidden" name="pedidoId" value="<%= p.getId() %>">
                    <button type="submit" class="btn-avanzar">Avanzar a Listo</button>
                </form>
            </div>
            <% } %>
        </div>

        <%-- LISTO --%>
        <div class="kanban-column">
            <h3>LISTO <span class="col-count">(<%= listos.size() %>)</span></h3>
            <% if (listos.isEmpty()) { %><div class="empty-col">Sin pedidos</div><% } %>
            <% for (Pedido p : listos) { %>
            <div class="kanban-card <%= p.getPrioridadCss() %>">
                <div class="card-header">
                    <span class="card-id">P-<%= p.getId() %></span>
                    <span class="badge prioridad-<%= p.getPrioridadCss().replace("prioridad-","") %>"><%= p.getPrioridadLabel() %></span>
                </div>
                <div class="card-body">
                    <p><strong><%= p.getNombreCliente() %></strong></p>
                    <p><%= p.getMarca().getNombre() %></p>
                    <p style="color:var(--text-muted)"><%= p.getPlataformaOrigen() %></p>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/pedidos/kanban">
                    <input type="hidden" name="pedidoId" value="<%= p.getId() %>">
                    <button type="submit" class="btn-avanzar">Avanzar a Entregado</button>
                </form>
            </div>
            <% } %>
        </div>

        <%-- ENTREGADO --%>
        <div class="kanban-column">
            <h3>ENTREGADO <span class="col-count">(<%= entregados.size() %>)</span></h3>
            <% if (entregados.isEmpty()) { %><div class="empty-col">Sin pedidos</div><% } %>
            <% for (Pedido p : entregados) { %>
            <div class="kanban-card <%= p.getPrioridadCss() %>">
                <div class="card-header">
                    <span class="card-id">P-<%= p.getId() %></span>
                    <span class="badge prioridad-<%= p.getPrioridadCss().replace("prioridad-","") %>"><%= p.getPrioridadLabel() %></span>
                </div>
                <div class="card-body">
                    <p><strong><%= p.getNombreCliente() %></strong></p>
                    <p><%= p.getMarca().getNombre() %></p>
                    <p style="color:var(--text-muted)"><%= p.getPlataformaOrigen() %></p>
                </div>
            </div>
            <% } %>
        </div>

    </div>

    <hr>
    <a href="${pageContext.request.contextPath}/pedidos/recibir">Ver CU1 – Pedidos Recibidos</a>
</div>
</body>
</html>
