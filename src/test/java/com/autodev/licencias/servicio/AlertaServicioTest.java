package com.autodev.licencias.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.autodev.licencias.dao.memoria.AlertaDAOMemoria;
import com.autodev.licencias.dao.memoria.LicenciaDAOMemoria;
import com.autodev.licencias.modelo.Alerta;
import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Programa;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la generación de alertas de vencimiento.
 */
class AlertaServicioTest {

    private static final LocalDate HOY = LicenciaServicioTest.HOY;

    private final Programa autodev = new Programa(1, "AUTODEV", new BigDecimal("250.00"));

    private LicenciaDAOMemoria licenciaDAO;
    private AlertaDAOMemoria alertaDAO;
    private AlertaServicio servicio;

    @BeforeEach
    void preparar() {
        licenciaDAO = new LicenciaDAOMemoria();
        alertaDAO = new AlertaDAOMemoria();
        LicenciaServicio licencias = new LicenciaServicio(licenciaDAO, alertaDAO, LicenciaServicioTest.RELOJ, 30);
        servicio = new AlertaServicio(licencias, alertaDAO, LicenciaServicioTest.RELOJ);
    }

    private Licencia guardarQueVence(LocalDate vencimiento, String contacto) {
        Licencia licencia = new Licencia();
        licencia.setCliente(new Cliente(1, "Comercial Andina SAC", "20601234561", contacto));
        licencia.setPrograma(autodev);
        licencia.setFechaVenta(vencimiento.minusYears(1));
        licencia.setFechaEmision(vencimiento.minusYears(1));
        licencia.calcularVencimiento();
        licencia.setMonto(new BigDecimal("250.00"));
        licencia.setMedioPago("Yape");
        return licenciaDAO.guardar(licencia);
    }

    @Test
    void generaAlertaParaUnaLicenciaPorVencer() {
        guardarQueVence(HOY.plusDays(16), "987654321");

        assertEquals(1, servicio.generarAlertas());
        assertEquals(1, servicio.listarPendientes().size());
        assertEquals(16, servicio.listarPendientes().get(0).getDiasRestantes());
    }

    @Test
    void noGeneraAlertaParaUnaLicenciaVigente() {
        guardarQueVence(HOY.plusDays(31), "987654321");

        assertEquals(0, servicio.generarAlertas());
    }

    @Test
    void generaAlertaParaUnaLicenciaVencida() {
        guardarQueVence(HOY.minusDays(30), "987654321");

        assertEquals(1, servicio.generarAlertas());
        assertEquals(-30, servicio.listarPendientes().get(0).getDiasRestantes());
    }

    @Test
    void noDuplicaAlertas() {
        guardarQueVence(HOY.plusDays(16), "987654321");

        servicio.generarAlertas();

        assertEquals(0, servicio.generarAlertas());
        assertEquals(1, servicio.listarPendientes().size());
    }

    @Test
    void unaAlertaAtendidaNoVuelveAAparecer() {
        guardarQueVence(HOY.plusDays(16), "987654321");
        servicio.generarAlertas();

        servicio.atender(servicio.listarPendientes().get(0));
        servicio.generarAlertas();

        assertTrue(servicio.listarPendientes().isEmpty());
    }

    @Test
    void noAvisaLicenciasRenovadas() {
        Licencia licencia = guardarQueVence(HOY.plusDays(5), "987654321");
        licencia.setEstado(EstadoLicencia.RENOVADA);

        assertEquals(0, servicio.generarAlertas());
    }

    @Test
    void armaElEnlaceDeWhatsAppConUnCelularPeruano() {
        guardarQueVence(HOY.plusDays(16), "987 654 321");
        servicio.generarAlertas();
        Alerta alerta = servicio.listarPendientes().get(0);

        String enlace = servicio.enlaceWhatsApp(alerta).orElseThrow().toString();

        assertTrue(enlace.startsWith("https://wa.me/51987654321?text="));
    }

    @Test
    void sinCelularNoHayEnlaceDeWhatsApp() {
        guardarQueVence(HOY.plusDays(16), "contabilidad@pacifico.pe");
        servicio.generarAlertas();

        assertTrue(servicio.enlaceWhatsApp(servicio.listarPendientes().get(0)).isEmpty());
    }

    @Test
    void elMensajeIncluyeElProgramaYLaFechaDeVencimiento() {
        guardarQueVence(HOY.plusDays(16), "987654321");
        servicio.generarAlertas();

        String mensaje = servicio.mensajeRenovacion(servicio.listarPendientes().get(0));

        assertTrue(mensaje.contains("AUTODEV"));
        assertTrue(mensaje.contains("12/11/2026"));
    }
}
