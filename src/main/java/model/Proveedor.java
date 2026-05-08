package model;

import jakarta.persistence.*;

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 100)
    private String correo;

    public Proveedor() {}

    public Proveedor(String nombre, String telefono, String correo) {
        this.nombre   = nombre;
        this.telefono = telefono;
        this.correo   = correo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String n) { this.nombre = n; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String t) { this.telefono = t; }

    public String getCorreo() { return correo; }
    public void setCorreo(String c) { this.correo = c; }
}
