package model;

import jakarta.persistence.*;

/**
 * Entidad Proveedor — Tarea T1.1 de HU1 (Iteración 1).
 *
 * Diagrama de Robustez: Entidad central del módulo de gestión de proveedores.
 *
 * Atributos según la Historia de Usuario 1:
 *   - id      : Long   (PK auto-generada)
 *   - nombre  : String (nombre del proveedor, requerido)
 *   - telefono: String (teléfono de contacto, requerido)
 *   - correo  : String (correo electrónico de contacto, requerido)
 *
 * Criterio de Aceptación:
 *   Escenario 1 — campos completos: el sistema registra al proveedor.
 *   Escenario 2 — campos vacíos: el sistema rechaza el registro.
 */
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

    // ── Constructores ─────────────────────────────────────────────────────

    public Proveedor() {}

    public Proveedor(String nombre, String telefono, String correo) {
        this.nombre   = nombre;
        this.telefono = telefono;
        this.correo   = correo;
    }

    // ── Getters y Setters ─────────────────────────────────────────────────

    public Long   getId()             { return id; }
    public void   setId(Long id)      { this.id = id; }

    public String getNombre()              { return nombre; }
    public void   setNombre(String n)      { this.nombre = n; }

    public String getTelefono()              { return telefono; }
    public void   setTelefono(String t)      { this.telefono = t; }

    public String getCorreo()              { return correo; }
    public void   setCorreo(String c)      { this.correo = c; }
}
