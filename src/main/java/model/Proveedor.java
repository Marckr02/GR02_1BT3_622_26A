package model;

import jakarta.persistence.*;

/**
 * Entidad que representa un proveedor de insumos.
 *
 * Trazabilidad – TAREA 1.1 HU3 (Marco)
 *   atributos: id, nombre, telefono, correo
 */
@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 120)
    private String correo;

    // ── Constructores ─────────────────────────────────────────────────────────

    public Proveedor() {}

    public Proveedor(String nombre, String telefono, String correo) {
        validar(nombre, telefono, correo);
        this.nombre   = nombre.trim();
        this.telefono = telefono.trim();
        this.correo   = correo.trim();
    }

    // ── Validación (refactorización: responsabilidad separada) ────────────────

    /**
     * Valida que los campos obligatorios no sean nulos ni vacíos.
     * Extraído como método privado para separar la responsabilidad de validación
     * de la de asignación de atributos.
     */
    private void validar(String nombre, String telefono, String correo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono del proveedor es obligatorio.");
        }
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo del proveedor es obligatorio.");
        }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono del proveedor es obligatorio.");
        }
        this.telefono = telefono.trim();
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo del proveedor es obligatorio.");
        }
        this.correo = correo.trim();
    }
}
