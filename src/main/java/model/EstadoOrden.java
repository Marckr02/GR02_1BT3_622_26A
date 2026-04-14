package model;

/**
 * Estados posibles de una OrdenDeCompra.
 *
 * Trazabilidad → Diagrama de Clases:
 *   Atributo "estado : String" en OrdenDeCompra.
 *   PENDIENTE  = orden registrada, stock sumado correctamente.
 *   DISCREPANCIA = diferencia entre lo facturado y lo recibido (extend del CU3).
 */
public enum EstadoOrden {
    PENDIENTE,
    DISCREPANCIA
}
