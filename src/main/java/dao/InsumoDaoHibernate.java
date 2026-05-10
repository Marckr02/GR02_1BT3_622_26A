package dao;

import config.HibernateUtil;
import model.Insumo;
import org.hibernate.Session;

import java.util.List;

public class InsumoDaoHibernate extends GenericHibernateDao<Insumo, Long> implements InsumoDao {

    public InsumoDaoHibernate() {
        super(Insumo.class);
    }

    @Override
    public List<Insumo> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select i from Insumo i " +
                    "left join fetch i.proveedor " +
                    "order by i.id",
                    Insumo.class
            ).getResultList();
        }
    }
}

