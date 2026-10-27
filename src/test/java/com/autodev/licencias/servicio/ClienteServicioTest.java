package com.autodev.licencias.servicio;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.autodev.licencias.dao.memoria.ClienteDAOMemoria;
import com.autodev.licencias.modelo.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del registro y la validación de clientes.
 */
class ClienteServicioTest {

    private ClienteServicio servicio;

    @BeforeEach
    void preparar() {
        servicio = new ClienteServicio(new ClienteDAOMemoria());
    }

    @Test
    void registraUnClienteYOrdenaLosEspacios() {
        Cliente cliente = servicio.registrar("  Ferretería   El Progreso ", "20487654321", " 945112233 ");

        assertTrue(cliente.getIdCliente() > 0);
        assertEquals("Ferretería El Progreso", cliente.getNombre());
        assertEquals("945112233", cliente.getContacto());
    }

    @Test
    void rechazaUnNombreVacio() {
        assertThrows(IllegalArgumentException.class, () -> servicio.registrar("   ", "20487654321", ""));
    }

    @Test
    void rechazaUnRucQueNoTiene11Digitos() {
        assertThrows(IllegalArgumentException.class, () -> servicio.registrar("Botica Salud", "123", ""));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrar("Botica Salud", "2060123456A", ""));
    }

    @Test
    void unRucVacioSeGuardaComoNulo() {
        Cliente cliente = servicio.registrar("María Quispe", "  ", "998877665");

        assertNull(cliente.getRuc());
    }

    @Test
    void rechazaUnRucRepetido() {
        servicio.registrar("Comercial Andina SAC", "20601234561", "");

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar("Otra empresa", "20601234561", ""));
    }

    @Test
    void alEditarPuedeMantenerSuPropioRuc() {
        Cliente cliente = servicio.registrar("Comercial Andina SAC", "20601234561", "");

        assertDoesNotThrow(() -> servicio.actualizar(cliente, "Comercial Andina S.A.C.", "20601234561", "987654321"));
        assertEquals("Comercial Andina S.A.C.", cliente.getNombre());
    }
}
