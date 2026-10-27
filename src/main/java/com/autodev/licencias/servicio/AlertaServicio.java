package com.autodev.licencias.servicio;

import com.autodev.licencias.dao.AlertaDAO;
import com.autodev.licencias.modelo.Alerta;
import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Genera y atiende las alertas de vencimiento.
 * Se ejecuta al iniciar sesión y luego una vez al día.
 */
public class AlertaServicio {

    private static final Logger log = LoggerFactory.getLogger(AlertaServicio.class);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LicenciaServicio licencias;
    private final AlertaDAO alertaDAO;
    private final Clock reloj;

    public AlertaServicio(LicenciaServicio licencias, AlertaDAO alertaDAO, Clock reloj) {
        this.licencias = licencias;
        this.alertaDAO = alertaDAO;
        this.reloj = reloj;
    }

    /**
     * Revisa las licencias y crea una alerta para cada una que esté por vencer o vencida
     * y que todavía no tenga alerta.
     *
     * @return cantidad de alertas nuevas
     */
    public int generarAlertas() {
        int nuevas = 0;
        for (Licencia licencia : licencias.listar()) {
            boolean requiereAviso = licencia.getEstado() == EstadoLicencia.POR_VENCER
                    || licencia.getEstado() == EstadoLicencia.VENCIDA;
            if (requiereAviso && !alertaDAO.existeParaLicencia(licencia.getIdLicencia())) {
                int dias = (int) licencia.diasRestantes(licencias.hoy());
                alertaDAO.guardar(new Alerta(licencia, LocalDateTime.now(reloj), dias));
                nuevas++;
            }
        }
        log.info("Revisión de vencimientos: {} alertas nuevas", nuevas);
        return nuevas;
    }

    /** @return alertas que faltan atender, de la licencia que vence primero a la última */
    public List<Alerta> listarPendientes() {
        return alertaDAO.listarPendientes();
    }

    /**
     * Marca la alerta como atendida.
     *
     * @param alerta alerta que el vendedor ya trabajó
     */
    public void atender(Alerta alerta) {
        alertaDAO.marcarAtendida(alerta.getIdAlerta());
        alerta.marcarAtendida();
        log.info("Alerta {} atendida", alerta.getIdAlerta());
    }

    /**
     * Mensaje que se envía al cliente para ofrecerle la renovación.
     *
     * @param alerta alerta pendiente
     * @return texto del mensaje
     */
    public String mensajeRenovacion(Alerta alerta) {
        Licencia licencia = alerta.getLicencia();
        String fecha = licencia.getFechaVencimiento().format(FECHA);
        String situacion = licencia.diasRestantes(licencias.hoy()) < 0 ? "venció el " : "vence el ";
        return "Hola, " + licencia.getCliente().getNombre() + ". Le escribimos de AUTODEV: su licencia de "
                + licencia.getPrograma().getNombre() + " " + situacion + fecha
                + ". ¿Desea renovarla para seguir usando el programa sin interrupciones?";
    }

    /**
     * Enlace de WhatsApp con el mensaje ya escrito, si el contacto del cliente es un celular.
     *
     * @param alerta alerta pendiente
     * @return enlace wa.me, o vacío si el contacto no es un celular
     */
    public Optional<URI> enlaceWhatsApp(Alerta alerta) {
        String digitos = StringUtils.getDigits(alerta.getLicencia().getCliente().getContacto());
        if (digitos.length() == 9 && digitos.startsWith("9")) {
            digitos = "51" + digitos;   // celular peruano sin código de país
        }
        if (digitos.length() != 11 || !digitos.startsWith("519")) {
            return Optional.empty();
        }
        String texto = URLEncoder.encode(mensajeRenovacion(alerta), StandardCharsets.UTF_8).replace("+", "%20");
        return Optional.of(URI.create("https://wa.me/" + digitos + "?text=" + texto));
    }
}
