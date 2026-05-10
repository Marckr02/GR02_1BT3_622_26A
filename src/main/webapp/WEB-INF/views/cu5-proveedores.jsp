<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Proveedor" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CU5 – Registrar Proveedor</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <style>
        .section-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 1.25rem;
        }
        .section-header h2 {
            font-size: 1.1rem;
            font-weight: 700;
            color: var(--text);
        }
        /* ── Formulario de registro ── */
        .form-proveedor {
            background: var(--bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 1.25rem;
            margin-bottom: 1.5rem;
        }
        .form-proveedor h3 {
            font-size: 0.95rem;
            font-weight: 600;
            margin-bottom: 1rem;
            color: var(--text);
        }
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr;
            gap: 0.75rem;
            margin-bottom: 1rem;
        }
        .form-group label {
            display: block;
            font-size: 0.75rem;
            font-weight: 600;
            color: var(--text-muted);
            margin-bottom: 0.3rem;
            text-transform: uppercase;
            letter-spacing: 0.04em;
        }
        .form-group input {
            width: 100%;
            box-sizing: border-box;
        }
        .btn-guardar {
            padding: 0.5rem 1.25rem;
            background: var(--accent, #2563eb);
            color: #fff;
            border: none;
            border-radius: var(--radius);
            font-size: 0.875rem;
            font-weight: 600;
            cursor: pointer;
        }
        .btn-guardar:hover {
            opacity: 0.88;
        }
        /* ── Mensajes ── */
        .msg-error {
            background: #fef2f2;
            border: 1px solid #fca5a5;
            color: #dc2626;
            border-radius: var(--radius);
            padding: 0.65rem 1rem;
            font-size: 0.875rem;
            margin-bottom: 1rem;
        }
        .msg-ok {
            background: var(--green-bg, #f0fdf4);
            border: 1px solid #86efac;
            color: var(--green, #16a34a);
            border-radius: var(--radius);
            padding: 0.65rem 1rem;
            font-size: 0.875rem;
            margin-bottom: 1rem;
        }
        /* ── Tabla de proveedores ── */
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
            color: var(--text-muted);
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
        .empty-state {
            text-align: center;
            padding: 2rem;
            color: var(--text-muted);
            font-size: 0.9rem;
        }
        @media (max-width: 700px) {
            .form-row { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>

<%-- ── Navbar ─────────────────────────────────────────────────────────────── --%>
<nav class="navbar">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">Dark Kitchen</a>
    <div class="navbar-links">
        <a href="${pageContext.request.contextPath}/insumos/entrada">CU3 · Insumos</a>
        <a href="${pageContext.request.contextPath}/menu/bloqueo">CU4 · Bloqueo</a>
        <a href="${pageContext.request.contextPath}/proveedores/lista" class="active">CU5 · Proveedores</a>
        <a href="${pageContext.request.contextPath}/login?logout=1"
           onclick="fetch('${pageContext.request.contextPath}/logout', {method:'POST'}).then(()=>window.location='${pageContext.request.contextPath}/login')">
            Cerrar sesión
        </a>
    </div>
</nav>

<div class="container">

    <%-- ── Encabezado ─────────────────────────────────────────────────────── --%>
    <div class="section-header">
        <div>
            <p style="font-size:0.75rem;text-transform:uppercase;letter-spacing:0.5px;font-weight:600;color:var(--text-muted);margin-bottom:0.2rem;">
                Iteración 1 · HU1
            </p>
            <h2>Gestión de Proveedores</h2>
        </div>
    </div>

    <%-- ── Mensaje de registro exitoso (Escenario 1) ─────────────────────── --%>
    <% if ("ok".equals(request.getParameter("registrado"))) { %>
    <div class="msg-ok">✓ Proveedor registrado correctamente y añadido al listado.</div>
    <% } %>

    <%-- ── Mensaje de error — campos vacíos (Escenario 2) ────────────────── --%>
    <% String error = (String) request.getAttribute("error"); %>
    <% if (error != null && !error.isEmpty()) { %>
    <div class="msg-error">⚠ <%= error %></div>
    <% } %>

    <%-- ── Formulario de registro de proveedor ────────────────────────────── --%>
    <div class="form-proveedor">
        <h3>Registrar nuevo proveedor</h3>
        <form method="post" action="${pageContext.request.contextPath}/proveedores/lista">
            <div class="form-row">
                <div class="form-group">
                    <label for="nombre">Nombre *</label>
                    <input type="text" id="nombre" name="nombre"
                           placeholder="Ej. Distribuidora Los Andes"
                           value="${not empty nombre ? nombre : ''}" required>
                </div>
                <div class="form-group">
                    <label for="telefono">Teléfono *</label>
                    <input type="text" id="telefono" name="telefono"
                           placeholder="Ej. 0991234567"
                           value="${not empty telefono ? telefono : ''}" required>
                </div>
                <div class="form-group">
                    <label for="correo">Correo electrónico *</label>
                    <input type="email" id="correo" name="correo"
                           placeholder="Ej. ventas@proveedor.com"
                           value="${not empty correo ? correo : ''}" required>
                </div>
            </div>
            <button type="submit" class="btn-guardar">Guardar</button>
        </form>
    </div>

    <%-- ── Listado de proveedores registrados ─────────────────────────────── --%>
    <div style="background:var(--bg);border:1px solid var(--border);border-radius:var(--radius);padding:1.25rem;">
        <h3 style="font-size:0.95rem;font-weight:600;margin-bottom:1rem;color:var(--text);">
            Proveedores registrados
        </h3>

        <%
            List<Proveedor> proveedores = (List<Proveedor>) request.getAttribute("proveedores");
            if (proveedores == null || proveedores.isEmpty()) {
        %>
        <div class="empty-state">No hay proveedores registrados aún.</div>
        <%
            } else {
        %>
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
                    <td><%= p.getNombre() %></td>
                    <td><%= p.getTelefono() %></td>
                    <td><%= p.getCorreo() %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
        <% } %>
    </div>

</div>
</body>
</html>
