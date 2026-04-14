package dao;

import model.OrdenDeCompra;

public class OrdenDeCompraDaoHibernate
        extends GenericHibernateDao<OrdenDeCompra, Long>
        implements OrdenDeCompraDao {

    public OrdenDeCompraDaoHibernate() {
        super(OrdenDeCompra.class);
    }
}
