package com.autodev.licencias.dao.mysql;

import com.autodev.licencias.dao.DaoException;
import com.autodev.licencias.dao.UsuarioDAO;
import com.autodev.licencias.modelo.Rol;
import com.autodev.licencias.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Implementación de {@link UsuarioDAO} con MySQL.
 * Todas las consultas usan PreparedStatement para evitar inyección SQL.
 */
public class UsuarioDAOMySQL implements UsuarioDAO {

    private final ConexionBD conexionBD;

    public UsuarioDAOMySQL(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Optional<Usuario> buscarPorUsuario(String usuario) {
        String sql = "SELECT id_usuario, usuario, clave_hash, nombre, rol, activo FROM usuario WHERE usuario = ?";
        try (Connection cn = conexionBD.abrir(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Usuario u = new Usuario(rs.getString("usuario"), rs.getString("clave_hash"),
                        rs.getString("nombre"), Rol.valueOf(rs.getString("rol")));
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setActivo(rs.getBoolean("activo"));
                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new DaoException("No se pudo buscar el usuario", e);
        }
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        String sql = "INSERT INTO usuario (usuario, clave_hash, nombre, rol, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = conexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getUsuario());
            ps.setString(2, usuario.getClaveHash());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getRol().name());
            ps.setBoolean(5, usuario.isActivo());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    usuario.setIdUsuario(claves.getInt(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new DaoException("No se pudo guardar el usuario", e);
        }
    }

    @Override
    public int contar() {
        try (Connection cn = conexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM usuario");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DaoException("No se pudo contar los usuarios", e);
        }
    }
}
