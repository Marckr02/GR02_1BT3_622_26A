<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>CU2 - Kanban de Pedidos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/kanban-styles.css">
</head>
<body>
<div class="container">
    <h2>CU2 - Kanban de Pedidos</h2>

   <div class="kanban-board" id="kanban-board">

           <!-- Columna RECIBIDO -->
           <div class="kanban-column">
               <h3>RECIBIDO</h3>
               <c:set var="primerRecibidoMostrado" value="false" />
               <c:forEach items="${pedidos}" var="pedido">
                   <c:if test="${pedido.estado.name() == 'RECIBIDO'}">
                       <div class="kanban-card ${pedido.prioridadCss}">
                           <div class="card-header">
                               <span class="card-id">P-${pedido.id}</span>
                               <span class="card-priority">${pedido.prioridadLabel}</span>
                           </div>
                           <div class="card-body">
                               <p><strong>${pedido.nombreCliente}</strong></p>
                               <p>${pedido.marca.nombre}</p>
                               <p>${pedido.plataformaOrigen}</p>
                           </div>

                           <c:if test="${primerRecibidoMostrado == 'false'}">
                               <form method="post" action="${pageContext.request.contextPath}/pedidos/kanban">
                                   <input type="hidden" name="pedidoId" value="${pedido.id}" />
                                   <button type="submit" class="btn-avanzar">Avanzar ➜</button>
                               </form>
                               <c:set var="primerRecibidoMostrado" value="true" />
                           </c:if>

                       </div>
                   </c:if>
               </c:forEach>
           </div>

           <!-- Columna EN PREPARACIÓN -->
           <div class="kanban-column">
               <h3>EN PREPARACIÓN</h3>
               <c:forEach items="${pedidos}" var="pedido">
                   <c:if test="${pedido.estado.name() == 'EN_PREP'}">
                       <div class="kanban-card ${pedido.prioridadCss}">
                           <div class="card-header">
                               <span class="card-id">P-${pedido.id}</span>
                               <span class="card-priority">${pedido.prioridadLabel}</span>
                           </div>
                           <div class="card-body">
                               <p><strong>${pedido.nombreCliente}</strong></p>
                               <p>${pedido.marca.nombre}</p>
                               <p>${pedido.plataformaOrigen}</p>
                           </div>
                           <form method="post" action="${pageContext.request.contextPath}/pedidos/kanban">
                               <input type="hidden" name="pedidoId" value="${pedido.id}" />
                               <button type="submit" class="btn-avanzar">Avanzar ➜</button>
                           </form>
                       </div>
                   </c:if>
               </c:forEach>
           </div>

           <!-- Columna LISTO -->
           <div class="kanban-column">
               <h3>LISTO</h3>
               <c:forEach items="${pedidos}" var="pedido">
                   <c:if test="${pedido.estado.name() == 'LISTO'}">
                       <div class="kanban-card ${pedido.prioridadCss}">
                           <div class="card-header">
                               <span class="card-id">P-${pedido.id}</span>
                               <span class="card-priority">${pedido.prioridadLabel}</span>
                           </div>
                           <div class="card-body">
                               <p><strong>${pedido.nombreCliente}</strong></p>
                               <p>${pedido.marca.nombre}</p>
                               <p>${pedido.plataformaOrigen}</p>
                           </div>
                           <form method="post" action="${pageContext.request.contextPath}/pedidos/kanban">
                               <input type="hidden" name="pedidoId" value="${pedido.id}" />
                               <button type="submit" class="btn-avanzar">Avanzar ➜</button>
                           </form>
                       </div>
                   </c:if>
               </c:forEach>
           </div>

           <!-- Columna ENTREGADO (sin botón, es el estado final) -->
           <div class="kanban-column">
               <h3>ENTREGADO</h3>
               <c:forEach items="${pedidos}" var="pedido">
                   <c:if test="${pedido.estado.name() == 'ENTREGADO'}">
                       <div class="kanban-card ${pedido.prioridadCss}">
                           <div class="card-header">
                               <span class="card-id">P-${pedido.id}</span>
                               <span class="card-priority">${pedido.prioridadLabel}</span>
                           </div>
                           <div class="card-body">
                               <p><strong>${pedido.nombreCliente}</strong></p>
                               <p>${pedido.marca.nombre}</p>
                               <p>${pedido.plataformaOrigen}</p>
                           </div>
                       </div>
                   </c:if>
               </c:forEach>
           </div>

       </div>

    <a href="${pageContext.request.contextPath}/index.jsp">Volver al inicio</a>
</div>
</body>
</html>