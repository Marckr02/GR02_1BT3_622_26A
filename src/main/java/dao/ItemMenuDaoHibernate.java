package dao;

import model.ItemMenu;

public class ItemMenuDaoHibernate extends GenericHibernateDao<ItemMenu, Long> implements ItemMenuDao {

    public ItemMenuDaoHibernate() {
        super(ItemMenu.class);
    }
}

