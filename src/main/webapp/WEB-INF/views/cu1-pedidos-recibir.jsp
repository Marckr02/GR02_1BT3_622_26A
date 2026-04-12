<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, model.Pedido, model.Marca" %>
<!DOCTYPE html>
<html lang="es">
<head>
 <meta charset="UTF-8">
 <title>CU1 – Recibir Pedidos</title>
 <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
 
</head>
<body>
<nav class="navbar">
 <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp"> DARK KITCHEN</a>
 <div class="navbar-links">
 <a href="${pageContext.request.contextPath}/pedidos/recibir" class="active">CU1 · Pedidos</a>
 <a href="${pageContext.request.contextPath}/pedidos/kanban">CU2 · Kanban</a>
 <a href="${pageContext.request.contextPath}/insumos/entrada">CU3 · Insumos</a>
 <a href="${pageContext.request.contextPath}/menu/bloqueo">CU4 · Bloqueo</a>
 </div>
</nav>
<div class="container">

 <div class="page-header"><h2>CU1 – Recibir Pedidos</h2></div>
 

 <%-- Mensajes de resultado --%>
 <% if ("1".equals(request.getParameter("ok"))) { %>
 <div class="msg-ok"> Pedido recibido correctamente y añadido al tablero.</div>
 <% } %>
 <% if (request.getAttribute("error") != null) { %>
 <div class="msg-error"> Error de recepción: <%= request.getAttribute("error") %></div>
 <% } %>

 <%-- Panel de simulación (representa la Plataforma de Delivery) --%>
 <div class="sim-panel">
 <h3> Simular Pedido Entrante (Plataforma Externa)</h3>
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
 <% }
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
 <button type="submit" class="btn btn-primary"> Enviar Pedido al Sistema</button>
 </form>
 </div>

 <%-- Lista de pedidos recibidos --%>
 <h3 style="margin-bottom:0.75rem;">
 Pedidos en estado RECIBIDO
 <% List<Pedido> pedidos = (List<Pedido>) request.getAttribute("pedidos"); %>
 <span style="font-weight:400; font-size:0.9rem; color:var(--text-muted);">
 (<%= pedidos != null ? pedidos.size() : 0 %> pedido(s))
 </span>
 </h3>

 <% if (pedidos == null || pedidos.isEmpty()) { %>
 <p style="color:var(--text-muted); font-style:italic;">
 No hay pedidos en estado RECIBIDO. Usa el formulario de arriba para simular uno.
 </p>
 <% } else { %>
 <div class="table-wrap"><table>
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
 <td style="font-size:0.82rem; color:var(--text-muted);">
 <%= p.getTimestamp().toLocalDate() %>
 <%= p.getTimestamp().toLocalTime().withSecond(0).withNano(0) %>
 </td>
 <td><code><%= p.getEstado() %></code></td>
 </tr>
 <% } %>
 </tbody>
 </table></div>
 <% } %>

 <hr style="margin:2rem 0; border:none; border-top:1px solid #e5e7eb;">
 <a href="${pageContext.request.contextPath}/index.jsp">← Volver al inicio</a>
 &nbsp;&nbsp;
 <a href="${pageContext.request.contextPath}/pedidos/kanban">Ver tablero Kanban (CU2) →</a>

</div>
</body>
</html>
