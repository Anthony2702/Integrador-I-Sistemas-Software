package com.autodev.licencias.vista;

import com.autodev.licencias.dao.DaoException;
import java.awt.Component;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mensajes al usuario. Los errores técnicos se registran en el log y al usuario
 * se le muestra un mensaje simple, sin detalles internos.
 */
public final class Dialogos {

    private static final Logger log = LoggerFactory.getLogger(Dialogos.class);
    private static final String TITULO = "AUTODEV - Licencias";

    private Dialogos() {
    }

    /** Muestra un mensaje informativo. */
    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Muestra una advertencia (por ejemplo, un dato mal ingresado). */
    public static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, TITULO, JOptionPane.WARNING_MESSAGE);
    }

    /** @return true si el usuario respondió "Sí" */
    public static boolean confirmar(Component padre, String pregunta) {
        return JOptionPane.showConfirmDialog(padre, pregunta, TITULO, JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    /**
     * Muestra un error. Si es una validación, muestra su mensaje; si es de base de datos
     * u otro error inesperado, lo registra en el log y muestra un mensaje general.
     *
     * @param padre ventana sobre la que se muestra
     * @param error excepción capturada
     */
    public static void error(Component padre, RuntimeException error) {
        if (error instanceof IllegalArgumentException || error instanceof IllegalStateException) {
            aviso(padre, error.getMessage());
            return;
        }
        if (error instanceof DaoException) {
            log.error(error.getMessage(), error);
            JOptionPane.showMessageDialog(padre, error.getMessage()
                    + ".\nRevisa que MySQL esté encendido. El detalle quedó en logs/autodev-licencias.log",
                    TITULO, JOptionPane.ERROR_MESSAGE);
            return;
        }
        log.error("Error inesperado", error);
        JOptionPane.showMessageDialog(padre, "Ocurrió un error inesperado. El detalle quedó en el log.",
                TITULO, JOptionPane.ERROR_MESSAGE);
    }
}
