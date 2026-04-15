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
}