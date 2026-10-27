package com.autodev.licencias.dao.mysql;

import com.autodev.licencias.dao.DaoException;
import com.autodev.licencias.dao.ProgramaDAO;
import com.autodev.licencias.modelo.Programa;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de {@link ProgramaDAO} con MySQL.
 */
public class ProgramaDAOMySQL implements ProgramaDAO {

    private final ConexionBD conexionBD;

    public ProgramaDAOMySQL(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public List<Programa> listar() {
        String sql = "SELECT id_programa, nombre, precio FROM programa ORDER BY id_programa";
        try (Connection cn = conexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Programa> programas = new ArrayList<>();
            while (rs.next()) {
                programas.add(new Programa(rs.getInt("id_programa"), rs.getString("nombre"),
                        rs.getBigDecimal("precio")));
            }
            return programas;
        } catch (SQLException e) {
            throw new DaoException("No se pudo listar los programas", e);
        }
    }

    @Override
    public void actualizarPrecio(int idPrograma, BigDecimal precio) {
        String sql = "UPDATE programa SET precio = ? WHERE id_programa = ?";
        try (Connection cn = conexionBD.abrir(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBigDecimal(1, precio);
            ps.setInt(2, idPrograma);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("No se pudo actualizar el precio", e);
        }
    }
}
