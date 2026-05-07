package dao;

import model.Proveedor;

/**
 * ProveedorDaoHibernate — implementación Hibernate de ProveedorDao.
 *
 * Hereda todas las operaciones CRUD de GenericHibernateDao:
 *   save(), update(), delete(), findById(), findAll()
 *
 * Tarea T1.2 — HU1, Iteración 1 (Diagrama de Robustez: Entidad)
 */
public class ProveedorDaoHibernate extends GenericHibernateDao<Proveedor, Long>
        implements ProveedorDao {

    public ProveedorDaoHibernate() {
        super(Proveedor.class);
    }
}
