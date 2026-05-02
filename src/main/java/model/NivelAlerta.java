package model;

/**
 * Nivel de criticidad de una alerta de stock.
 *
 * CRITICO    → stock actual == 0 o por debajo del 50 % del mínimo
 * ADVERTENCIA → stock actual está entre el 50 % y el 100 % del mínimo (inclusive)
 */
public enum NivelAlerta {
    CRITICO,
    ADVERTENCIA
}
