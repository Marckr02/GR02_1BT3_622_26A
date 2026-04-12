<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Insumo, model.ItemMenu, model.DetalleInsumoMenu, service.MenuService" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>CU4 – Monitor de Menu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <style>
        .section-title {
            font-size: 0.8rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: var(--text-muted);
            margin: 1.5rem 0 0.75rem;
            padding-bottom: 0.4rem;
            border-bottom: 1px solid var(--border);
        }
        .info-box {
            background: var(--bg);
            border: 1px solid var(--border);
            border-left: 3px solid var(--blue);
            border-radius: var(--radius);
            padding: 0.85rem 1.1rem;
            margin-bottom: 1.5rem;
            font-size: 0.875rem;
            color: var(--text-muted);
        }
        .motivo {
            font-size: 0.78rem;
            color: var(--red);
            margin-top: 0.3rem;
        }
        .stock-bar-wrap {
            background: var(--bg3);
            border-radius: 3px;
            height: 5px;
            width: 70px;
            display: inline-block;
            vertical-align: middle;
            margin-left: 0.4rem;
            overflow: hidden;
        }
        .stock-bar { height: 5px; border-radius: 3px; }
        .stock-bar.critico { background: #c9404a; }
        .stock-bar.ok      { background: #40a060; }
        .resumen-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 1rem;
            margin-bottom: 1.5rem;
        }
        .resumen-card {
            background: var(--bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 1rem;
            text-align: center;
        }
        .resumen-card .num {
            font-size: 2rem;
            font-weight: 700;
            color: var(--text);
            line-height: 1;
        }
        .resumen-card .num.rojo   { color: var(--red); }
        .resumen-card .num.verde  { color: var(--green); }
        .resumen-card .num.amarillo { color: var(--yellow); }
        .resumen-card .lbl {
            font-size: 0.75rem;
            color: var(--text-muted);
            margin-top: 0.3rem;
            font-weight: 500;
        }
    </style>
</head>
<body>
<nav class="navbar">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">Dark Kitchen</a>
    <div class="navbar-links">
        <a href="${pageContext.request.contextPath}/pedidos/recibir">CU1 · Pedidos</a>
        <a href="${pageContext.request.contextPath}/pedidos/kanban">CU2 · Kanban</a>
        <a href="${pageContext.request.contextPath}/insumos/entrada">CU3 · Insumos</a>
        <a href="${pageContext.request.contextPath}/menu/bloqueo" class="active">CU4 · Bloqueo</a>
    </div>
</nav>

<div class="container">
    <div class="page-header">
        <h2>CU4 – Monitor de Disponibilidad del Menu</h2>
        <p>Estado actual del menu segun el inventario. El bloqueo y reactivacion de platos ocurre automaticamente al modificar el stock en CU3.</p>
    </div>

    <div class="info-box">
        El sistema evalua automaticamente cada plato al registrar o reducir stock en CU3.
        Un plato se bloquea si alguno de sus insumos no tiene stock suficiente para prepararlo,
        y se reactiva automaticamente cuando el stock es repuesto.
    </div>

    <%
        List<ItemMenu> todosItems   = (List<ItemMenu>) request.getAttribute("todosItems");
        List<Insumo>   todosInsumos = (List<Insumo>)  request.getAttribute("todosInsumos");
        List<Insumo>   criticos     = (List<Insumo>)  request.getAttribute("criticos");
        MenuService    menuService  = (MenuService)   request.getAttribute("menuService");

        if (todosItems   == null) todosItems   = new java.util.ArrayList<>();
        if (todosInsumos == null) todosInsumos = new java.util.ArrayList<>();
        if (criticos     == null) criticos     = new java.util.ArrayList<>();

        int totalActivos   = 0;
        int totalBloqueados = 0;
        for (ItemMenu it : todosItems) {
            if (it.isActivo()) totalActivos++; else totalBloqueados++;
        }
    %>

    <%-- Resumen --%>
    <div class="resumen-grid">
        <div class="resumen-card">
            <div class="num verde"><%= totalActivos %></div>
            <div class="lbl">Platos disponibles</div>
        </div>
        <div class="resumen-card">
            <div class="num rojo"><%= totalBloqueados %></div>
            <div class="lbl">Platos bloqueados</div>
        </div>
        <div class="resumen-card">
            <div class="num amarillo"><%= criticos.size() %></div>
            <div class="lbl">Insumos en nivel critico</div>
        </div>
    </div>

    <%-- Estado del menu --%>
    <div class="section-title">Estado del menu</div>
    <% if (todosItems.isEmpty()) { %>
    <p style="color:var(--text-muted); font-style:italic; font-size:0.875rem;">
        No hay platos registrados. Ve a CU3 para registrar insumos y los platos demo se crearan automaticamente.
    </p>
    <% } else { %>
    <div class="table-wrap">
    <table>
        <thead>
            <tr>
                <th>Plato</th>
                <th>Marca</th>
                <th>Disponibilidad</th>
                <th>Motivo de bloqueo</th>
            </tr>
        </thead>
        <tbody>
        <% for (ItemMenu plato : todosItems) { %>
        <tr>
            <td><strong><%= plato.getNombre() %></strong></td>
            <td style="color:var(--text-muted)"><%= plato.getMarca() != null ? plato.getMarca().getNombre() : "—" %></td>
            <td>
                <span class="badge <%= plato.isActivo() ? "badge-ok" : "badge-bloqueado" %>">
                    <%= plato.isActivo() ? "Disponible" : "Bloqueado" %>
                </span>
            </td>
            <td>
                <% if (!plato.isActivo() && menuService != null) {
                       List<String> motivos = menuService.obtenerMotivosBloqueo(plato);
                       for (String motivo : motivos) { %>
                <div class="motivo"><%= motivo %></div>
                <%     }
                   } else if (plato.isActivo()) { %>
                <span style="color:var(--text-muted); font-size:0.82rem;">—</span>
                <% } %>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
    </div>
    <% } %>

    <%-- Inventario de insumos --%>
    <div class="section-title">Inventario de insumos</div>
    <% if (todosInsumos.isEmpty()) { %>
    <p style="color:var(--text-muted); font-style:italic; font-size:0.875rem;">Sin insumos registrados.</p>
    <% } else { %>
    <div class="table-wrap">
    <table>
        <thead>
            <tr><th>Insumo</th><th>Stock actual</th><th>Stock minimo</th><th>Estado</th></tr>
        </thead>
        <tbody>
        <% for (Insumo ins : todosInsumos) {
               boolean esCritico = ins.getCantidad() <= ins.getStockMinimo();
               double pct = ins.getStockMinimo() > 0
                       ? Math.min(100, (ins.getCantidad() / ins.getStockMinimo()) * 100) : 100; %>
        <tr>
            <td><strong><%= ins.getNombre() %></strong></td>
            <td>
                <%= ins.getCantidad() %> <%= ins.getUnidad() %>
                <span class="stock-bar-wrap">
                    <span class="stock-bar <%= esCritico ? "critico" : "ok" %>"
                          style="width:<%= (int)Math.min(100, pct) %>%"></span>
                </span>
            </td>
            <td><%= ins.getStockMinimo() %> <%= ins.getUnidad() %></td>
            <td>
                <span class="badge <%= esCritico ? "badge-critico" : "badge-ok" %>">
                    <%= esCritico ? "Critico" : "OK" %>
                </span>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
    </div>
    <% } %>

    <hr>
    <a href="${pageContext.request.contextPath}/insumos/entrada">Ir a CU3 – Gestionar Insumos</a>
</div>
</body>
</html>
