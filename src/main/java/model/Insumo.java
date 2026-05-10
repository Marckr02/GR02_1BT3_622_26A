package model;

import jakarta.persistence.*;

/**
 * Entidad que representa un insumo del inventario centralizado.
 *
 * Trazabilidad – TAREA 3.3 HU3 (Jeremy)
 *   Se agrega la relación ManyToOne con Proveedor (optional=true / LAZY)
 *   para no romper los datos base existentes que no tienen proveedor asignado.
 *
 * Refactorización: la relación se declara como LAZY para evitar
 * cargas innecesarias de Proveedor cada vez que se consulte un Insumo.
 * Se documenta el motivo para futuros desarrolladores.
 */
@Entity
@Table(name = "insumo")
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Double cantidad;

    @Column(nullable = false, length = 20)
    private String unidad;

    @Column(nullable = false)
    private Double stockMinimo;

    /**
     * Proveedor asociado al insumo.
     *
     * optional = true  → la FK es nullable; los insumos existentes sin proveedor
     *                    siguen siendo válidos y no rompen la inicialización base.
     * fetch = LAZY     → el proveedor solo se carga cuando se accede explícitamente,
     *                    evitando JOINs innecesarios en consultas de inventario masivo.
     */
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = true)
    private Proveedor proveedor;

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getCantidad() { return cantidad; }
    public void setCantidad(Double cantidad) { this.cantidad = cantidad; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public Double getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Double stockMinimo) { this.stockMinimo = stockMinimo; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
}