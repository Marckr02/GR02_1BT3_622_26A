<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.AlertaStock, model.NivelAlerta" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Historial de Alertas de Stock</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <style>
        .alertas-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 1.25rem;
        }
        .alertas-header h2 {
            font-size: 1.1rem;
            font-weight: 700;
            color: var(--text);
        }
        .tabla-alertas {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.88rem;
        }
        .tabla-alertas thead tr {
            background: var(--bg2);
            border-bottom: 2px solid var(--border);
        }
        .tabla-alertas th {
            text-align: left;
            padding: 0.65rem 0.85rem;
            font-weight: 600;
            color: var(--text-muted, #6b7280);
            text-transform: uppercase;
            font-size: 0.75rem;
            letter-spacing: 0.04em;
        }
        .tabla-alertas td {
            padding: 0.65rem 0.85rem;
            border-bottom: 1px solid var(--border);
            color: var(--text);
            vertical-align: middle;
        }
        .tabla-alertas tbody tr:hover {
            background: var(--bg2);
        }
        .badge {
            display: inline-block;
            padding: 0.2rem 0.6rem;
            border-radius: 9999px;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }
        .badge-critico {
            background: #fef2f2;
            color: #dc2626;
            border: 1px solid #fca5a5;
        }
        .badge-advertencia {
            background: #fffbeb;
            color: #d97706;
            border: 1px solid #fcd34d;
        }
        .badge-activa {
            background: #eff6ff;
            color: #2563eb;
            border: 1px solid #93c5fd;
        }
        .badge-resuelta {
            background: #f0fdf4;
            color: #16a34a;
            border: 1px solid #86efac;
        }
        .empty-state {
            text-align: center;
            padding: 3rem 1rem;
            color: var(--text-muted, #6b7280);
            font-size: 0.9rem;
        }
        .empty-state span {
            display: block;
            font-size: 2rem;
            margin-bottom: 0.5rem;
        }
    </style>
</head>
<body>
<%-- Incluir navbar si existe --%>
<%
    // Recuperar la lista de alertas inyectada por AlertaHistorialServlet
    List<AlertaStock> alertas = (List<AlertaStock>) request.getAttribute("alertas");
%>

<div class="container" style="max-width: 1000px; margin: 2rem auto; padding: 0 1rem;">

    <div class="alertas-header">
        <h2>📋 Historial de Alertas de Stock</h2>
        <a href="${pageContext.request.contextPath}/dashboard"
           style="font-size:0.85rem; color: var(--accent, #2563eb); text-decoration:none;">
            ← Volver al Dashboard
        </a>
    </div>

    <% if (alertas == null || alertas.isEmpty()) { %>
    <div class="empty-state">
        <span>✅</span>
        No existen alertas de stock registradas en el sistema.
    </div>
    <% } else { %>
    <table class="tabla-alertas">
        <thead>
        <tr>
            <th>#</th>
            <th>Insumo</th>
            <th>Nivel</th>
            <th>Fecha / Hora</th>
            <th>Estado</th>
            <th>Fecha Resolución</th>
        </tr>
        </thead>
        <tbody>
        <%
            int idx = 1;
            for (AlertaStock alerta : alertas) {
                String badgeNivel = alerta.getNivel() == NivelAlerta.CRITICO
                        ? "badge-critico" : "badge-advertencia";
                String textoNivel = alerta.getNivel() == NivelAlerta.CRITICO
                        ? "Crítico" : "Advertencia";
                String badgeEstado = alerta.isActiva() ? "badge-activa"  : "badge-resuelta";
                String textoEstado = alerta.isActiva() ? "Activa"        : "Resuelta";
                String fechaRes    = alerta.getFechaResolucion() != null
                        ? alerta.getFechaResolucion().toString().replace("T", " ").substring(0, 16)
                        : "—";
                String fechaTs     = alerta.getTimestamp() != null
                        ? alerta.getTimestamp().toString().replace("T", " ").substring(0, 16)
                        : "—";
        %>
        <tr>
            <td><%= idx++ %></td>
            <td><strong><%= alerta.getInsumo().getNombre() %></strong>
                <small style="color:var(--text-muted,#6b7280);">
                    (<%= alerta.getInsumo().getUnidad() %>)
                </small>
            </td>
            <td><span class="badge <%= badgeNivel %>"><%= textoNivel %></span></td>
            <td><%= fechaTs %></td>
            <td><span class="badge <%= badgeEstado %>"><%= textoEstado %></span></td>
            <td><%= fechaRes %></td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <p style="margin-top:0.75rem; font-size:0.8rem; color:var(--text-muted,#6b7280);">
        Total: <%= alertas.size() %> alerta(s) registrada(s).
    </p>
    <% } %>
</div>
</body>
</html>