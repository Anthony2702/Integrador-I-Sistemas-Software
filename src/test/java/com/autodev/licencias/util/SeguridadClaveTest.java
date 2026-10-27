package com.autodev.licencias.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Pruebas del cifrado de claves (hash PBKDF2 con sal).
 */
class SeguridadClaveTest {

    @Test
    void elHashNoContieneLaClave() {
        String hash = SeguridadClave.generarHash("MiClave2026!".toCharArray());

        assertFalse(hash.contains("MiClave2026!"));
    }

    @Test
    void aceptaLaClaveCorrecta() {
        String hash = SeguridadClave.generarHash("MiClave2026!".toCharArray());

        assertTrue(SeguridadClave.verificar("MiClave2026!".toCharArray(), hash));
    }

    @Test
    void rechazaUnaClaveIncorrecta() {
        String hash = SeguridadClave.generarHash("MiClave2026!".toCharArray());

        assertFalse(SeguridadClave.verificar("otraClave".toCharArray(), hash));
    }

    @Test
    void laMismaClaveGeneraHashesDistintosPorLaSal() {
        String hash1 = SeguridadClave.generarHash("MiClave2026!".toCharArray());
        String hash2 = SeguridadClave.generarHash("MiClave2026!".toCharArray());

        assertNotEquals(hash1, hash2);
    }

    @Test
    void rechazaUnHashMalFormado() {
        assertFalse(SeguridadClave.verificar("MiClave2026!".toCharArray(), "texto-cualquiera"));
        assertFalse(SeguridadClave.verificar("MiClave2026!".toCharArray(), null));
    }
}
