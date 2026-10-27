package com.autodev.licencias.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.autodev.licencias.dao.memoria.AlertaDAOMemoria;
import com.autodev.licencias.dao.memoria.LicenciaDAOMemoria;
import com.autodev.licencias.modelo.Alerta;
import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.TipoLicencia;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del registro de ventas y renovaciones (con DAO en memoria, sin MySQL).
 */
class LicenciaServicioTest {

    static final Clock RELOJ = Clock.fixed(Instant.parse("2026-10-27T15:00:00Z"), ZoneId.of("America/Lima"));
    static final LocalDate HOY = LocalDate.of(2026, 10, 27);

    private final Cliente cliente = new Cliente(1, "Comercial Andina SAC", "20601234561", "987654321");
    private final Programa autodev = new Programa(1, "AUTODEV", new BigDecimal("250.00"));

    private LicenciaDAOMemoria licenciaDAO;
    private AlertaDAOMemoria alertaDAO;
    private LicenciaServicio servicio;

    @BeforeEach
    void preparar() {
        licenciaDAO = new LicenciaDAOMemoria();
        alertaDAO = new AlertaDAOMemoria();
        servicio = new LicenciaServicio(licenciaDAO, alertaDAO, RELOJ, 30);
    }

    private NuevaVenta venta(TipoLicencia tipo, LocalDate fecha, String monto, String descuento) {
        return new NuevaVenta(cliente, autodev, tipo, fecha, new BigDecimal(monto), new BigDecimal(descuento), "Yape", null);
    }

    private Licencia ventaAnteriorQueVence(LocalDate vencimiento) {
        return servicio.registrarVenta(venta(TipoLicencia.NUEVA, vencimiento.minusYears(1), "250", "0"));
    }

    @Test
    void registrarVentaCalculaElVencimientoYLaGuarda() {
        Licencia licencia = servicio.registrarVenta(venta(TipoLicencia.NUEVA, HOY, "250", "10"));

        assertTrue(licencia.getIdLicencia() > 0);
        assertEquals(HOY, licencia.getFechaEmision());
        assertEquals(HOY.plusYears(1), licencia.getFechaVencimiento());
        assertEquals(EstadoLicencia.VIGENTE, licencia.getEstado());
        assertEquals(0, new BigDecimal("0.10").compareTo(licencia.getDescuento()));
        assertEquals(new BigDecimal("225.00"), licencia.getMontoFinal());
        assertEquals(1, licenciaDAO.listar().size());
    }

    @Test
    void rechazaMontoCeroONegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarVenta(venta(TipoLicencia.NUEVA, HOY, "0", "0")));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarVenta(venta(TipoLicencia.NUEVA, HOY, "-5", "0")));
    }

    @Test
    void rechazaDescuentoFueraDeRango() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarVenta(venta(TipoLicencia.NUEVA, HOY, "250", "101")));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarVenta(venta(TipoLicencia.NUEVA, HOY, "250", "-1")));
    }

    @Test
    void rechazaVentaSinCliente() {
        NuevaVenta sinCliente = new NuevaVenta(null, autodev, TipoLicencia.NUEVA, HOY,
                new BigDecimal("250"), BigDecimal.ZERO, "Yape", null);

        assertThrows(IllegalArgumentException.class, () -> servicio.registrarVenta(sinCliente));
    }

    @Test
    void rechazaVentaConFechaFutura() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarVenta(venta(TipoLicencia.NUEVA, HOY.plusDays(1), "250", "0")));
    }

    @Test
    void rechazaVentaSinMedioDePago() {
        NuevaVenta sinMedio = new NuevaVenta(cliente, autodev, TipoLicencia.NUEVA, HOY,
                new BigDecimal("250"), BigDecimal.ZERO, "  ", null);

        assertThrows(IllegalArgumentException.class, () -> servicio.registrarVenta(sinMedio));
    }

    @Test
    void laRenovacionEmpiezaCuandoVenceLaLicenciaAnterior() {
        Licencia anterior = ventaAnteriorQueVence(HOY.plusDays(10));

        Licencia nueva = servicio.registrarVenta(venta(TipoLicencia.RENOVACION, HOY, "250", "0"));

        assertEquals(HOY.plusDays(10), nueva.getFechaEmision());
        assertEquals(HOY.plusDays(10).plusYears(1), nueva.getFechaVencimiento());
        assertEquals(EstadoLicencia.RENOVADA, licenciaDAO.buscar(anterior.getIdLicencia()).getEstado());
    }

    @Test
    void renovarUnaLicenciaVencidaEmpiezaHoy() {
        ventaAnteriorQueVence(HOY.minusDays(30));

        Licencia nueva = servicio.registrarVenta(venta(TipoLicencia.RENOVACION, HOY, "250", "0"));

        assertEquals(HOY, nueva.getFechaEmision());
    }

    @Test
    void renovarAtiendeLasAlertasDeLaLicenciaAnterior() {
        Licencia anterior = ventaAnteriorQueVence(HOY.plusDays(10));
        alertaDAO.guardar(new Alerta(anterior, HOY.atStartOfDay(), 10));

        servicio.registrarVenta(venta(TipoLicencia.RENOVACION, HOY, "250", "0"));

        assertTrue(alertaDAO.listarPendientes().isEmpty());
    }

    @Test
    void noSePuedeRenovarSinUnaLicenciaAnterior() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarVenta(venta(TipoLicencia.RENOVACION, HOY, "250", "0")));
    }

    @Test
    void listarActualizaYGuardaLosEstados() {
        Licencia licencia = ventaAnteriorQueVence(HOY.plusDays(40));
        licencia.setFechaVencimiento(HOY.plusDays(5));

        servicio.listar();

        assertEquals(EstadoLicencia.POR_VENCER, licenciaDAO.buscar(licencia.getIdLicencia()).getEstado());
    }
}
