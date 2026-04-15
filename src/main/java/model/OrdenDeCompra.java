package model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden_de_compra")
public class OrdenDeCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Embedded
    private DatosFactura datosFactura = new DatosFactura();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado = EstadoOrden.PENDIENTE;

    @OneToMany(mappedBy = "ordenDeCompra",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles = new ArrayList<>();
    
    public String getNumeroFactura() {
        return datosFactura.getNumeroFactura();
    }
    public void setNumeroFactura(String v) {
        datosFactura.setNumeroFactura(v);
    }

    public String getNombreProveedor() {
        return datosFactura.getNombreProveedor();
    }
    public void setNombreProveedor(String v) {
        datosFactura.setNombreProveedor(v);
    }

    public LocalDate getFechaFactura() {
        return datosFactura.getFechaFactura();
    }
    public void setFechaFactura(LocalDate v) {
        datosFactura.setFechaFactura(v);
    }

    public DatosFactura getDatosFactura() { return datosFactura; }

    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }

    public List<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrden> detalles) {
        this.detalles = detalles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}