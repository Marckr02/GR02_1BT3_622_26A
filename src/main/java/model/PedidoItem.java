package model;

public class PedidoItem {

    private ItemMenu plato;
    private int cantidad;
    private double precioUnitario;

    public PedidoItem(ItemMenu plato, int cantidad, double precioUnitario) {
        this.plato = plato;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public ItemMenu getPlato() { return plato; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
}