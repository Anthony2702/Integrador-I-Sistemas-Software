package com.autodev.licencias.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.autodev.licencias.dao.memoria.ProgramaDAOMemoria;
import com.autodev.licencias.modelo.Programa;
import com.autodev.licencias.modelo.Rol;
import com.autodev.licencias.modelo.Usuario;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del cambio de precios (solo el administrador puede hacerlo).
 */
class ProgramaServicioTest {

    private final Usuario admin = new Usuario("admin", "hash", "Administrador", Rol.ADMIN);
    private final Usuario vendedor = new Usuario("vendedor", "hash", "Vendedor", Rol.VENDEDOR);
    private Programa autosire;
    private ProgramaServicio servicio;

    @BeforeEach
    void preparar() {
        autosire = new Programa(2, "AUTOSIRE", new BigDecimal("180.00"));
        servicio = new ProgramaServicio(new ProgramaDAOMemoria(autosire));
    }

    @Test
    void elAdministradorCambiaElPrecio() {
        servicio.cambiarPrecio(admin, autosire, new BigDecimal("200.00"));

        assertEquals(new BigDecimal("200.00"), servicio.listar().get(0).getPrecio());
    }

    @Test
    void elVendedorNoPuedeCambiarPrecios() {
        assertThrows(IllegalStateException.class,
                () -> servicio.cambiarPrecio(vendedor, autosire, new BigDecimal("200.00")));
    }

    @Test
    void rechazaUnPrecioCeroONegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.cambiarPrecio(admin, autosire, BigDecimal.ZERO));
    }
}
