package com.autodev.licencias.dao.memoria;

import com.autodev.licencias.dao.UsuarioDAO;
import com.autodev.licencias.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** DAO falso en memoria para probar los servicios sin MySQL. */
public class UsuarioDAOMemoria implements UsuarioDAO {

    private final List<Usuario> usuarios = new ArrayList<>();

    @Override
    public Optional<Usuario> buscarPorUsuario(String usuario) {
        return usuarios.stream().filter(u -> u.getUsuario().equals(usuario)).findFirst();
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        usuario.setIdUsuario(usuarios.size() + 1);
        usuarios.add(usuario);
        return usuario;
    }

    @Override
    public int contar() {
        return usuarios.size();
    }
}
