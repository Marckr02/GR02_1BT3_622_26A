<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>CU2 - Kanban de Pedidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
</head>
<body>
<div class="container">
    <h2>CU2 - Kanban de Pedidos</h2>
    <p>Tablero estatico inicial para estados de preparacion.</p>

    <div class="kanban-board" id="kanban-board"></div>

    <a href="${pageContext.request.contextPath}/index.jsp">Volver al inicio</a>
</div>
<script src="${pageContext.request.contextPath}/resources/js/kanban.js"></script>
</body>
</html>

