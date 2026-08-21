package com.demo.demo_integradora.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String contrasenaPlano) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytesHash = md.digest(contrasenaPlano.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytesHash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash de la contraseña", e);
        }
    }

    public static boolean verificar(String contrasenaPlano, String hashGuardado) {
        return hash(contrasenaPlano).equals(hashGuardado);
    }
}
