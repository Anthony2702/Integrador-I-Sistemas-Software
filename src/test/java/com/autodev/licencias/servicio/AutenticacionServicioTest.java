package com.autodev.licencias.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.autodev.licencias.dao.memoria.UsuarioDAOMemoria;
import com.autodev.licencias.modelo.Rol;
import com.autodev.licencias.modelo.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del inicio de sesión.
 */
class AutenticacionServicioTest {

    private UsuarioDAOMemoria usuarioDAO;
    private AutenticacionServicio servicio;

    @BeforeEach
    void preparar() {
        usuarioDAO = new UsuarioDAOMemoria();
        servicio = new AutenticacionServicio(usuarioDAO);
        servicio.crearUsuariosIniciales("Admin2026!", "Ventas2026!");
    }

    @Test
    void creaAdministradorYVendedorLaPrimeraVez() {
        Usuario admin = usuarioDAO.buscarPorUsuario("admin").orElseThrow();

        assertEquals(2, usuarioDAO.contar());
        assertEquals(Rol.ADMIN, admin.getRol());
        assertNotEquals("Admin2026!", admin.getClaveHash());
    }

    @Test
    void noVuelveACrearUsuariosSiYaExisten() {
        servicio.crearUsuariosIniciales("Admin2026!", "Ventas2026!");

        assertEquals(2, usuarioDAO.contar());
    }

    @Test
    void ingresaConLaClaveCorrecta() {
        Usuario usuario = servicio.iniciarSesion("admin", "Admin2026!".toCharArray()).orElseThrow();

        assertTrue(usuario.esAdmin());
    }

    @Test
    void rechazaUnaClaveIncorrecta() {
        assertTrue(servicio.iniciarSesion("admin", "clave-mala".toCharArray()).isEmpty());
    }

    @Test
    void rechazaUnUsuarioQueNoExiste() {
        assertTrue(servicio.iniciarSesion("pedro", "Admin2026!".toCharArray()).isEmpty());
    }

    @Test
    void rechazaUnUsuarioInactivo() {
        usuarioDAO.buscarPorUsuario("vendedor").orElseThrow().setActivo(false);

        assertTrue(servicio.iniciarSesion("vendedor", "Ventas2026!".toCharArray()).isEmpty());
    }

    @Test
    void rechazaDatosVacios() {
        assertTrue(servicio.iniciarSesion("  ", "Admin2026!".toCharArray()).isEmpty());
        assertTrue(servicio.iniciarSesion("admin", new char[0]).isEmpty());
    }

    @Test
    void exigeUnaClaveInicialDeAlMenos8Caracteres() {
        AutenticacionServicio nuevo = new AutenticacionServicio(new UsuarioDAOMemoria());

        assertThrows(IllegalStateException.class, () -> nuevo.crearUsuariosIniciales("123", "Ventas2026!"));
    }
}
