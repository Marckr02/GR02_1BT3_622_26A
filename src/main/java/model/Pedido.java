package model;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.RECIBIDO;

    @Column(nullable = false, length = 80)
    private String plataformaOrigen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "marca_id")
    private Marca marca;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // ── CU1: campos nuevos ───────────────────────────────────────────────────

    /** Nombre del cliente que hizo el pedido (simulado desde la plataforma). */
    @Column(nullable = false, length = 120)
    private String nombreCliente;

    /**
     * Prioridad calculada automáticamente al recibir el pedido.
     * 1 = Alta, 2 = Media, 3 = Baja.
     * Se asigna en función del tiempoLimiteMin: <= 20 min → Alta, <= 40 → Media, resto → Baja.
     */
    @Column(nullable = false)
    private int prioridad = 2;

    /**
     * Minutos máximos acordados con la plataforma para entregar el pedido.
     * Permite calcular la prioridad automáticamente.
     */
    @Column(nullable = false)
    private int tiempoLimiteMin = 30;

    // ── Getters y Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public String getPlataformaOrigen() { return plataformaOrigen; }
    public void setPlataformaOrigen(String plataformaOrigen) { this.plataformaOrigen = plataformaOrigen; }

    public Marca getMarca() { return marca; }
    public void setMarca(Marca marca) { this.marca = marca; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    public int getTiempoLimiteMin() { return tiempoLimiteMin; }
    public void setTiempoLimiteMin(int tiempoLimiteMin) { this.tiempoLimiteMin = tiempoLimiteMin; }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static final Map<Integer, String[]> INFO_PRIORIDAD = Map.of(
        1, new String[]{"Alta",  "prioridad-alta"},
        2, new String[]{"Media", "prioridad-media"},
        3, new String[]{"Baja",  "prioridad-baja"}
    );
    public String getPrioridadLabel(){
        return INFO_PRIORIDAD.getOrDefault(prioridad, INFO_PRIORIDAD.get(3))[0];
    }

    public String getPrioridadCss(){
        return INFO_PRIORIDAD.getOrDefault(prioridad, INFO_PRIORIDAD.get(3))[1];
    }
}