package service;

import dao.ProveedorDao;
import dao.ProveedorDaoHibernate;
import model.Proveedor;

/**
 * ProveedorService — FASE REFACTOR.
 *
 * ─── HISTORIA TDD ─────────────────────────────────────────────────────────────
 *
 * FASE RED (Tests 1–4 FALLAN):
 *   Se escribieron primero los cuatro tests en ProveedorServiceTest con el DAO
 *   mockeado. En esta fase, registrarProveedor() no existía, por lo que los
 *   tests fallaban con errores de compilación y/o AssertionError.
 *
 * FASE GREEN (Tests 1–4 PASAN — implementación mínima):
 *   Se implementó registrarProveedor() con la lógica mínima necesaria:
 *     1. Validar que ningún campo sea nulo o vacío → lanzar IllegalArgumentException.
 *     2. Llamar a proveedorDao.save(proveedor) y retornar el objeto guardado.
 *   Los cuatro tests pasaron sin refactorización adicional.
 *
 * FASE REFACTOR (código actual):
 *   REFACTOR 1 — Extraer método privado validarCamposObligatorios():
 *     La validación de los tres campos se concentra en un único método privado
 *     con nombre expresivo, eliminando la duplicación de llamadas a esCampoVacio()
 *     dentro de registrarProveedor() y mejorando la legibilidad.
 *
 *   REFACTOR 2 — Extraer método privado esCampoVacio():
 *     La condición de campo nulo/vacío/solo-espacios se encapsula en un método
 *     reutilizable. Esto evita repetir "== null || .trim().isEmpty()" en cada
 *     validación y centraliza la lógica de "campo en blanco".
 *
 *   Los cuatro tests siguen pasando sin ninguna modificación.
 *
 * ─── TRAZABILIDAD ─────────────────────────────────────────────────────────────
 *
 * HU1 – Iteración 1: Registrar Proveedor
 *   Criterio de Aceptación Escenario 1 → cubierto por Test 1 y Test 3
 *   Criterio de Aceptación Escenario 2 → cubierto por Test 2 y Test 4
 *
 * Diagrama de Robustez:
 *   ProveedorService actúa como objeto de Control entre el servlet (Boundary)
 *   y el ProveedorDao (Entidad).
 *
 * Tarea T1.2 — HU1, Iteración 1
 */
public class ProveedorService {

    private static final String ERROR_CAMPOS_REQUERIDOS =
            "Todos los campos son obligatorios: nombre, teléfono y correo.";

    private final ProveedorDao proveedorDao;

    // ── Constructores ─────────────────────────────────────────────────────

    public ProveedorService(ProveedorDao proveedorDao) {
        this.proveedorDao = proveedorDao;
    }

    public ProveedorService() {
        this(new ProveedorDaoHibernate());
    }

    // ── Caso de Uso Principal ─────────────────────────────────────────────

    /**
     * registrarProveedor(proveedor) : Proveedor
     *
     * Orquesta el registro de un nuevo proveedor:
     *   1. validarCamposObligatorios() → verifica nombre, teléfono y correo.
     *   2. proveedorDao.save()         → persiste el proveedor en la BD.
     *
     * Lanza IllegalArgumentException si algún campo obligatorio está vacío.
     *
     * Criterio de Aceptación HU1:
     *   Escenario 1 — campos completos: retorna el proveedor registrado.
     *   Escenario 2 — campos vacíos   : lanza IllegalArgumentException.
     */
    public Proveedor registrarProveedor(Proveedor proveedor) {
        validarCamposObligatorios(proveedor);
        return proveedorDao.save(proveedor);
    }

    /**
     * listarProveedores() : List<Proveedor>
     *
     * Retorna todos los proveedores registrados en la BD.
     * Usado por ProveedorServlet.doGet() para poblar la tabla de la vista.
     * (Preparación para HU3 — Consultar Listado de Proveedores)
     */
    public java.util.List<Proveedor> listarProveedores() {
        return proveedorDao.findAll();
    }

    // ── FASE REFACTOR: métodos privados extraídos ─────────────────────────

    /**
     * REFACTOR 1: Extraído de registrarProveedor().
     * Centraliza la validación de los tres campos obligatorios.
     * Lanza IllegalArgumentException con mensaje claro si alguno falla.
     */
    private void validarCamposObligatorios(Proveedor proveedor) {
        if (esCampoVacio(proveedor.getNombre())
                || esCampoVacio(proveedor.getTelefono())
                || esCampoVacio(proveedor.getCorreo())) {
            throw new IllegalArgumentException(ERROR_CAMPOS_REQUERIDOS);
        }
    }

    /**
     * REFACTOR 2: Extraído de validarCamposObligatorios().
     * Determina si un campo de texto es nulo, vacío o solo espacios en blanco.
     */
    private boolean esCampoVacio(String campo) {
        return campo == null || campo.trim().isEmpty();
    }
}
