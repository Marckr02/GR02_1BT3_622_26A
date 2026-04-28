<%
    if (session.getAttribute("usuarioActivo") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dark Kitchen — Sistema de Gestión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
</head>
<body>

<nav class="navbar">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">Dark Kitchen</a>
    <div class="navbar-links">
        <a href="${pageContext.request.contextPath}/pedidos/recibir">CU1 · Pedidos</a>
        <a href="${pageContext.request.contextPath}/pedidos/kanban">CU2 · Kanban</a>
        <a href="${pageContext.request.contextPath}/insumos/entrada">CU3 · Insumos</a>
        <a href="${pageContext.request.contextPath}/menu/bloqueo">CU4 · Bloqueo</a>
    </div>
</nav>

<div class="container">

    <div class="page-header">
        <h2>Sistema de Gestión para Dark Kitchens Colaborativas</h2>
        <p>Plataforma de gestión de pedidos e inventario · GR2SW — Escuela Politécnica Nacional</p>
    </div>

    <div class="stat-grid">
        <a href="${pageContext.request.contextPath}/pedidos/recibir" class="stat-card">
            <div class="cu-label">Incremento 1 · CU1</div>
            <div class="cu-title">Recibir Pedidos</div>
            <div class="cu-desc">Recepción y validación de pedidos desde plataformas de delivery externas</div>
        </a>
        <a href="${pageContext.request.contextPath}/pedidos/kanban" class="stat-card">
            <div class="cu-label">Incremento 1 · CU2</div>
            <div class="cu-title">Tablero Kanban</div>
            <div class="cu-desc">Actualización del estado de preparación a través del flujo de producción</div>
        </a>
        <a href="${pageContext.request.contextPath}/insumos/entrada" class="stat-card">
            <div class="cu-label">Incremento 2 · CU3</div>
            <div class="cu-title">Entrada de Insumos</div>
            <div class="cu-desc">Registro de insumos compartidos y actualización del inventario centralizado</div>
        </a>
        <a href="${pageContext.request.contextPath}/menu/bloqueo" class="stat-card">
            <div class="cu-label">Incremento 2 · CU4</div>
            <div class="cu-title">Bloqueo de Menú</div>
            <div class="cu-desc">Desactivación automática de platos cuando los insumos alcanzan nivel crítico</div>
        </a>
    </div>

    <div style="padding: 1rem 1.25rem; background: var(--bg); border: 1px solid var(--border); border-radius: var(--radius); margin-top: 1rem;">
        <p style="font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600; color: var(--text-muted); margin-bottom: 0.4rem;">Integrantes</p>
        <p style="color: var(--text-muted); font-size: 0.875rem;">
            Rubén Cuenca &nbsp;·&nbsp; Jeremy Jiménez &nbsp;·&nbsp; Álvaro Montalván &nbsp;·&nbsp; Marco Ríos
        </p>
    </div>

</div>
</body>
</html>
