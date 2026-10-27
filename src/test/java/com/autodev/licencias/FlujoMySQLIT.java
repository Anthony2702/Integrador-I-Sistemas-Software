package com.autodev.licencias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.autodev.licencias.config.Configuracion;
import com.autodev.licencias.modelo.Cliente;
import com.autodev.licencias.modelo.EstadoLicencia;
import com.autodev.licencias.modelo.Licencia;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.Rol;
import com.autodev.licencias.modelo.TipoLicencia;
import com.autodev.licencias.modelo.Usuario;
import com.autodev.licencias.servicio.NuevaVenta;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * Prueba de integración contra MySQL: recorre el flujo completo con los DAO reales.
 * No corre con "mvn test" (termina en IT). Ejecutar con:
 * {@code mvn test -Dtest=FlujoMySQLIT -Dsurefire.failIfNoSpecifiedTests=false}
 * Deja datos de prueba en la base: usar solo en la base de desarrollo.
 */
class FlujoMySQLIT {

    @Test
    void ventaRenovacionAlertasYPreciosContraMySQL() {
        Assumptions.assumeTrue(Files.exists(Path.of("config.properties")), "Falta config.properties");
        Contexto contexto = new Contexto(Configuracion.cargar());
        Assumptions.assumeTrue(contexto.getConexion().probar(), "MySQL no está disponible");

        String ruc = "20" + (100_000_000 + ThreadLocalRandom.current().nextInt(800_000_000));
        Cliente cliente = contexto.getClientes().registrar("Cliente de integración", ruc, "987000111");
        assertTrue(cliente.getIdCliente() > 0);

        Programa programa = contexto.getProgramas().listar().get(2);
        var hoy = contexto.getLicencias().hoy();
        Licencia anterior = contexto.getLicencias().registrarVenta(new NuevaVenta(cliente, programa,
                TipoLicencia.NUEVA, hoy.minusYears(1).plusDays(10), programa.getPrecio(), BigDecimal.TEN, "Yape", "IT"));
        assertTrue(contexto.getLicencias().listar().stream().anyMatch(l -> l.getIdLicencia() == anterior.getIdLicencia()));

        contexto.getAlertas().generarAlertas();
        assertTrue(contexto.getAlertas().listarPendientes().stream()
                .anyMatch(a -> a.getLicencia().getIdLicencia() == anterior.getIdLicencia()));

        Licencia nueva = contexto.getLicencias().registrarVenta(new NuevaVenta(cliente, programa,
                TipoLicencia.RENOVACION, hoy, programa.getPrecio(), BigDecimal.ZERO, "Transferencia", null));
        assertEquals(anterior.getFechaVencimiento(), nueva.getFechaEmision());
        Licencia guardada = contexto.getLicencias().listar().stream()
                .filter(l -> l.getIdLicencia() == anterior.getIdLicencia()).findFirst().orElseThrow();
        assertEquals(EstadoLicencia.RENOVADA, guardada.getEstado());
        assertTrue(contexto.getAlertas().listarPendientes().stream()
                .noneMatch(a -> a.getLicencia().getIdLicencia() == anterior.getIdLicencia()));

        Usuario admin = new Usuario("admin", "", "Administrador", Rol.ADMIN);
        BigDecimal precioOriginal = programa.getPrecio();
        contexto.getProgramas().cambiarPrecio(admin, programa, new BigDecimal("155.50"));
        assertEquals(new BigDecimal("155.50"), contexto.getProgramas().listar().get(2).getPrecio());
        contexto.getProgramas().cambiarPrecio(admin, programa, precioOriginal);
    }
}
