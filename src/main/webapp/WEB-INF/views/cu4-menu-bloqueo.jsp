<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Insumo, model.ItemMenu" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>CU4 – Bloqueo Automático de Menú</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <style>
        /* ── Paneles ──────────────────────────────────────────────────── */
        .panel-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1.5rem;
            margin-bottom: 2rem;
        }
        .panel {
            background: #f8fafc;
            border: 1px solid #dbe2ea;
            border-radius: 8px;
            padding: 1rem 1.2rem;
        }
        .panel h3 { margin-top: 0; font-size: 1rem; }

        /* ── Tablas ───────────────────────────────────────────────────── */
        table { width: 100%; border-collapse: collapse; font-size: 0.88rem; }
        th    { background: #1e40af; color: #fff; padding: 0.5rem 0.75rem; text-align: left; }
        td    { padding: 0.45rem 0.75rem; border-bottom: 1px solid #e5e7eb; }
        tr:hover td { background: #f9fafb; }

        /* ── Badges de estado ─────────────────────────────────────────── */
        .badge        { display: inline-block; padding: 0.18rem 0.6rem;
                        border-radius: 999px; font-size: 0.78rem; font-weight: 600; }
        .badge-critico { background: #fee2e2; color: #991b1b; }
        .badge-ok      { background: #d1fae5; color: #065f46; }
        .badge-activo  { background: #d1fae5; color: #065f46; }
        .badge-bloqueado { background: #fee2e2; color: #991b1b; }

        /* ── Mensajes ─────────────────────────────────────────────────── */
        .msg-alerta { background: #fef9c3; border: 1px solid #fde047; color: #854d0e;
                      border-radius: 6px; padding: 0.75rem 1rem; margin-bottom: 1rem; }
        .msg-ok     { background: #d1fae5; border: 1px solid #6ee7b7; color: #065f46;
                      border-radius: 6px; padding: 0.75rem 1rem; margin-bottom: 1rem; }
        .msg-neutro { background: #e0e7ff; border: 1px solid #a5b4fc; color: #3730a3;
                      border-radius: 6px; padding: 0.75rem 1rem; margin-bottom: 1rem; }
        .msg-error  { background: #fee2e2; border: 1px solid #fca5a5; color: #991b1b;
                      border-radius: 6px; padding: 0.75rem 1rem; margin-bottom: 1rem; }

        /* ── Botón de acción ──────────────────────────────────────────── */
        .btn-bloqueo {
            padding: 0.65rem 1.6rem;
            background: #dc2626;
            color: #fff;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 600;
        }
        .btn-bloqueo:hover { background: #b91c1c; }

        /* ── Barra de stock visual ────────────────────────────────────── */
        .stock-bar-wrap { background: #e5e7eb; border-radius: 4px; height: 8px; width: 100px; display: inline-block; vertical-align: middle; }
        .stock-bar      { height: 8px; border-radius: 4px; }
        .stock-bar.critico { background: #ef4444; }
        .stock-bar.ok      { background: #22c55e; }

        /* ── Sección de ciclo automático ─────────────────────────────── */
        .action-panel {
            background: #fff7ed;
            border: 1px solid #fed7aa;
            border-radius: 8px;
            padding: 1.2rem 1.5rem;
            margin-bottom: 2rem;
        }
        .action-panel h3 { margin-top: 0; color: #c2410c; }
    </style>
</head>
<body>
<div class="container">

    <h2>CU4 – Bloqueo Automático de Menú por Falta de Stock</h2>
    <p style="color:#6b7280; margin-top:-0.5rem;">
        El sistema monitorea los niveles de inventario y desactiva automáticamente
        los platos que no pueden prepararse por falta de insumos.
    </p>

    <%-- ── Mensajes de resultado del POST (PRG) ─────────────────────────── --%>
    <%
        String bloqueados  = request.getParameter("bloqueados");
        String criticosP   = request.getParameter("criticos");
        String sinCambios  = request.getParameter("sinCambios");
        String errorBloqueo = (String) request.getAttribute("errorBloqueo");
    %>
    <% if (errorBloqueo != null) { %>
        <div class="msg-error">⚠️ Error al ejecutar el ciclo: <%= errorBloqueo %></div>
    <% } else if (bloqueados != null) { %>
        <div class="msg-alerta">
            🚨 <strong>Alerta de reposición urgente emitida.</strong>
            Se bloquearon <strong><%= bloqueados %></strong> plato(s)
            por <strong><%= criticosP %></strong> insumo(s) en nivel crítico.
            Notificación enviada al Administrador de Bodega.
        </div>
    <% } else if ("1".equals(sinCambios)) { %>
        <div class="msg-neutro">
            ✅ Ciclo ejecutado. Todos los insumos están dentro de niveles aceptables.
            Ningún plato fue bloqueado.
        </div>
    <% } %>

    <%-- ── Panel de acción del sistema ──────────────────────────────────── --%>
    <div class="action-panel">
        <h3>⚙️ Ejecutar Ciclo de Monitoreo (Sistema como Actor)</h3>
        <p style="margin:0 0 0.8rem; font-size:0.92rem;">
            Simula el proceso automático: detecta insumos críticos → identifica platos
            afectados → desactiva disponibilidad en plataformas de delivery →
            emite alerta de reposición urgente.
        </p>
        <form method="post" action="${pageContext.request.contextPath}/menu/bloqueo">
            <button type="submit" class="btn-bloqueo">
                🔒 Ejecutar Bloqueo Automático de Menú
            </button>
        </form>
    </div>

    <%-- ── Paneles de inventario y menú ─────────────────────────────────── --%>
    <%
        List<Insumo>   todosInsumos = (List<Insumo>)   request.getAttribute("todosInsumos");
        List<ItemMenu> todosItems   = (List<ItemMenu>)  request.getAttribute("todosItems");
        List<Insumo>   criticos     = (List<Insumo>)   request.getAttribute("criticos");
        int totalCriticos = (criticos != null) ? criticos.size() : 0;
    %>

    <div class="panel-grid">

        <%-- ── Panel Inventario ────────────────────────────────────────── --%>
        <div class="panel">
            <h3>
                📦 Inventario de Insumos
                <% if (totalCriticos > 0) { %>
                    <span class="badge badge-critico"><%= totalCriticos %> crítico(s)</span>
                <% } %>
            </h3>
            <% if (todosInsumos == null || todosInsumos.isEmpty()) { %>
                <p style="color:#6b7280; font-style:italic;">Sin insumos registrados.</p>
            <% } else { %>
            <table>
                <thead>
                    <tr><th>Insumo</th><th>Stock</th><th>Mínimo</th><th>Estado</th></tr>
                </thead>
                <tbody>
                <%
                    for (Insumo ins : todosInsumos) {
                        boolean esCritico = ins.getCantidad() <= ins.getStockMinimo();
                        double pct = ins.getStockMinimo() > 0
                                ? Math.min(100, (ins.getCantidad() / ins.getStockMinimo()) * 100)
                                : 100;
                %>
                <tr>
                    <td><strong><%= ins.getNombre() %></strong></td>
                    <td>
                        <%= ins.getCantidad() %> <%= ins.getUnidad() %>
                        <div class="stock-bar-wrap">
                            <div class="stock-bar <%= esCritico ? "critico" : "ok" %>"
                                 style="width:<%= (int)pct %>%"></div>
                        </div>
                    </td>
                    <td><%= ins.getStockMinimo() %> <%= ins.getUnidad() %></td>
                    <td>
                        <span class="badge <%= esCritico ? "badge-critico" : "badge-ok" %>">
                            <%= esCritico ? "⚠ Crítico" : "✓ OK" %>
                        </span>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
            <% } %>
        </div>

        <%-- ── Panel Menú ──────────────────────────────────────────────── --%>
        <div class="panel">
            <h3>🍽️ Estado del Menú</h3>
            <% if (todosItems == null || todosItems.isEmpty()) { %>
                <p style="color:#6b7280; font-style:italic;">Sin platos registrados.</p>
            <% } else { %>
            <table>
                <thead>
                    <tr><th>Plato</th><th>Marca</th><th>Disponibilidad</th></tr>
                </thead>
                <tbody>
                <% for (ItemMenu item : todosItems) { %>
                <tr>
                    <td><strong><%= item.getNombre() %></strong></td>
                    <td style="font-size:0.82rem; color:#6b7280;">
                        <%= item.getMarca() != null ? item.getMarca().getNombre() : "—" %>
                    </td>
                    <td>
                        <span class="badge <%= item.isActivo() ? "badge-activo" : "badge-bloqueado" %>">
                            <%= item.isActivo() ? "✓ Activo" : "🔒 Bloqueado" %>
                        </span>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
            <% } %>
        </div>
    </div>

    <%-- ── Leyenda de trazabilidad ───────────────────────────────────────── --%>
    <details style="margin-top:1rem; font-size:0.85rem; color:#6b7280;">
        <summary style="cursor:pointer; font-weight:600; color:#374151;">
            ℹ️ Trazabilidad con el diagrama de robustez CU4
        </summary>
        <ul style="margin:0.75rem 0 0 1rem; line-height:1.7;">
            <li><strong>Monitor de Inventario</strong> → <code>MenuService.detectarInsumosCriticos()</code></li>
            <li><strong>Analista de Platillos Afectados</strong> → <code>MenuService.identificarPlatosAfectados()</code></li>
            <li><strong>Gestor de Bloqueo de Platillos</strong> → <code>MenuService.bloquearPlatosAfectados()</code></li>
            <li><strong>API de Integración con App Delivery</strong> → desactiva <code>ItemMenu.activo = false</code></li>
            <li><strong>API de Notificaciones</strong> → alerta visible al Administrador de Bodega (mensaje en vista)</li>
        </ul>
    </details>

    <hr style="margin:2rem 0; border:none; border-top:1px solid #e5e7eb;">
    <a href="${pageContext.request.contextPath}/index.jsp">← Volver al inicio</a>
    &nbsp;&nbsp;
    <a href="${pageContext.request.contextPath}/insumos/entrada">Ver CU3 – Entrada de Insumos →</a>

</div>
</body>
</html>
