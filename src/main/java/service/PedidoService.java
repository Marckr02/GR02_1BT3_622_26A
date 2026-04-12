package service;

import dao.MarcaDao;
import dao.MarcaDaoHibernate;
import dao.PedidoDao;
import dao.PedidoDaoHibernate;
import model.EstadoPedido;
import model.Marca;
import model.Pedido;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio principal para el flujo de pedidos.
 *
 * CU1 – Recibir pedido de plataforma externa
 *   - recibirPedido(): valida datos, asigna prioridad y persiste.
 *   - listarPedidosRecibidos(): devuelve sólo los pedidos en estado RECIBIDO.
 *   - listarTodos(): devuelve todos los pedidos (usado por el Kanban en CU2).
 *
 * CU2 (stub)
 *   - moverKanbanStub(): marcador para la siguiente iteración.
 */
public class PedidoService {

    private final PedidoDao pedidoDao;
    private final MarcaDao marcaDao;

    public PedidoService() {
        this.pedidoDao = new PedidoDaoHibernate();
        this.marcaDao = new MarcaDaoHibernate();
        inicializarMarcasBase();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CU1 – Recibir pedido de plataforma externa
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Recibe los datos crudos de la plataforma de delivery (simulados desde el
     * formulario JSP) y ejecuta la lógica del CU1:
     *
     *  1. Valida que los campos obligatorios estén presentes.
     *  2. Resuelve la Marca correspondiente en la BD.
     *  3. Asigna prioridad automática según tiempoLimiteMin.
     *  4. Persiste el pedido en estado RECIBIDO.
     *
     * @param plataformaOrigen Nombre de la app de delivery (ej. "Rappi", "iFood").
     * @param nombreCliente    Nombre del cliente del pedido.
     * @param marcaId          ID de la marca que debe preparar el pedido.
     * @param tiempoLimiteMin  Minutos límite de entrega acordados con la plataforma.
     * @return el Pedido persistido con id generado.
     * @throws IllegalArgumentException si algún dato obligatorio falta o la marca no existe.
     */
    public Pedido recibirPedido(String plataformaOrigen,
                                String nombreCliente,
                                Long marcaId,
                                int tiempoLimiteMin) {

        // 1. Validar datos del pedido
        validarDatosPedido(plataformaOrigen, nombreCliente, marcaId, tiempoLimiteMin);

        // 2. Resolver Marca
        Optional<Marca> marcaOpt = marcaDao.findById(marcaId);
        if (marcaOpt.isEmpty()) {
            throw new IllegalArgumentException("Marca con id=" + marcaId + " no encontrada.");
        }

        // 3. Construir y completar el Pedido
        Pedido pedido = new Pedido();
        pedido.setPlataformaOrigen(plataformaOrigen.trim());
        pedido.setNombreCliente(nombreCliente.trim());
        pedido.setMarca(marcaOpt.get());
        pedido.setTiempoLimiteMin(tiempoLimiteMin);
        pedido.setTimestamp(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.RECIBIDO);

        // 4. Asignar prioridad (<<include>> del diagrama de CU1)
        pedido.setPrioridad(asignarPrioridad(tiempoLimiteMin));

        // 5. Persistir
        return pedidoDao.save(pedido);
    }

    /**
     * Devuelve todos los pedidos en estado RECIBIDO para mostrarse en la vista
     * del CU1 (tablero de pedidos entrantes).
     */
    public List<Pedido> listarPedidosRecibidos() {
        return pedidoDao.findAll().stream()
                .filter(p -> p.getEstado() == EstadoPedido.RECIBIDO)
                .sorted((a, b) -> Integer.compare(a.getPrioridad(), b.getPrioridad()))
                .collect(Collectors.toList()); // <-- Cambiado de .toList()
    }

    /**
     * Devuelve todos los pedidos, ordenados por prioridad y luego por timestamp.
     */
    public List<Pedido> listarTodos() {
        return pedidoDao.findAll().stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(a.getPrioridad(), b.getPrioridad());
                    if (cmp != 0) return cmp;
                    return a.getTimestamp().compareTo(b.getTimestamp());
                })
                .collect(Collectors.toList()); // <-- Cambiado de .toList()
    }

    /**
     * Devuelve todas las marcas registradas (para poblar el <select> del formulario).
     */
    public List<Marca> listarMarcas() {
        inicializarMarcasBase();
        return marcaDao.findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CU2 – Actualizar estado en tablero Kanban
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Avanza el pedido al siguiente estado en el flujo Kanban:
     *   RECIBIDO → EN_PREP → LISTO → ENTREGADO
     *
     * @param pedidoId ID del pedido a avanzar.
     * @throws IllegalArgumentException si el pedido no existe o ya está en ENTREGADO.
     */
    public void avanzarEstado(Long pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoDao.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido con id=" + pedidoId + " no encontrado.");
        }

        Pedido pedido = pedidoOpt.get();
        EstadoPedido estadoActual = pedido.getEstado();

        EstadoPedido siguienteEstado;
        switch (estadoActual) {
            case RECIBIDO:
                siguienteEstado = EstadoPedido.EN_PREP;
                break;
            case EN_PREP:
                siguienteEstado = EstadoPedido.LISTO;
                break;
            case LISTO:
                siguienteEstado = EstadoPedido.ENTREGADO;
                break;
            default:
                throw new IllegalArgumentException("El pedido P-" + pedidoId + " ya está en estado ENTREGADO.");
        }

        pedido.setEstado(siguienteEstado);
        pedidoDao.update(pedido);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Lógica de asignación de prioridad del pedido.
     * Reproduce el <<include>> "Asignar prioridad del pedido en el tablero" del CU1.
     *
     *  1 (Alta)  → tiempoLimiteMin <= 20
     *  2 (Media) → tiempoLimiteMin <= 40
     *  3 (Baja)  → tiempoLimiteMin > 40
     */
    private int asignarPrioridad(int tiempoLimiteMin) {
        if (tiempoLimiteMin <= 20) return 1;
        if (tiempoLimiteMin <= 40) return 2;
        return 3;
    }

    /**
     * Valida que los campos obligatorios del pedido no estén vacíos o inválidos.
     * Reproduce el <<include>> "Validar datos del pedido" del CU1.
     *
     * En caso de datos inconsistentes lanza IllegalArgumentException, que el
     * Servlet captura para notificar el error de recepción (<<extend>>).
     */
    private void validarDatosPedido(String plataformaOrigen,
                                    String nombreCliente,
                                    Long marcaId,
                                    int tiempoLimiteMin) {
        if (plataformaOrigen == null || plataformaOrigen.isBlank()) {
            throw new IllegalArgumentException("La plataforma de origen es obligatoria.");
        }
        if (nombreCliente == null || nombreCliente.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        if (marcaId == null || marcaId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una marca válida.");
        }
        if (tiempoLimiteMin <= 0 || tiempoLimiteMin > 120) {
            throw new IllegalArgumentException("El tiempo límite debe estar entre 1 y 120 minutos.");
        }
    }

    private void inicializarMarcasBase() {
        if (!marcaDao.findAll().isEmpty()) {
            return;
        }

        Arrays.asList("Burger Lab", "Pizza House", "Sushi Go")
                .forEach(this::crearMarcaBase);
    }

    private void crearMarcaBase(String nombreMarca) {
        Marca marca = new Marca();
        marca.setNombre(nombreMarca);
        marcaDao.save(marca);
    }
}
