package dao;

import model.AlertaStock;
import model.Insumo;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para AlertaStock.
 * Extiende GenericDao para heredar save / update / delete / findById / findAll.
 */
public interface AlertaDao extends GenericDao<AlertaStock, Long> {

    /**
     * Devuelve la alerta activa asociada a un insumo, si existe.
     *
     * @param insumo insumo a consultar
     * @return Optional con la alerta activa, o vacío si no hay ninguna
     */
    Optional<AlertaStock> findActivaByInsumo(Insumo insumo);

    /**
     * Devuelve todas las alertas (activas e históricas) ordenadas de más
     * reciente a más antigua.
     */
    List<AlertaStock> findAllOrdenadas();
}
