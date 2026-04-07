package dao;

import model.DetalleInsumoMenu;

public class DetalleInsumoMenuDaoHibernate extends GenericHibernateDao<DetalleInsumoMenu, Long>
        implements DetalleInsumoMenuDao {

    public DetalleInsumoMenuDaoHibernate() {
        super(DetalleInsumoMenu.class);
    }
}

