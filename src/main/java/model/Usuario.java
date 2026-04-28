package model;

import jakarta.persistence.*;

/**
 * Entidad Usuario — Tarea 1 de HU8 (Diagrama de Robustez: Entidad).
 *
 * Atributos según el diagrama de clases del Incremento 3:
 *   -id           : Long
 *   -username     : String
 *   -passwordHash : String   (contraseña almacenada con hash SHA-256)
 *   -rol          : Rol      (enum COCINERO | ADMIN_BODEGA)
 *
 * Esta entidad es el núcleo que alimenta a UsuarioService (Control)
 * y cuyo estado persiste en la tabla "usuario" de la BD H2.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 64)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    // ── Constructores ─────────────────────────────────────────────────────

    public Usuario() {}

    public Usuario(String username, String passwordHash, Rol rol) {
        this.username     = username;
        this.passwordHash = passwordHash;
        this.rol          = rol;
    }

    // ── Getters y Setters ─────────────────────────────────────────────────

    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }

    public String getUsername()              { return username; }
    public void   setUsername(String u)      { this.username = u; }

    public String getPasswordHash()              { return passwordHash; }
    public void   setPasswordHash(String ph)     { this.passwordHash = ph; }

    public Rol  getRol()           { return rol; }
    public void setRol(Rol rol)    { this.rol = rol; }
}
