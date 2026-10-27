package com.autodev.licencias.dao.mysql;

import com.autodev.licencias.dao.ClienteDAO;
import com.autodev.licencias.dao.DaoException;
import com.autodev.licencias.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de {@link ClienteDAO} con MySQL.
 */
public class ClienteDAOMySQL implements ClienteDAO {

    private static final String COLUMNAS = "SELECT id_cliente, nombre, ruc, contacto FROM cliente";

    private final ConexionBD conexionBD;

    public ClienteDAOMySQL(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        String sql = "INSERT INTO cliente (nombre, ruc, contacto) VALUES (?, ?, ?)";
        try (Connection cn = conexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            llenar(ps, cliente);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    cliente.setIdCliente(claves.getInt(1));
                }
            }
            return cliente;
        } catch (SQLException e) {
            throw new DaoException("No se pudo guardar el cliente", e);
        }
    }

    @Override
    public void actualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre = ?, ruc = ?, contacto = ? WHERE id_cliente = ?";
        try (Connection cn = conexionBD.abrir(); PreparedStatement ps = cn.prepareStatement(sql)) {
            llenar(ps, cliente);
            ps.setInt(4, cliente.getIdCliente());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("No se pudo actualizar el cliente", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorRuc(String ruc) {
        try (Connection cn = conexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(COLUMNAS + " WHERE ruc = ?")) {
            ps.setString(1, ruc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("No se pudo buscar el cliente", e);
        }
    }

    @Override
    public List<Cliente> listar() {
        try (Connection cn = conexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(COLUMNAS + " ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            List<Cliente> clientes = new ArrayList<>();
            while (rs.next()) {
                clientes.add(mapear(rs));
            }
            return clientes;
        } catch (SQLException e) {
            throw new DaoException("No se pudo listar los clientes", e);
        }
    }

    private void llenar(PreparedStatement ps, Cliente cliente) throws SQLException {
        ps.setString(1, cliente.getNombre());
        ps.setString(2, cliente.getRuc());
        ps.setString(3, cliente.getContacto());
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"),
                rs.getString("ruc"), rs.getString("contacto"));
    }
}
