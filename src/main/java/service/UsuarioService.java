package service;

import dao.UsuarioDao;
import dao.UsuarioDaoHibernate;
import model.Rol;
import model.Usuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * UsuarioService — FASE REFACTOR.
 *
 * Refactorizaciones aplicadas respecto a la fase Green:
 *
 *   REFACTOR 1 — Extraer método privado validarCredenciales():
 *     La comparación de hash se extrae a un método con nombre
 *     expresivo, eliminando el comentario explicativo inline
 *     y haciendo autenticar() más legible de un vistazo.
 *
 *   REFACTOR 2 — Extraer método privado buscarUsuario():
 *     La búsqueda + verificación de existencia se encapsulan,
 *     separando la responsabilidad de "encontrar" de la de "validar".
 *     autenticar() queda como orquestador de dos pasos claros.
 *
 * Los dos tests unitarios siguen pasando sin modificación alguna.
 *
 * Tarea 2 — HU8 (Diagrama de Robustez: Control)
 */
public class UsuarioService {

    private static final String ERROR_CREDENCIALES = "Credenciales incorrectas. Intente nuevamente.";

    private final UsuarioDao usuarioDao;

    public UsuarioService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public UsuarioService() {
        this.usuarioDao = new UsuarioDaoHibernate();
        inicializarUsuariosBase();
    }

    // ── FASE REFACTOR: autenticar() limpio como orquestador ───────────────

    /**
     * autenticar(username, password) : Usuario
     *
     * Orquesta dos pasos con responsabilidades claras:
     *   1. buscarUsuario()        → encuentra o lanza excepción
     *   2. validarCredenciales()  → verifica hash o lanza excepción
     */
    public Usuario autenticar(String username, String password) {
        Usuario usuario = buscarUsuario(username);
        validarCredenciales(password, usuario.getPasswordHash());
        return usuario;
    }

    // ── Métodos privados extraídos (Refactor 1 y 2) ───────────────────────

    /**
     * REFACTOR 2: Extraído de autenticar().
     * Busca el usuario por username; lanza SecurityException si no existe.
     */
    private Usuario buscarUsuario(String username) {
        return usuarioDao.findByUsername(username)
                .orElseThrow(() -> new SecurityException(ERROR_CREDENCIALES));
    }

    /**
     * REFACTOR 1: Extraído de autenticar().
     * Compara el hash de la contraseña ingresada contra el almacenado.
     * Lanza SecurityException si no coinciden.
     */
    private void validarCredenciales(String passwordIngresada, String hashAlmacenado) {
        if (!hashSHA256(passwordIngresada).equals(hashAlmacenado)) {
            throw new SecurityException(ERROR_CREDENCIALES);
        }
    }

    // ── Datos semilla ─────────────────────────────────────────────────────

    private void inicializarUsuariosBase() {
        if (!usuarioDao.findAll().isEmpty()) return;

        List<Object[]> usuarios = Arrays.asList(
                new Object[]{"chef01",  "pass123",  Rol.COCINERO},
                new Object[]{"chef02",  "pass456",  Rol.COCINERO},
                new Object[]{"admin01", "admin123", Rol.ADMIN_BODEGA}
        );

        for (Object[] datos : usuarios) {
            usuarioDao.save(new Usuario(
                    (String) datos[0],
                    hashSHA256((String) datos[1]),
                    (Rol) datos[2]
            ));
        }
    }

    // ── Utilidad: hash SHA-256 ────────────────────────────────────────────

    public static String hashSHA256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }
}
