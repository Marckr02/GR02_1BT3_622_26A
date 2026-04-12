package dao;

import config.HibernateUtil;
import java.util.List;
import model.ItemMenu;
import org.hibernate.Session;

public class ItemMenuDaoHibernate extends GenericHibernateDao<ItemMenu, Long> implements ItemMenuDao {

    public ItemMenuDaoHibernate() {
        super(ItemMenu.class);
    }

    @Override
    public List<ItemMenu> findAllWithInsumos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select distinct im from ItemMenu im " +
                    "left join fetch im.insumosRequeridos dim " +
                    "left join fetch dim.insumo",
                    ItemMenu.class
            ).getResultList();
        }
    }
}

