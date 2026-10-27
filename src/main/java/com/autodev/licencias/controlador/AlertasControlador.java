package com.autodev.licencias.controlador;

import com.autodev.licencias.Contexto;
import com.autodev.licencias.modelo.Alerta;
import com.autodev.licencias.vista.AlertasPanel;
import com.autodev.licencias.vista.Dialogos;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de la pantalla "Alertas".
 */
public class AlertasControlador {

    private static final Logger log = LoggerFactory.getLogger(AlertasControlador.class);

    private final AlertasPanel vista;
    private final Contexto contexto;
    private final Navegacion navegacion;

    public AlertasControlador(AlertasPanel vista, Contexto contexto, Navegacion navegacion) {
        this.vista = vista;
        this.contexto = contexto;
        this.navegacion = navegacion;
        vista.getBtnRevisar().addActionListener(e -> revisar());
        vista.getBtnContactar().addActionListener(e -> contactar());
        vista.getBtnRenovar().addActionListener(e -> renovar());
        vista.getBtnAtendida().addActionListener(e -> atender());
    }

    /** Vuelve a leer las alertas pendientes. */
    public void refrescar() {
        vista.cargar(contexto.getAlertas().listarPendientes(), contexto.getLicencias().hoy());
    }

    private void revisar() {
        try {
            int nuevas = contexto.getAlertas().generarAlertas();
            refrescar();
            navegacion.alertasCambiaron();
            Dialogos.info(vista, nuevas == 0 ? "No hay vencimientos nuevos." : "Se generaron " + nuevas + " alertas nuevas.");
        } catch (RuntimeException e) {
            Dialogos.error(vista, e);
        }
    }

    private Alerta seleccionada() {
        Alerta alerta = vista.getAlertaSeleccionada();
        if (alerta == null) {
            Dialogos.aviso(vista, "Selecciona una alerta de la lista");
        }
        return alerta;
    }

    /** Abre WhatsApp con el mensaje ya escrito; si no hay celular, copia el mensaje. */
    private void contactar() {
        Alerta alerta = seleccionada();
        if (alerta == null) {
            return;
        }
        String mensaje = contexto.getAlertas().mensajeRenovacion(alerta);
        Optional<URI> enlace = contexto.getAlertas().enlaceWhatsApp(alerta);
        try {
            if (enlace.isPresent() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(enlace.get());
            } else {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(mensaje), null);
                Dialogos.info(vista, "El cliente no tiene un celular registrado.\nContacto: "
                        + alerta.getLicencia().getCliente().getContacto()
                        + "\n\nSe copió este mensaje para que lo envíes:\n\n" + mensaje);
            }
        } catch (IOException e) {
            log.error("No se pudo abrir WhatsApp", e);
            Dialogos.aviso(vista, "No se pudo abrir el navegador.");
            return;
        }
        if (Dialogos.confirmar(vista, "¿Ya contactaste al cliente? La alerta se marcará como atendida.")) {
            atender(alerta);
        }
    }

    private void renovar() {
        Alerta alerta = seleccionada();
        if (alerta != null) {
            navegacion.renovar(alerta.getLicencia());
        }
    }

    private void atender() {
        Alerta alerta = seleccionada();
        if (alerta != null) {
            atender(alerta);
        }
    }

    private void atender(Alerta alerta) {
        try {
            contexto.getAlertas().atender(alerta);
            refrescar();
            navegacion.alertasCambiaron();
        } catch (RuntimeException e) {
            Dialogos.error(vista, e);
        }
    }
}
