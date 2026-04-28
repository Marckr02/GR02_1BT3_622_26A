package dao;

import config.HibernateUtil;
import model.Usuario;
import org.hibernate.Session;

import java.util.Optional;

/**
 * UsuarioDaoHibernate — implementación Hibernate de UsuarioDao.
 *
 * Tarea 1 — HU8 (Diagrama de Robustez: Entidad)
 * Hereda operaciones CRUD de GenericHibernateDao.
 */
public class UsuarioDaoHibernate extends GenericHibernateDao<Usuario, Long>
        implements UsuarioDao {

    public UsuarioDaoHibernate() {
        super(Usuario.class);
    }

    /**
     * Busca usuario por username usando HQL.
     * Retorna Optional.empty() si no existe, sin lanzar excepción.
     */
    @Override
    public Optional<Usuario> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }
}
