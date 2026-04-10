<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CU3 – Entrada de Insumos | Dark Kitchen</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/insumos.css">
</head>
<body>

<div class="container">

    <%-- ── Encabezado ─────────────────────────────────────────────────────── --%>
    <div class="page-header">
        <div>
            <h2>🥦 CU3 – Entrada de Insumos Compartidos</h2>
            <p class="subtitle">Inventario centralizado · Incremento 2: Control de Inventario</p>
        </div>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn-back">← Volver al inicio</a>
    </div>

    <%-- ── Mensajes de resultado ──────────────────────────────────────────── --%>
    <c:if test="${not empty param.reducidoOk}">
        <div class="alert alert-success">✅ Stock reducido correctamente.</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">⚠️ ${error}</div>
    </c:if>
    <c:if test="${not empty errorReduccion}">
        <div class="alert alert-error">⚠️ ${errorReduccion}</div>
    </c:if>

    <%-- ── Comprobante de recepción (Generador de Comprobantes) ──────────── --%>
    <%--  Trazabilidad → DA CU3 paso "Generar comprobante de recepción exitosa"  --%>
    <c:if test="${not empty comprobante}">
        <div class="comprobante">
            <h3>
                <c:choose>
                    <c:when test="${comprobante.estado == 'DISCREPANCIA'}">
                        ⚠️ Recepción registrada con DISCREPANCIA
                    </c:when>
                    <c:otherwise>
                        ✅ Comprobante de Recepción – Orden #${comprobante.id}
                    </c:otherwise>
                </c:choose>
            </h3>
            <p><strong>Factura:</strong> ${comprobante.numeroFactura} &nbsp;|&nbsp;
               <strong>Proveedor:</strong> ${comprobante.nombreProveedor} &nbsp;|&nbsp;
               <strong>Fecha:</strong> ${comprobante.fechaFactura} &nbsp;|&nbsp;
               <strong>Estado:</strong>
               <span class="badge badge-${comprobante.estado == 'DISCREPANCIA' ? 'warn' : 'ok'}">
                   ${comprobante.estado}
               </span>
            </p>
            <table class="tbl">
                <thead>
                    <tr>
                        <th>Insumo</th><th>Cant. Pedida</th><th>Cant. Recibida</th>
                        <th>Precio Unit.</th><th>Discrepancia</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="det" items="${comprobante.detalles}">
                        <tr class="${det.hayDiscrepancia() ? 'row-warn' : ''}">
                            <td>${det.insumo.nombre}</td>
                            <td>${det.cantidadPedida} ${det.insumo.unidad}</td>
                            <td>${det.cantidadRecibida} ${det.insumo.unidad}</td>
                            <td>$<fmt:formatNumber value="${det.precioUnitario}" pattern="0.00"/></td>
                            <td>${det.hayDiscrepancia() ? '⚠️ Sí' : '✓'}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <%-- ══════════════════════════════════════════════════════════════════════
         SECCIÓN 1: INVENTARIO CENTRALIZADO (lista de todos los insumos)
         Trazabilidad → DA CU3: "listar inventario centralizado"
         ══════════════════════════════════════════════════════════════════════ --%>
    <section class="seccion">
        <div class="seccion-header">
            <h3>📦 Inventario Centralizado</h3>
            <span class="badge badge-info">${insumos.size()} insumos</span>
        </div>

        <table class="tbl" id="tablaInventario">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Insumo</th>
                    <th>Stock Actual</th>
                    <th>Stock Mínimo</th>
                    <th>Unidad</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="ins" items="${insumos}" varStatus="st">
                    <tr class="${ins.cantidad <= ins.stockMinimo ? 'row-critico' : ''}">
                        <td>${st.count}</td>
                        <td><strong>${ins.nombre}</strong></td>
                        <td>${ins.cantidad}</td>
                        <td>${ins.stockMinimo}</td>
                        <td>${ins.unidad}</td>
                        <td>
                            <c:choose>
                                <c:when test="${ins.cantidad <= ins.stockMinimo}">
                                    <span class="badge badge-warn">⚠️ Crítico</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-ok">✓ OK</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <%-- Botón Reducir → abre modal de reducción --%>
                            <button class="btn btn-danger btn-sm"
                                    onclick="abrirModalReducir(${ins.id}, '${ins.nombre}', ${ins.cantidad}, '${ins.unidad}')">
                                ➖ Reducir
                            </button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <%-- Botón principal para abrir formulario de registro --%>
        <div style="margin-top:1rem;">
            <button class="btn btn-primary" onclick="toggleFormulario()">
                📋 Registrar nueva entrada de insumos
            </button>
        </div>
    </section>

    <%-- ══════════════════════════════════════════════════════════════════════
         SECCIÓN 2: FORMULARIO DE REGISTRO DE ENTRADA
         Trazabilidad → DA CU3: pasos "Cargar datos de la orden de compra / factura"
                               y "Registrar cantidades recibidas por ítem"
         Trazabilidad → Robustez CU3: Interfaz "Interfaz de Registro de Compras"
         ══════════════════════════════════════════════════════════════════════ --%>
    <section class="seccion" id="formularioEntrada" style="display:none;">
        <h3>📝 Nueva Entrada de Insumos</h3>

        <form method="post" action="${pageContext.request.contextPath}/insumos/entrada"
              id="formEntrada">

            <%-- Datos de la factura (Validador de Orden de Compra) --%>
            <fieldset class="fieldset">
                <legend>Datos de la Factura / Orden de Compra</legend>
                <div class="form-row">
                    <div class="form-group">
                        <label for="numeroFactura">N° Factura *</label>
                        <input type="text" id="numeroFactura" name="numeroFactura"
                               placeholder="Ej: FAC-2026-001" required>
                    </div>
                    <div class="form-group">
                        <label for="nombreProveedor">Proveedor / Negocio *</label>
                        <input type="text" id="nombreProveedor" name="nombreProveedor"
                               placeholder="Ej: Distribuidora El Mayorista" required>
                    </div>
                    <div class="form-group">
                        <label for="fechaFactura">Fecha de Factura *</label>
                        <input type="date" id="fechaFactura" name="fechaFactura" required>
                    </div>
                </div>
            </fieldset>

            <%-- Selección de insumos (lista desplegable + cantidades) --%>
            <fieldset class="fieldset">
                <legend>Insumos a Registrar</legend>
                <p class="hint">
                    Seleccione los insumos que llegaron en esta entrega.
                    Complete la cantidad de la factura y la recibida físicamente.
                </p>

                <%-- Fila dinámica para añadir ítems --%>
                <div id="contenedorItems">
                    <div class="item-row" id="item-0">
                        <div class="form-group">
                            <label>Insumo *</label>
                            <select name="insumoId" class="select-insumo" required>
                                <option value="">-- Seleccionar --</option>
                                <c:forEach var="ins" items="${insumos}">
                                    <option value="${ins.id}"
                                            data-nombre="${ins.nombre}"
                                            data-unidad="${ins.unidad}">
                                        ${ins.nombre} (${ins.unidad})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Cant. Factura *</label>
                            <input type="number" name="cantidadPedida_DYN" min="0.01"
                                   step="0.01" placeholder="0.00" required class="input-pedida">
                        </div>
                        <div class="form-group">
                            <label>Cant. Recibida *</label>
                            <input type="number" name="cantidadRecibida_DYN" min="0"
                                   step="0.01" placeholder="0.00" required class="input-recibida">
                        </div>
                        <div class="form-group">
                            <label>Precio Unit. ($) *</label>
                            <input type="number" name="precioUnitario_DYN" min="0"
                                   step="0.01" placeholder="0.00" required class="input-precio">
                        </div>
                        <button type="button" class="btn btn-danger btn-sm"
                                onclick="eliminarItem(this)" title="Quitar fila">✕</button>
                    </div>
                </div>

                <button type="button" class="btn btn-secondary" onclick="agregarItem()">
                    ➕ Añadir otro insumo
                </button>
            </fieldset>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary" onclick="prepararFormulario()">
                    💾 Registrar entrada
                </button>
                <button type="button" class="btn btn-secondary" onclick="toggleFormulario()">
                    Cancelar
                </button>
            </div>
        </form>
    </section>

</div><!-- /container -->

<%-- ══════════════════════════════════════════════════════════════════════════
     MODAL: REDUCIR STOCK
     Trazabilidad → DA CU3 (acción adicional): descontarStock(cantidad) en Insumo
     ══════════════════════════════════════════════════════════════════════════ --%>
<div id="modalReducir" class="modal-overlay" style="display:none;">
    <div class="modal-box">
        <h3>➖ Reducir Stock</h3>
        <p>Insumo: <strong id="modalNombreInsumo"></strong></p>
        <p>Stock actual: <strong id="modalStockActual"></strong>
           <span id="modalUnidadInsumo"></span></p>

        <form method="post" action="${pageContext.request.contextPath}/insumos/entrada">
            <input type="hidden" name="accion" value="reducir">
            <input type="hidden" name="insumoId" id="modalInsumoId">

            <div class="form-group">
                <label for="cantidadReducir">Cantidad a reducir *</label>
                <input type="number" id="cantidadReducir" name="cantidadReducir"
                       min="0.01" step="0.01" placeholder="0.00" required>
                <small id="msgMaxReducir"></small>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-danger">Reducir</button>
                <button type="button" class="btn btn-secondary"
                        onclick="cerrarModal()">Cancelar</button>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/insumos.js"></script>
</body>
</html>
