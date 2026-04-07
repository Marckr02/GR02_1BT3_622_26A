package dao;

import model.Insumo;

public class InsumoDaoHibernate extends GenericHibernateDao<Insumo, Long> implements InsumoDao {

    public InsumoDaoHibernate() {
        super(Insumo.class);
    }
}

