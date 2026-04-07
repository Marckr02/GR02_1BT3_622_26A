package dao;

import model.Marca;

public class MarcaDaoHibernate extends GenericHibernateDao<Marca, Long> implements MarcaDao {

    public MarcaDaoHibernate() {
        super(Marca.class);
    }
}

