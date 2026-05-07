package dao;

import model.Proveedor;

/**
 * ProveedorDao — contrato de persistencia para la entidad Proveedor.
 *
 * Extiende GenericDao para heredar save / update / delete / findById / findAll.
 *
 * Tarea T1.2 — HU1, Iteración 1 (Diagrama de Robustez: Entidad → DAO)
 */
public interface ProveedorDao extends GenericDao<Proveedor, Long> {
    // Hereda: save, update, delete, findById, findAll
    // Se pueden agregar consultas específicas en futuras historias (ej. findByNombre)
}
