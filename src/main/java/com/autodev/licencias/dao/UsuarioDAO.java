package com.autodev.licencias.dao;

import com.autodev.licencias.modelo.Usuario;
import java.util.Optional;

/**
 * Acceso a datos de los usuarios del sistema.
 */
public interface UsuarioDAO {

    /**
     * @param usuario nombre de usuario
     * @return el usuario, si existe
     */
    Optional<Usuario> buscarPorUsuario(String usuario);

    /**
     * Guarda un usuario nuevo y le asigna su id.
     *
     * @param usuario usuario a guardar (con la clave ya cifrada)
     * @return el mismo usuario con su id
     */
    Usuario guardar(Usuario usuario);

    /** @return cantidad de usuarios registrados */
    int contar();
}
