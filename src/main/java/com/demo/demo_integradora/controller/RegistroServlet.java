package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Dao.UsuarioDao;
import com.demo.demo_integradora.model.Rol;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.utils.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Pattern;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private static final String VISTA = "/registro.jsp";

    private static final Pattern CORREO_VALIDO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final UsuarioDao usuarioDao = new UsuarioDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombreUsuario = request.getParameter("nombreUsuario");
        String correo = request.getParameter("correoElectronico");
        String confirmarCorreo = request.getParameter("confirmarCorreoElectronico");
        String contrasena = request.getParameter("contrasena");
        String confirmarContrasena = request.getParameter("confirmarContrasena");

        String error = validar(nombreUsuario, correo, confirmarCorreo, contrasena, confirmarContrasena);

        if (error == null && usuarioDao.getByCorreo(correo) != null) {
            error = "Ya existe una cuenta registrada con ese correo.";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("nombreUsuario", nombreUsuario);
            request.setAttribute("correoElectronico", correo);
            request.setAttribute("confirmarCorreoElectronico", confirmarCorreo);
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        Usuario nuevo = new Usuario(nombreUsuario, correo, PasswordUtil.hash(contrasena), Rol.USUARIO);
        boolean creado = usuarioDao.create(nuevo);

        if (!creado) {
            request.setAttribute("error", "No se pudo crear la cuenta, intenta de nuevo.");
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/login?registrado=1");
    }

    private String validar(String nombreUsuario, String correo, String confirmarCorreo,
                            String contrasena, String confirmarContrasena) {
        if (isBlank(nombreUsuario) || isBlank(correo) || isBlank(confirmarCorreo)
                || isBlank(contrasena) || isBlank(confirmarContrasena)) {
            return "Todos los campos son obligatorios.";
        }
        if (!CORREO_VALIDO.matcher(correo).matches()) {
            return "El correo electrónico no tiene un formato válido.";
        }
        if (!correo.equalsIgnoreCase(confirmarCorreo)) {
            return "Los correos electrónicos no coinciden.";
        }
        if (!contrasena.equals(confirmarContrasena)) {
            return "Las contraseñas no coinciden.";
        }
        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
