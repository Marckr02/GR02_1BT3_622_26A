package dao;

import config.HibernateUtil;
import model.AlertaStock;
import model.Insumo;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

/**
 * Implementación Hibernate de AlertaDao.
 */
public class AlertaDaoHibernate
        extends GenericHibernateDao<AlertaStock, Long>
        implements AlertaDao {

    public AlertaDaoHibernate() {
        super(AlertaStock.class);
    }

    /**
     * Busca la alerta activa (activa = true) para el insumo indicado.
     * Solo puede existir una alerta activa por insumo a la vez.
     */
    @Override
    public Optional<AlertaStock> findActivaByInsumo(Insumo insumo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM AlertaStock a WHERE a.insumo = :insumo AND a.activa = true";
            AlertaStock alerta = session.createQuery(hql, AlertaStock.class)
                    .setParameter("insumo", insumo)
                    .uniqueResult();
            return Optional.ofNullable(alerta);
        }
    }

    /**
     * Devuelve todas las alertas ordenadas de más reciente a más antigua.
     */
    @Override
    public List<AlertaStock> findAllOrdenadas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM AlertaStock a ORDER BY a.timestamp DESC";
            return session.createQuery(hql, AlertaStock.class).getResultList();
        }
    }
}
