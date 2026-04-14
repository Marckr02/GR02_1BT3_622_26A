package model;

import jakarta.persistence.*;

/**
 * Línea de detalle de una OrdenDeCompra.
 *
 * Trazabilidad → Diagrama de Clases (CU3):
 *   Clase "DetalleOrden" con atributos: cantidadPedida, cantidadRecibida, precioUnitario.
 *   Método: hayDiscrepancia() → compara cantidadPedida vs cantidadRecibida.
 *
 * Trazabilidad → Diagrama de Actividades (CU3):
 *   Paso "Registrar cantidades recibidas por ítem" → cada ítem es un DetalleOrden.
 */
@Entity
@Table(name = "detalle_orden")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "orden_de_compra_id")
    private OrdenDeCompra ordenDeCompra;

    @ManyToOne(optional = false)
    @JoinColumn(name = "insumo_id")
    private Insumo insumo;

    /** Cantidad que indica la factura. */
    @Column(nullable = false)
    private Double cantidadPedida;

    /** Cantidad que físicamente ingresó a bodega. */
    @Column(nullable = false)
    private Double cantidadRecibida;

    @Column(nullable = false)
    private Double precioUnitario;

    // ── Método de negocio: hayDiscrepancia() ────────────────────────────────
    /**
     * Trazabilidad → Diagrama de Clases método hayDiscrepancia():
     * Retorna true si la cantidad recibida difiere de la pedida (tolerancia 0.001).
     */
    public boolean hayDiscrepancia() {
        return Math.abs(cantidadPedida - cantidadRecibida) > 0.001;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrdenDeCompra getOrdenDeCompra() { return ordenDeCompra; }
    public void setOrdenDeCompra(OrdenDeCompra ordenDeCompra) { this.ordenDeCompra = ordenDeCompra; }

    public Insumo getInsumo() { return insumo; }
    public void setInsumo(Insumo insumo) { this.insumo = insumo; }

    public Double getCantidadPedida() { return cantidadPedida; }
    public void setCantidadPedida(Double cantidadPedida) { this.cantidadPedida = cantidadPedida; }

    public Double getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(Double cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
}
