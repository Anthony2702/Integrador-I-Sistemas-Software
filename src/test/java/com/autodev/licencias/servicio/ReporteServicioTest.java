package com.autodev.licencias.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.autodev.licencias.dao.memoria.AlertaDAOMemoria;
import com.autodev.licencias.dao.memoria.LicenciaDAOMemoria;
import com.autodev.licencias.dao.memoria.ProgramaDAOMemoria;
import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.ResumenMes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del reporte "Resumen" (ventas del mes y estado de la cartera).
 */
class ReporteServicioTest {

    private final Programa autodev = new Programa(1, "AUTODEV", new BigDecimal("250.00"));
    private final Programa autosire = new Programa(2, "AUTOSIRE", new BigDecimal("180.00"));
    private final Programa autoguias = new Programa(3, "AUTOGUIAS", new BigDecimal("150.00"));
    private final Cliente andina = new Cliente(1, "Comercial Andina SAC", "20601234561", "");
    private final Cliente rojas = new Cliente(2, "Estudio Contable Rojas", "10456789012", "");
    private final Cliente quispe = new Cliente(3, "María Quispe", null, "");

    private LicenciaDAOMemoria licenciaDAO;
    private ReporteServicio servicio;

    @BeforeEach
    void preparar() {
        licenciaDAO = new LicenciaDAOMemoria();
        LicenciaServicio licencias = new LicenciaServicio(licenciaDAO, new AlertaDAOMemoria(),
                LicenciaServicioTest.RELOJ, 30);
        servicio = new ReporteServicio(licencias, new ProgramaDAOMemoria(autodev, autosire, autoguias),
                new BigDecimal("1500"));

        guardar(andina, autodev, LocalDate.of(2026, 10, 5), "250.00", "0.10");   // octubre, vigente
        guardar(rojas, autosire, LocalDate.of(2026, 10, 20), "180.00", "0");     // octubre, vigente
        guardar(andina, autodev, LocalDate.of(2026, 9, 15), "250.00", "0");      // setiembre, vigente
        guardar(rojas, autoguias, LocalDate.of(2025, 11, 6), "150.00", "0");     // vence en 10 días
        guardar(quispe, autodev, LocalDate.of(2025, 9, 1), "250.00", "0");       // vencida
    }

    private void guardar(Cliente cliente, Programa programa, LocalDate fecha, String monto, String descuento) {
        Licencia licencia = new Licencia();
        licencia.setCliente(cliente);
        licencia.setPrograma(programa);
        licencia.setFechaVenta(fecha);
        licencia.setFechaEmision(fecha);
        licencia.calcularVencimiento();
        licencia.setMonto(new BigDecimal(monto));
        licencia.setDescuento(new BigDecimal(descuento));
        licencia.setMedioPago("Yape");
        licenciaDAO.guardar(licencia);
    }

    @Test
    void sumaLosIngresosDelMesConDescuento() {
        ResumenMes resumen = servicio.resumen(YearMonth.of(2026, 10));

        assertEquals(new BigDecimal("405.00"), resumen.ingresos());
        assertEquals(2, resumen.licenciasVendidas());
        assertEquals(27, resumen.porcentajeMeta());
    }

    @Test
    void muestraLosIngresosPorProgramaIncluyendoLosQueNoVendieron() {
        ResumenMes resumen = servicio.resumen(YearMonth.of(2026, 10));

        assertEquals(List.of("AUTODEV", "AUTOSIRE", "AUTOGUIAS"), List.copyOf(resumen.ingresosPorPrograma().keySet()));
        assertEquals(new BigDecimal("225.00"), resumen.ingresosPorPrograma().get("AUTODEV"));
        assertEquals(new BigDecimal("180.00"), resumen.ingresosPorPrograma().get("AUTOSIRE"));
        assertEquals(new BigDecimal("0.00"), resumen.ingresosPorPrograma().get("AUTOGUIAS"));
    }

    @Test
    void cuentaElEstadoActualDeLaCartera() {
        ResumenMes resumen = servicio.resumen(YearMonth.of(2026, 10));

        assertEquals(3, resumen.vigentes());
        assertEquals(1, resumen.porVencer());
        assertEquals(1, resumen.vencidas());
        assertEquals(2, resumen.clientesActivos());
    }
}
