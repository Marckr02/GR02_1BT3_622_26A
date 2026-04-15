package model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;


@Embeddable
public class DatosFactura {

    @Column(nullable = false, length = 60)
    private String numeroFactura;

    @Column(nullable = false, length = 120)
    private String nombreProveedor;

    @Column(nullable = false)
    private LocalDate fechaFactura;

    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public LocalDate getFechaFactura() { return fechaFactura; }
    public void setFechaFactura(LocalDate fechaFactura) {
        this.fechaFactura = fechaFactura;
    }
}