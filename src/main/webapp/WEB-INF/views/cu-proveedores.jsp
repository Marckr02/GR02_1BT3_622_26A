<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Proveedor" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Proveedores</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <style>
        .proveedores-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 1.25rem;
        }
        .proveedores-header h2 {
            font-size: 1.1rem;
            font-weight: 700;
            color: var(--text);
        }
        .tabla-proveedores {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.88rem;
        }
        .tabla-proveedores thead tr {
            background: var(--bg2);
            border-bottom: 2px solid var(--border);
        }
        .tabla-proveedores th {
            text-align: left;
            padding: 0.65rem 0.85rem;
            font-weight: 600;
            color: var(--text-muted, #6b7280);
            text-transform: uppercase;
            font-size: 0.75rem;
            letter-spacing: 0.04em;
        }
        .tabla-proveedores td {
            padding: 0.65rem 0.85rem;
            border-bottom: 1px solid var(--border);
            color: var(--text);
            vertical-align: middle;
        }
        .tabla-proveedores tbody tr:hover {
            background: var(--bg2);
        }
        .form-registro {
            background: var(--bg2);
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 1.25rem;
            margin-bottom: 1.5rem;
        }
        .form-registro h3 {
            font-size: 0.95rem;
            font-weight: 600;
            margin-bottom: 1rem;
            color: var(--text);
        }
        .form-row {
            display: flex;
            gap: 0.75rem;
            flex-wrap: wrap;
            margin-bottom: 0.75rem;
        }
        .form-row input {
            flex: 1;
            min-width: 180px;
            padding: 0.5rem 0.75rem;
            border: 1px solid var(--border);
            border-radius: 6px;
            font-size: 0.88rem;
            background: var(--bg);
            color: var(--text);
        }
        .btn-registrar {
            padding: 0.5rem 1.25rem;
            background: var(--accent, #2563eb);
            color: #fff;
            border: none;
            border-radius: 6px;
            font-size: 0.88rem;
            font-weight: 500;
            cursor: pointer;
        }
        .btn-registrar:hover { opacity: 0.88; }
        .msg-ok    { color: #16a34a; font-size: 0.88rem; margin-bottom: 0.75rem; }
        .msg-error { color: #dc2626; font-size: 0.88rem; margin-bottom: 0.75rem; }
        .empty-state {
            text-align: center;
            padding: 3rem 1rem;
            color: var(--text-muted, #6b7280);
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
<%
    List<Proveedor> proveedores = (List<Proveedor>) request.getAttribute("proveedores");
    String mensaje = (String) request.getAttribute("mensaje");
    String error   = (String) request.getAttribute("error");
%>

<div class="container" style="max-width: 900px; margin: 2rem auto; padding: 0 1rem;">

    <div class="proveedores-header">
        <h2>🏭 Proveedores</h2>
        <a href="${pageContext.request.contextPath}/dashboard"
           style="font-size:0.85rem; color:var(--accent,#2563eb); text-decoration:none;">
            ← Volver al Dashboard
        </a>
    </div>

    <%-- Mensajes de estado --%>
    <% if (mensaje != null) { %>
    <p class="msg-ok">✅ <%= mensaje %></p>
    <% } %>
    <% if (error != null) { %>
    <p class="msg-error">⚠️ <%= error %></p>
    <% } %>

    <%-- Formulario de registro --%>
    <div class="form-registro">
        <h3>Registrar nuevo proveedor</h3>
        <form method="post" action="${pageContext.request.contextPath}/proveedores/lista">
            <div class="form-row">
                <input type="text"  name="nombre"   placeholder="Nombre *"   required />
                <input type="text"  name="telefono" placeholder="Teléfono *" required />
                <input type="email" name="correo"   placeholder="Correo *"   required />
            </div>
            <button type="submit" class="btn-registrar">+ Registrar</button>
        </form>
    </div>

    <%-- Tabla de proveedores --%>
    <% if (proveedores == null || proveedores.isEmpty()) { %>
    <div class="empty-state">
        No hay proveedores registrados aún.
    </div>
    <% } else { %>
    <table class="tabla-proveedores">
        <thead>
        <tr>
            <th>#</th>
            <th>Nombre</th>
            <th>Teléfono</th>
            <th>Correo</th>
        </tr>
        </thead>
        <tbody>
        <%
            int idx = 1;
            for (Proveedor p : proveedores) {
        %>
        <tr>
            <td><%= idx++ %></td>
            <td><strong><%= p.getNombre() %></strong></td>
            <td><%= p.getTelefono() %></td>
            <td><%= p.getCorreo() %></td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <p style="margin-top:0.75rem; font-size:0.8rem; color:var(--text-muted,#6b7280);">
        Total: <%= proveedores.size() %> proveedor(es) registrado(s).
    </p>
    <% } %>
</div>
</body>
</html>