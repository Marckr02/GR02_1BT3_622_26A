package dao;

import model.OrdenDeCompra;

/**
 * DAO para OrdenDeCompra.
 * Trazabilidad → Diagrama de Robustez CU3: entidad "Orden de Compra"
 * accedida por el control "Validador de Orden de Compra".
 */
public interface OrdenDeCompraDao extends GenericDao<OrdenDeCompra, Long> {
}
