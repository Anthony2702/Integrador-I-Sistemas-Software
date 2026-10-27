package com.autodev.licencias.dao;

/**
 * Error al leer o escribir en la base de datos.
 * Envuelve la {@link java.sql.SQLException} para que las capas superiores no dependan de JDBC.
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
