package com.autodev.licencias.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Cifra y verifica claves con PBKDF2 (HMAC-SHA256) y una sal aleatoria.
 * Formato guardado: {@code iteraciones:salHex:hashHex}.
 */
public final class SeguridadClave {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 120_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    private SeguridadClave() {
    }

    /**
     * Genera el hash de una clave para guardarlo en la base de datos.
     *
     * @param clave clave en texto plano
     * @return hash con el formato {@code iteraciones:sal:hash}
     */
    public static String generarHash(char[] clave) {
        byte[] sal = new byte[BYTES_SAL];
        ALEATORIO.nextBytes(sal);
        byte[] hash = pbkdf2(clave, sal, ITERACIONES);
        return ITERACIONES + ":" + HEX.formatHex(sal) + ":" + HEX.formatHex(hash);
    }

    /**
     * Comprueba si una clave corresponde al hash guardado.
     *
     * @param clave clave escrita por el usuario
     * @param hashGuardado hash leído de la base de datos
     * @return true si la clave es correcta
     */
    public static boolean verificar(char[] clave, String hashGuardado) {
        if (hashGuardado == null) {
            return false;
        }
        String[] partes = hashGuardado.split(":");
        if (partes.length != 3) {
            return false;
        }
        try {
            int iteraciones = Integer.parseInt(partes[0]);
            byte[] sal = HEX.parseHex(partes[1]);
            byte[] esperado = HEX.parseHex(partes[2]);
            byte[] calculado = pbkdf2(clave, sal, iteraciones);
            // Comparación en tiempo constante para no dar pistas por el tiempo de respuesta
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] clave, byte[] sal, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(clave, sal, iteraciones, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo cifrar la clave", e);
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Borra la clave de la memoria después de usarla.
     *
     * @param clave arreglo a limpiar
     */
    public static void limpiar(char[] clave) {
        if (clave != null) {
            Arrays.fill(clave, '\0');
        }
    }
}
