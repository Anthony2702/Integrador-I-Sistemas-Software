package com.autodev.licencias.dao.mysql;

import com.autodev.licencias.config.Configuracion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abre conexiones a MySQL con los datos de {@code config.properties}.
 * Cada DAO abre y cierra su conexión con try-with-resources.
 */
public class ConexionBD {

    private static final Logger log = LoggerFactory.getLogger(ConexionBD.class);

    private final String url;
    private final String usuario;
    private final String clave;

    public ConexionBD(Configuracion configuracion) {
        this.url = configuracion.urlBaseDatos();
        this.usuario = configuracion.usuarioBaseDatos();
        this.clave = configuracion.claveBaseDatos();
    }

    /**
     * @return una conexión nueva a la base de datos
     * @throws SQLException si MySQL no responde o las credenciales son incorrectas
     */
    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }

    /** @return true si se pudo conectar a la base de datos */
    public boolean probar() {
        try (Connection conexion = abrir()) {
            return conexion.isValid(3);
        } catch (SQLException e) {
            // No se registra la clave, solo la URL y el usuario
            log.error("No se pudo conectar a {} con el usuario {}: {}", url, usuario, e.getMessage());
            return false;
        }
    }
}
