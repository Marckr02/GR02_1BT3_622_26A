package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una alerta de stock bajo para un insumo.
 *
 * Trazabilidad – Tarea 1:
 *   atributos: id, insumo (FK), nivel, timestamp, estado, fechaResolucion
 */
@Entity
@Table(name = "alerta_stock")
public class AlertaStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Insumo que disparó la alerta. */
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    /** Gravedad de la alerta en el momento de su generación. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelAlerta nivel;

    /** Instante en que se generó la alerta. */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * Estado actual de la alerta.
     * true  = activa  (no resuelta)
     * false = resuelta
     */
    @Column(nullable = false)
    private boolean activa;

    /** Fecha/hora en que el stock volvió a superar el mínimo. */
    @Column
    private LocalDateTime fechaResolucion;

    // ── Constructores ─────────────────────────────────────────────────────────

    public AlertaStock() {}

    public AlertaStock(Insumo insumo, NivelAlerta nivel) {
        this.insumo    = insumo;
        this.nivel     = nivel;
        this.timestamp = LocalDateTime.now();
        this.activa    = true;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Insumo getInsumo() { return insumo; }
    public void setInsumo(Insumo insumo) { this.insumo = insumo; }

    public NivelAlerta getNivel() { return nivel; }
    public void setNivel(NivelAlerta nivel) { this.nivel = nivel; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }
}
