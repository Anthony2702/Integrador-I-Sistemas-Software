package com.autodev.licencias.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las reglas de negocio de una licencia (escritas antes del código, TDD).
 */
class LicenciaTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 27);
    private static final int DIAS_AVISO = 30;

    private Licencia licenciaQueVence(LocalDate vencimiento) {
        Licencia licencia = new Licencia();
        licencia.setFechaEmision(vencimiento.minusYears(1));
        licencia.calcularVencimiento();
        return licencia;
    }

    @Test
    void venceUnAnioDespuesDeLaEmision() {
        Licencia licencia = new Licencia();
        licencia.setFechaEmision(LocalDate.of(2026, 1, 15));

        assertEquals(LocalDate.of(2027, 1, 15), licencia.calcularVencimiento());
        assertEquals(LocalDate.of(2027, 1, 15), licencia.getFechaVencimiento());
    }

    @Test
    void emitidaUn29DeFebreroVenceEl28DeFebrero() {
        Licencia licencia = new Licencia();
        licencia.setFechaEmision(LocalDate.of(2028, 2, 29));

        assertEquals(LocalDate.of(2029, 2, 28), licencia.calcularVencimiento());
    }

    @Test
    void sinFechaDeEmisionNoSePuedeCalcularElVencimiento() {
        assertThrows(IllegalStateException.class, () -> new Licencia().calcularVencimiento());
    }

    @Test
    void conMasDe30DiasEstaVigente() {
        Licencia licencia = licenciaQueVence(HOY.plusDays(31));

        assertEquals(EstadoLicencia.VIGENTE, licencia.actualizarEstado(HOY, DIAS_AVISO));
    }

    @Test
    void con30DiasOMenosEstaPorVencer() {
        assertEquals(EstadoLicencia.POR_VENCER,
                licenciaQueVence(HOY.plusDays(30)).actualizarEstado(HOY, DIAS_AVISO));
        assertEquals(EstadoLicencia.POR_VENCER,
                licenciaQueVence(HOY).actualizarEstado(HOY, DIAS_AVISO));
    }

    @Test
    void despuesDelVencimientoEstaVencida() {
        Licencia licencia = licenciaQueVence(HOY.minusDays(1));

        assertEquals(EstadoLicencia.VENCIDA, licencia.actualizarEstado(HOY, DIAS_AVISO));
    }

    @Test
    void unaLicenciaRenovadaNoCambiaDeEstado() {
        Licencia licencia = licenciaQueVence(HOY.minusDays(10));
        licencia.setEstado(EstadoLicencia.RENOVADA);

        assertEquals(EstadoLicencia.RENOVADA, licencia.actualizarEstado(HOY, DIAS_AVISO));
    }

    @Test
    void calculaLosDiasRestantes() {
        assertEquals(16, licenciaQueVence(HOY.plusDays(16)).diasRestantes(HOY));
        assertEquals(-30, licenciaQueVence(HOY.minusDays(30)).diasRestantes(HOY));
    }

    @Test
    void elTotalAplicaElDescuento() {
        Licencia licencia = new Licencia();
        licencia.setMonto(new BigDecimal("250.00"));
        licencia.setDescuento(new BigDecimal("0.10"));

        assertEquals(new BigDecimal("225.00"), licencia.getMontoFinal());
    }

    @Test
    void sinDescuentoElTotalEsElMonto() {
        Licencia licencia = new Licencia();
        licencia.setMonto(new BigDecimal("180"));

        assertEquals(new BigDecimal("180.00"), licencia.getMontoFinal());
    }

    @Test
    void alMarcarUnaAlertaQuedaAtendida() {
        Alerta alerta = new Alerta(licenciaQueVence(HOY.plusDays(10)), HOY.atStartOfDay(), 10);

        alerta.marcarAtendida();

        assertTrue(alerta.isAtendida());
    }

    @Test
    void losEstadosSeLeenDesdeSuEtiqueta() {
        assertEquals(EstadoLicencia.POR_VENCER, EstadoLicencia.desdeEtiqueta("Por vencer"));
        assertEquals(TipoLicencia.RENOVACION, TipoLicencia.desdeEtiqueta("Renovación"));
    }
}
