package com.demo.demo_integradora.utils;

import java.security.SecureRandom;

public class CodigoRecuperacionUtil {

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGITUD = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private CodigoRecuperacionUtil() {
    }

    public static String generar() {
        StringBuilder sb = new StringBuilder(LONGITUD);
        for (int i = 0; i < LONGITUD; i++) {
            sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }
}
