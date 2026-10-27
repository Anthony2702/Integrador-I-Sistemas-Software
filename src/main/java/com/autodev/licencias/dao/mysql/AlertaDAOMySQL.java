package com.autodev.licencias.dao.mysql;

import com.autodev.licencias.dao.AlertaDAO;
import com.autodev.licencias.dao.DaoException;
import com.autodev.licencias.modelo.Alerta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de {@link AlertaDAO} con MySQL.
 */
public class AlertaDAOMySQL implements AlertaDAO {

    private final ConexionBD conexionBD;

    public AlertaDAOMySQL(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Alerta guardar(Alerta alerta) {
        String sql = "INSERT INTO alerta (id_licencia, fecha_generada, dias_restantes, atendida) VALUES (?, ?, ?, ?)";
        try (Connection cn = conexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, alerta.getLicencia().getIdLicencia());
            ps.setTimestamp(2, Timestamp.valueOf(alerta.getFechaGenerada()));
            ps.setInt(3, alerta.getDiasRestantes());
            ps.setBoolean(4, alerta.isAtendida());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    alerta.setIdAlerta(claves.getInt(1));
                }
            }
            return alerta;
        } catch (SQLException e) {
            throw new DaoException("No se pudo guardar la alerta", e);
        }
    }

    @Override
    public boolean existeParaLicencia(int idLicencia) {
        String sql = "SELECT 1 FROM alerta WHERE id_licencia = ? LIMIT 1";
        try (Connection cn = conexionBD.abrir(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idLicencia);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException("No se pudo consultar las alertas", e);
        }
    }

    @Override
    public List<Alerta> listarPendientes() {
        String sql = "SELECT a.id_alerta, a.fecha_generada, a.dias_restantes, a.atendida, x.*"
                + " FROM alerta a JOIN (" + LicenciaDAOMySQL.SELECT_LICENCIA + ") x"
                + " ON x.id_licencia = a.id_licencia"
                + " WHERE a.atendida = FALSE ORDER BY x.fecha_vencimiento";
        try (Connection cn = conexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Alerta> alertas = new ArrayList<>();
            while (rs.next()) {
                Alerta alerta = new Alerta(LicenciaDAOMySQL.mapear(rs),
                        rs.getTimestamp("fecha_generada").toLocalDateTime(), rs.getInt("dias_restantes"));
                alerta.setIdAlerta(rs.getInt("id_alerta"));
                alerta.setAtendida(rs.getBoolean("atendida"));
                alertas.add(alerta);
            }
            return alertas;
        } catch (SQLException e) {
            throw new DaoException("No se pudo listar las alertas", e);
        }
    }

    @Override
    public void marcarAtendida(int idAlerta) {
        ejecutar("UPDATE alerta SET atendida = TRUE WHERE id_alerta = ?", idAlerta);
    }

    @Override
    public void atenderPorLicencia(int idLicencia) {
        ejecutar("UPDATE alerta SET atendida = TRUE WHERE id_licencia = ?", idLicencia);
    }

    private void ejecutar(String sql, int id) {
        try (Connection cn = conexionBD.abrir(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("No se pudo actualizar la alerta", e);
        }
    }
}
