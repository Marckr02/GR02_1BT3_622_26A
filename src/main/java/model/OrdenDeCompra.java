package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una Orden de Compra / Factura de insumos.
 *
 * Trazabilidad → Diagrama de Clases (CU3):
 *   Clase "OrdenDeCompra" con atributos: idOrden, fecha, numeroFactura, estado.
 *   Métodos: validar(), generarComprobante() → implementados en InsumoService.
 *
 * Trazabilidad → Diagrama de Robustez (CU3):
 *   Entidad "Orden de Compra" que es validada por el control "Validador de Orden de Compra"
 *   y que, al ser válida, activa al "Gestor de Inventario" para sumar stock.
 */
@Entity
@Table(name = "orden_de_compra")
public class OrdenDeCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número de factura del proveedor — campo obligatorio para validación contable. */
    @Column(nullable = false, length = 60)
    private String numeroFactura;

    /** Nombre del proveedor / negocio que emitió la factura. */
    @Column(nullable = false, length = 120)
    private String nombreProveedor;

    /** Fecha en que se emitió la factura. */
    @Column(nullable = false)
    private LocalDate fechaFactura;

    /**
     * Estado de la orden: PENDIENTE → validada y stock sumado; DISCREPANCIA → hay diferencia.
     * Mapea al enum del diagrama de clases.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado = EstadoOrden.PENDIENTE;

    /** Líneas de detalle (un registro por cada tipo de insumo comprado). */
    @OneToMany(mappedBy = "ordenDeCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles = new ArrayList<>();

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public LocalDate getFechaFactura() { return fechaFactura; }
    public void setFechaFactura(LocalDate fechaFactura) { this.fechaFactura = fechaFactura; }

    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }

    public List<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrden> detalles) { this.detalles = detalles; }
}
