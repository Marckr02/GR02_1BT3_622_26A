package dao;

import config.HibernateUtil;
import model.Proveedor;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

/**
 * Implementación Hibernate de ProveedorDao.
 *
 * Trazabilidad – TAREA 2.1 HU3 (Rubén)
 *
 * Refactorización: las consultas HQL se extraen como constantes
 * para evitar literales duplicados y facilitar el mantenimiento.
 */
public class ProveedorDaoHibernate
        extends GenericHibernateDao<Proveedor, Long>
        implements ProveedorDao {

    // ── Consultas HQL centralizadas (refactorización) ─────────────────────────
    private static final String HQL_BY_CORREO  =
            "FROM Proveedor p WHERE p.correo = :correo";
    private static final String HQL_ALL_SORTED =
            "FROM Proveedor p ORDER BY p.nombre ASC";

    public ProveedorDaoHibernate() {
        super(Proveedor.class);
    }

    /**
     * Busca un proveedor por correo electrónico.
     * Usado por ProveedorService para validar duplicados.
     */
    @Override
    public Optional<Proveedor> findByCorreo(String correo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Proveedor proveedor = session.createQuery(HQL_BY_CORREO, Proveedor.class)
                    .setParameter("correo", correo)
                    .uniqueResult();
            return Optional.ofNullable(proveedor);
        }
    }

    /**
     * Devuelve todos los proveedores ordenados alfabéticamente por nombre.
     */
    @Override
    public List<Proveedor> findAllOrdenados() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(HQL_ALL_SORTED, Proveedor.class).getResultList();
        }
    }
}