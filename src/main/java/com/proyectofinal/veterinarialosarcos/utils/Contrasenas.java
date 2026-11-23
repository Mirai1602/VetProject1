package com.proyectofinal.veterinarialosarcos.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifrado de contraseñas con PBKDF2-HMAC-SHA256 y sal aleatoria (incluido en Java,
 * sin dependencias extra). Formato guardado: {@code pbkdf2$iteraciones$sal$hash}.
 */
public final class Contrasenas {

    private static final String PREFIJO = "pbkdf2$";
    private static final int ITERACIONES = 120_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private Contrasenas() {
    }

    public static String cifrar(String contrasena) {
        byte[] sal = new byte[BYTES_SAL];
        ALEATORIO.nextBytes(sal);
        byte[] hash = pbkdf2(contrasena, sal, ITERACIONES);
        Base64.Encoder b64 = Base64.getEncoder();
        return PREFIJO + ITERACIONES + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(hash);
    }

    /** Compara una contraseña escrita con la guardada (cifrada o, si es antigua, en texto plano). */
    public static boolean verificar(String contrasena, String guardada) {
        if (contrasena == null || guardada == null) {
            return false;
        }
        if (!estaCifrada(guardada)) {
            return MessageDigest.isEqual(contrasena.getBytes(), guardada.getBytes());
        }
        String[] partes = guardada.split("\\$");
        if (partes.length != 4) {
            return false;
        }
        Base64.Decoder b64 = Base64.getDecoder();
        byte[] sal = b64.decode(partes[2]);
        byte[] esperado = b64.decode(partes[3]);
        byte[] calculado = pbkdf2(contrasena, sal, Integer.parseInt(partes[1]));
        return MessageDigest.isEqual(esperado, calculado);
    }

    public static boolean estaCifrada(String guardada) {
        return guardada != null && guardada.startsWith(PREFIJO);
    }

    private static byte[] pbkdf2(String contrasena, byte[] sal, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), sal, iteraciones, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo cifrar la contraseña", e);
        } finally {
            spec.clearPassword();
        }
    }
}
