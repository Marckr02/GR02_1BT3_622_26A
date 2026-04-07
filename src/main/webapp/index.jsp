<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dark Kitchen - Base del Sistema</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
</head>
<body>
<div class="container">
    <h1>Dark Kitchen</h1>
    <p>Base del sistema lista para implementar los casos de uso.</p>

    <ul>
        <li><a href="${pageContext.request.contextPath}/pedidos/recibir">CU1 - Recibir Pedidos</a></li>
        <li><a href="${pageContext.request.contextPath}/pedidos/kanban">CU2 - Kanban de Produccion</a></li>
        <li><a href="${pageContext.request.contextPath}/insumos/entrada">CU3 - Entrada de Insumos</a></li>
        <li><a href="${pageContext.request.contextPath}/menu/bloqueo">CU4 - Bloqueo de Menu por Stock</a></li>
    </ul>
</div>
</body>
</html>
