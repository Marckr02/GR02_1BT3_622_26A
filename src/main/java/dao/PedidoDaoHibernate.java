package dao;

import model.Pedido;

public class PedidoDaoHibernate extends GenericHibernateDao<Pedido, Long> implements PedidoDao {

    public PedidoDaoHibernate() {
        super(Pedido.class);
    }
}

