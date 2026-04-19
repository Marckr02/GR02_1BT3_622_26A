package service;

import model.ItemMenu;
import model.PedidoItem;
import java.util.List;

public class PedidoItemService {

    public boolean agregarPlato(ItemMenu plato) {
        return plato.isActivo() && plato.puedePrepararse();
    }

    public double calcularTotal(List<PedidoItem> items) {
        return items.stream()
                .mapToDouble(i -> i.getPrecioUnitario() * i.getCantidad())
                .sum();
    }

    public double aplicarDescuento(List<PedidoItem> items, double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException(
                    "El porcentaje de descuento debe estar entre 0 y 100.");
        }
        double total = calcularTotal(items);
        return total * (1 - porcentaje / 100.0);
    }
}