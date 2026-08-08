package com.demo.demo_integradora.utils;

import com.demo.demo_integradora.model.Usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SesionUtil {

    public static final String ATRIBUTO_USUARIO = "usuarioSesion";

    private SesionUtil() {
    }

    public static void iniciarSesion(HttpServletRequest request, Usuario usuario) {
        HttpSession session = request.getSession(true);
        session.setAttribute(ATRIBUTO_USUARIO, usuario);
    }

    public static Usuario obtenerUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
    }

    public static boolean haySesion(HttpServletRequest request) {
        return obtenerUsuario(request) != null;
    }

    public static void cerrarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
