package com.autodev.licencias.dao.mysql;

import com.autodev.licencias.dao.DaoException;
import com.autodev.licencias.dao.LicenciaDAO;
import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.TipoLicencia;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de {@link LicenciaDAO} con MySQL.
 */
public class LicenciaDAOMySQL implements LicenciaDAO {

    /** Consulta base: licencia con los datos de su cliente y programa. */
    static final String SELECT_LICENCIA = "SELECT l.id_licencia, l.tipo, l.fecha_venta, l.monto, l.descuento,"
            + " l.medio_pago, l.fecha_emision, l.fecha_vencimiento, l.estado, l.notas,"
            + " c.id_cliente, c.nombre AS cliente_nombre, c.ruc, c.contacto,"
            + " p.id_programa, p.nombre AS programa_nombre, p.precio"
            + " FROM licencia l"
            + " JOIN cliente c ON c.id_cliente = l.id_cliente"
            + " JOIN programa p ON p.id_programa = l.id_programa";

    private final ConexionBD conexionBD;

    public LicenciaDAOMySQL(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Licencia guardar(Licencia l) {
        String sql = "INSERT INTO licencia (id_cliente, id_programa, tipo, fecha_venta, monto, descuento,"
                + " medio_pago, fecha_emision, fecha_vencimiento, estado, notas)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection cn = conexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, l.getCliente().getIdCliente());
            ps.setInt(2, l.getPrograma().getIdPrograma());
            ps.setString(3, l.getTipo().getEtiqueta());
            ps.setDate(4, Date.valueOf(l.getFechaVenta()));
            ps.setBigDecimal(5, l.getMonto());
            ps.setBigDecimal(6, l.getDescuento());
            ps.setString(7, l.getMedioPago());
            ps.setDate(8, Date.valueOf(l.getFechaEmision()));
            ps.setDate(9, Date.valueOf(l.getFechaVencimiento()));
            ps.setString(10, l.getEstado().getEtiqueta());
            ps.setString(11, l.getNotas());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    l.setIdLicencia(claves.getInt(1));
                }
            }
            return l;
        } catch (SQLException e) {
            throw new DaoException("No se pudo guardar la licencia", e);
        }
    }

    @Override
    public void actualizarEstado(int idLicencia, EstadoLicencia estado) {
        String sql = "UPDATE licencia SET estado = ? WHERE id_licencia = ?";
        try (Connection cn = conexionBD.abrir(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado.getEtiqueta());
            ps.setInt(2, idLicencia);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("No se pudo actualizar el estado de la licencia", e);
        }
    }

    @Override
    public List<Licencia> listar() {
        String sql = SELECT_LICENCIA + " ORDER BY l.fecha_venta DESC, l.id_licencia DESC";
        try (Connection cn = conexionBD.abrir();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Licencia> licencias = new ArrayList<>();
            while (rs.next()) {
                licencias.add(mapear(rs));
            }
            return licencias;
        } catch (SQLException e) {
            throw new DaoException("No se pudo listar las licencias", e);
        }
    }

    /**
     * Convierte una fila de {@link #SELECT_LICENCIA} en una licencia. También lo usa el DAO de alertas.
     */
    static Licencia mapear(ResultSet rs) throws SQLException {
        Licencia l = new Licencia();
        l.setIdLicencia(rs.getInt("id_licencia"));
        l.setCliente(new Cliente(rs.getInt("id_cliente"), rs.getString("cliente_nombre"),
                rs.getString("ruc"), rs.getString("contacto")));
        l.setPrograma(new Programa(rs.getInt("id_programa"), rs.getString("programa_nombre"),
                rs.getBigDecimal("precio")));
        l.setTipo(TipoLicencia.desdeEtiqueta(rs.getString("tipo")));
        l.setFechaVenta(rs.getDate("fecha_venta").toLocalDate());
        l.setMonto(rs.getBigDecimal("monto"));
        l.setDescuento(rs.getBigDecimal("descuento"));
        l.setMedioPago(rs.getString("medio_pago"));
        l.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
        l.setFechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate());
        l.setEstado(EstadoLicencia.desdeEtiqueta(rs.getString("estado")));
        l.setNotas(rs.getString("notas"));
        return l;
    }
}
