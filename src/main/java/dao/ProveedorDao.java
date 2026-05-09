package dao;

import model.Proveedor;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para Proveedor.
 * Extiende GenericDao para heredar save / update / delete / findById / findAll.
 *
 * Trazabilidad – TAREA 1.2 HU3 (Marco)
 */
public interface ProveedorDao extends GenericDao<Proveedor, Long> {

    /**
     * Busca un proveedor por su correo electrónico.
     * Útil para validar duplicados antes de registrar.
     *
     * @param correo correo a buscar
     * @return Optional con el proveedor si existe, vacío si no
     */
    Optional<Proveedor> findByCorreo(String correo);

    /**
     * Devuelve todos los proveedores ordenados alfabéticamente por nombre.
     */
    List<Proveedor> findAllOrdenados();
}
