package dao;

import model.Usuario;
import java.util.Optional;

/**
 * UsuarioDao — contrato de persistencia para la entidad Usuario.
 * Extiende GenericDao para heredar save/update/delete/findById/findAll.
 *
 * Tarea 1 — HU8 (Diagrama de Robustez: Entidad → DAO)
 */
public interface UsuarioDao extends GenericDao<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario (único en BD).
     * Usado por UsuarioService.autenticar() en la fase de búsqueda.
     */
    Optional<Usuario> findByUsername(String username);
}
