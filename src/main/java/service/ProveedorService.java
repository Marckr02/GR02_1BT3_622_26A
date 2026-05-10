package service;

import dao.ProveedorDao;
import dao.ProveedorDaoHibernate;
import model.Proveedor;

import java.util.List;

/**
 * Servicio de negocio para la gestión de proveedores.
 *
 * Trazabilidad – TAREA 2.2 HU3 (Rubén)
 *
 * Refactorización: la validación de campos se extrae al método privado
 * validarCampos() para que registrar() actúe como orquestador limpio.
 */
public class ProveedorService {

    private final ProveedorDao proveedorDao;

    public ProveedorService() {
        this(ServiceFactory.createProveedorDao());
    }

    // Constructor para inyección en tests
    public ProveedorService(ProveedorDao proveedorDao) {
        this.proveedorDao = proveedorDao;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REGISTRO
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Registra un nuevo proveedor en el sistema.
     * Valida campos obligatorios y unicidad de correo antes de persistir.
     *
     * @param nombre   nombre del proveedor
     * @param telefono teléfono de contacto
     * @param correo   correo electrónico (debe ser único)
     * @return el proveedor persistido
     * @throws IllegalArgumentException si algún campo es inválido o el correo ya existe
     */
    public Proveedor registrar(String nombre, String telefono, String correo) {
        validarCampos(nombre, telefono, correo);
        validarCorreoUnico(correo);
        Proveedor proveedor = new Proveedor(nombre, telefono, correo);
        return proveedorDao.save(proveedor);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CONSULTA
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devuelve todos los proveedores ordenados alfabéticamente.
     */
    public List<Proveedor> listar() {
        return proveedorDao.findAllOrdenados();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VALIDACIONES (refactorización: extraídas como métodos privados)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Valida que los campos obligatorios no sean nulos, vacíos ni solo espacios.
     * Centraliza todas las validaciones de formato en un único punto.
     */
    private void validarCampos(String nombre, String telefono, String correo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono del proveedor es obligatorio.");
        }
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo del proveedor es obligatorio.");
        }
        if (!correo.contains("@")) {
            throw new IllegalArgumentException("El correo del proveedor no es válido.");
        }
    }

    /**
     * Verifica que el correo no esté registrado por otro proveedor.
     */
    private void validarCorreoUnico(String correo) {
        proveedorDao.findByCorreo(correo.trim()).ifPresent(p -> {
            throw new IllegalArgumentException(
                    "Ya existe un proveedor registrado con el correo: " + correo);
        });
    }
}