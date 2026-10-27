package com.autodev.licencias.config;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lee la configuración del archivo {@code config.properties}.
 * Ese archivo tiene las claves de la base de datos y no se sube a GitHub.
 */
public final class Configuracion {

    private static final Logger log = LoggerFactory.getLogger(Configuracion.class);
    private static final String ARCHIVO = "config.properties";

    private final Properties propiedades;

    private Configuracion(Properties propiedades) {
        this.propiedades = propiedades;
    }

    /**
     * Carga {@code config.properties} desde la carpeta donde se ejecuta el sistema.
     *
     * @return la configuración leída
     * @throws IllegalStateException si el archivo no existe
     */
    public static Configuracion cargar() {
        Path ruta = Path.of(ARCHIVO);
        if (!Files.exists(ruta)) {
            throw new IllegalStateException("No se encontró " + ruta.toAbsolutePath()
                    + ". Copia config.example.properties como config.properties.");
        }
        Properties propiedades = new Properties();
        try (Reader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            propiedades.load(lector);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ARCHIVO, e);
        }
        log.info("Configuración cargada desde {}", ruta.toAbsolutePath());
        return new Configuracion(propiedades);
    }

    /**
     * Crea una configuración a partir de propiedades (se usa en las pruebas).
     *
     * @param propiedades valores de configuración
     * @return la configuración
     */
    public static Configuracion de(Properties propiedades) {
        return new Configuracion(propiedades);
    }

    private String texto(String clave, String porDefecto) {
        return StringUtils.defaultIfBlank(propiedades.getProperty(clave), porDefecto).trim();
    }

    /** @return URL JDBC de MySQL */
    public String urlBaseDatos() {
        return texto("db.url", "jdbc:mysql://localhost:3306/autodev_licencias");
    }

    /** @return usuario de MySQL */
    public String usuarioBaseDatos() {
        return texto("db.usuario", "autodev_app");
    }

    /** @return clave de MySQL */
    public String claveBaseDatos() {
        return texto("db.clave", "");
    }

    /** @return días antes del vencimiento en que se genera la alerta (30 por defecto) */
    public int diasAviso() {
        return NumberUtils.toInt(texto("alerta.dias", "30"), 30);
    }

    /** @return meta de ventas mensual en soles */
    public BigDecimal metaMensual() {
        return NumberUtils.toScaledBigDecimal(texto("meta.mensual", "0"));
    }

    /** @return clave del usuario admin que se crea la primera vez */
    public String claveInicialAdmin() {
        return texto("admin.clave.inicial", "");
    }

    /** @return clave del usuario vendedor que se crea la primera vez */
    public String claveInicialVendedor() {
        return texto("vendedor.clave.inicial", "");
    }
}
