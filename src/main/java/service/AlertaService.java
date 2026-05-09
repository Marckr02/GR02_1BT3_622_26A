package service;

import dao.AlertaDaoHibernate;
import model.AlertaStock;
import model.Insumo;
import model.NivelAlerta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class AlertaService {

    private final AlertaDaoHibernate alertaDao;

    public AlertaService() {
        this.alertaDao = new AlertaDaoHibernate();
    }

    // Constructor para inyección en tests
    public AlertaService(AlertaDaoHibernate alertaDao) {
        this.alertaDao = alertaDao;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CLASIFICACIÓN DE NIVEL
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Determina el nivel de alerta en función del stock actual y el mínimo.
     *
     * @param stockActual  cantidad actual del insumo
     * @param stockMinimo  umbral mínimo configurado para el insumo
     * @return NivelAlerta.CRITICO, NivelAlerta.ADVERTENCIA o null si no aplica alerta
     */
    public NivelAlerta clasificarNivel(double stockActual, double stockMinimo) {
        if (stockMinimo <= 0) {
            // Sin mínimo configurado no se pueden generar alertas
            return null;
        }
        if (stockActual == 0 || stockActual < stockMinimo * 0.50) {
            return NivelAlerta.CRITICO;
        }
        if (stockActual <= stockMinimo) {
            return NivelAlerta.ADVERTENCIA;
        }
        return null; // stock por encima del mínimo → sin alerta
    }

    public void generarSiCritico(Insumo insumo) {
        NivelAlerta nivel = clasificarNivel(insumo.getCantidad(), insumo.getStockMinimo());
        if (nivel == null) {
            return; // stock suficiente, nada que hacer
        }

        Optional<AlertaStock> existente = alertaDao.findActivaByInsumo(insumo);
        if (existente.isPresent()) {
            // Actualizar nivel de la alerta activa si cambió
            AlertaStock alerta = existente.get();
            if (alerta.getNivel() != nivel) {
                alerta.setNivel(nivel);
                alertaDao.update(alerta);
            }
        } else {
            // Crear nueva alerta
            AlertaStock nueva = new AlertaStock(insumo, nivel);
            alertaDao.save(nueva);
        }
    }

    public void resolverSiActiva(Insumo insumo) {
        if (insumo.getCantidad() <= insumo.getStockMinimo()) {
            return; // todavía por debajo o en el umbral → no resolver
        }

        Optional<AlertaStock> activa = alertaDao.findActivaByInsumo(insumo);
        activa.ifPresent(alerta -> {
            alerta.setActiva(false);
            alerta.setFechaResolucion(LocalDateTime.now());
            alertaDao.update(alerta);
        });
    }

    public List<AlertaStock> listarHistorial() {
        return alertaDao.findAllOrdenadas();
    }
}