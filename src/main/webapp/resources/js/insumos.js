/**
 * insumos.js  –  CU3 Entrada de Insumos
 *
 * Funciones:
 *   toggleFormulario()   → muestra/oculta el formulario de registro
 *   agregarItem()        → añade una fila dinámica de insumo
 *   eliminarItem(btn)    → quita una fila dinámica
 *   prepararFormulario() → renombra los campos dinámicos antes del submit
 *   abrirModalReducir()  → abre el modal de reducción de stock
 *   cerrarModal()        → cierra el modal
 */

let itemCounter = 1;

// ── Mostrar / ocultar formulario de registro ─────────────────────────────
function toggleFormulario() {
    const sec = document.getElementById('formularioEntrada');
    const visible = sec.style.display !== 'none';
    sec.style.display = visible ? 'none' : 'block';
    if (!visible) {
        // Poner fecha de hoy por defecto
        const hoy = new Date().toISOString().split('T')[0];
        document.getElementById('fechaFactura').value = hoy;
        sec.scrollIntoView({ behavior: 'smooth' });
    }
}

// ── Agregar fila de ítem ─────────────────────────────────────────────────
function agregarItem() {
    const contenedor = document.getElementById('contenedorItems');
    const template   = document.getElementById('item-0').cloneNode(true);
    template.id = 'item-' + itemCounter++;

    // Limpiar valores clonados
    template.querySelectorAll('input').forEach(i => i.value = '');
    template.querySelector('select').value = '';

    contenedor.appendChild(template);
}

// ── Eliminar fila de ítem ────────────────────────────────────────────────
function eliminarItem(btn) {
    const row = btn.closest('.item-row');
    const contenedor = document.getElementById('contenedorItems');
    if (contenedor.querySelectorAll('.item-row').length > 1) {
        row.remove();
    } else {
        alert('Debe haber al menos un insumo en la orden.');
    }
}

// ── Preparar formulario antes del submit ─────────────────────────────────
// Los inputs tienen name="cantidadPedida_DYN"; los renombramos a
// "cantidadPedida_<insumoId>" para que el servlet pueda leerlos por id.
function prepararFormulario() {
    const rows = document.querySelectorAll('.item-row');
    rows.forEach(row => {
        const select   = row.querySelector('.select-insumo');
        const insumoId = select ? select.value : '';
        if (!insumoId) return;

        const pedida   = row.querySelector('.input-pedida');
        const recibida = row.querySelector('.input-recibida');
        const precio   = row.querySelector('.input-precio');

        if (pedida)   pedida.name   = 'cantidadPedida_'   + insumoId;
        if (recibida) recibida.name = 'cantidadRecibida_' + insumoId;
        if (precio)   precio.name   = 'precioUnitario_'   + insumoId;
    });
    return true;
}

// ── Modal de reducción de stock ──────────────────────────────────────────
function abrirModalReducir(id, nombre, stockActual, unidad) {
    document.getElementById('modalInsumoId').value   = id;
    document.getElementById('modalNombreInsumo').textContent = nombre;
    document.getElementById('modalStockActual').textContent  = stockActual;
    document.getElementById('modalUnidadInsumo').textContent = unidad;

    const input = document.getElementById('cantidadReducir');
    input.max   = stockActual;
    input.value = '';

    document.getElementById('msgMaxReducir').textContent =
        'Máximo permitido: ' + stockActual + ' ' + unidad;

    document.getElementById('modalReducir').style.display = 'flex';
}

function cerrarModal() {
    document.getElementById('modalReducir').style.display = 'none';
}

// Cerrar modal al hacer clic en el overlay
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('modalReducir').addEventListener('click', function(e) {
        if (e.target === this) cerrarModal();
    });
});
