<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Pedido, model.Marca" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>CU1 – Recibir Pedidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <style>
        /* ── Formulario de simulación ───────────────────────────────────── */
        .sim-panel {
            background: #f0f4ff;
            border: 1px solid #c7d5f8;
            border-radius: 8px;
            padding: 1.2rem 1.5rem;
            margin-bottom: 2rem;
        }
        .sim-panel h3 { margin-top: 0; color: #1e40af; }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 0.8rem 1.5rem;
        }
        .form-grid label { display: block; font-size: 0.85rem; color: #374151; margin-bottom: 0.25rem; }
        .form-grid input,
        .form-grid select {
            width: 100%;
            padding: 0.45rem 0.7rem;
            border: 1px solid #d1d5db;
            border-radius: 6px;
            font-size: 0.95rem;
            box-sizing: border-box;
        }
        .btn-sim {
            margin-top: 1rem;
            padding: 0.55rem 1.4rem;
            background: #2563eb;
            color: #fff;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 0.95rem;
        }
        .btn-sim:hover { background: #1d4ed8; }

        /* ── Mensajes de estado ─────────────────────────────────────────── */
        .msg-ok    { background:#d1fae5; border:1px solid #6ee7b7; color:#065f46;
            border-radius:6px; padding:0.6rem 1rem; margin-bottom:1rem; }
        .msg-error { background:#fee2e2; border:1px solid #fca5a5; color:#991b1b;
            border-radius:6px; padding:0.6rem 1rem; margin-bottom:1rem; }

        /* ── Tabla de pedidos recibidos ─────────────────────────────────── */
        table { width:100%; border-collapse:collapse; font-size:0.9rem; }
        th    { background:#1e40af; color:#fff; padding:0.55rem 0.75rem; text-align:left; }
        td    { padding:0.5rem 0.75rem; border-bottom:1px solid #e5e7eb; }
        tr:hover td { background:#f9fafb; }

        /* Badges de prioridad */
        .badge { display:inline-block; padding:0.2rem 0.65rem;
            border-radius:999px; font-size:0.78rem; font-weight:600; }
        .prioridad-alta  { background:#fee2e2; color:#991b1b; }
        .prioridad-media { background:#fef9c3; color:#854d0e; }
        .prioridad-baja  { background:#d1fae5; color:#065f46; }

        /* Badge de plataforma */
        .plat { display:inline-block; padding:0.15rem 0.55rem; border-radius:4px;
            font-size:0.8rem; background:#e0e7ff; color:#3730a3; }
    </style>
</head>
<body>
<div class="container">

    <h2>CU1 – Recibir Pedidos de Plataforma Externa</h2>
    <p style="color:#6b7280; margin-top:-0.5rem;">
        Simulación de recepción de pedidos entrantes desde apps de delivery.
    </p>

    <%-- ── Mensajes de resultado ───────────────────────────────────────── --%>
    <% if ("1".equals(request.getParameter("ok"))) { %>
    <div class="msg-ok">✅ Pedido recibido correctamente y añadido al tablero.</div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
    <div class="msg-error">⚠️ Error de recepción: <%= request.getAttribute("error") %></div>
    <% } %>

    <%-- ── Panel de simulación (representa la Plataforma de Delivery) ──── --%>
    <div class="sim-panel">
        <h3>📦 Simular Pedido Entrante (Plataforma Externa)</h3>
        <form method="post" action="${pageContext.request.contextPath}/pedidos/recibir">
            <div class="form-grid">
                <div>
                    <label for="plataformaOrigen">Plataforma de delivery</label>
                    <select name="plataformaOrigen" id="plataformaOrigen" required>
                        <option value="">— Seleccionar —</option>
                        <option value="Rappi">Rappi</option>
                        <option value="iFood">iFood</option>
                        <option value="Uber Eats">Uber Eats</option>
                        <option value="PedidosYa">PedidosYa</option>
                    </select>
                </div>
                <div>
                    <label for="nombreCliente">Nombre del cliente</label>
                    <input type="text" name="nombreCliente" id="nombreCliente"
                           placeholder="Ej: Ana Mora" required maxlength="120">
                </div>
                <div>
                    <label for="marcaId">Marca (cocina responsable)</label>
                    <select name="marcaId" id="marcaId" required>
                        <option value="">— Seleccionar —</option>
                        <%
                            List<Marca> marcas = (List<Marca>) request.getAttribute("marcas");
                            if (marcas != null) {
                                for (Marca m : marcas) {
                        %>
                        <option value="<%= m.getId() %>"><%= m.getNombre() %></option>
                        <%      }
                        }
                        %>
                    </select>
                </div>
                <div>
                    <label for="tiempoLimiteMin">Tiempo límite (minutos)</label>
                    <input type="number" name="tiempoLimiteMin" id="tiempoLimiteMin"
                           value="30" min="1" max="120" required>
                    <small style="color:#6b7280;">
                        ≤ 20 min → Alta &nbsp;|&nbsp; ≤ 40 min → Media &nbsp;|&nbsp; > 40 min → Baja
                    </small>
                </div>
            </div>
            <button type="submit" class="btn-sim">📨 Enviar Pedido al Sistema</button>
        </form>
    </div>

    <%-- ── Lista de pedidos recibidos ───────────────────────────────────── --%>
    <h3 style="margin-bottom:0.75rem;">
        🗂 Pedidos en estado RECIBIDO
        <% List<Pedido> pedidos = (List<Pedido>) request.getAttribute("pedidos"); %>
        <span style="font-weight:400; font-size:0.9rem; color:#6b7280;">
            (<%= pedidos != null ? pedidos.size() : 0 %> pedido(s))
        </span>
    </h3>

    <% if (pedidos == null || pedidos.isEmpty()) { %>
    <p style="color:#6b7280; font-style:italic;">
        No hay pedidos en estado RECIBIDO. Usa el formulario de arriba para simular uno.
    </p>
    <% } else { %>
    <table>
        <thead>
        <tr>
            <th>#</th>
            <th>Plataforma</th>
            <th>Cliente</th>
            <th>Marca</th>
            <th>Prioridad</th>
            <th>Tiempo límite</th>
            <th>Recibido</th>
            <th>Estado</th>
        </tr>
        </thead>
        <tbody>
        <% for (Pedido p : pedidos) { %>
        <tr>
            <td><strong>#<%= p.getId() %></strong></td>
            <td><span class="plat"><%= p.getPlataformaOrigen() %></span></td>
            <td><%= p.getNombreCliente() %></td>
            <td><%= p.getMarca().getNombre() %></td>
            <td>
                        <span class="badge <%= p.getPrioridadCss() %>">
                            <%= p.getPrioridadLabel() %>
                        </span>
            </td>
            <td><%= p.getTiempoLimiteMin() %> min</td>
            <td style="font-size:0.82rem; color:#6b7280;">
                <%= p.getTimestamp().toLocalDate() %>
                <%= p.getTimestamp().toLocalTime().withSecond(0).withNano(0) %>
            </td>
            <td><code><%= p.getEstado() %></code></td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>

    <hr style="margin:2rem 0; border:none; border-top:1px solid #e5e7eb;">
    <a href="${pageContext.request.contextPath}/index.jsp">← Volver al inicio</a>
    &nbsp;&nbsp;
    <a href="${pageContext.request.contextPath}/pedidos/kanban">Ver tablero Kanban (CU2) →</a>

</div>
</body>
</html>
