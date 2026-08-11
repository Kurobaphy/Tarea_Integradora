package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Dao.UsuarioDao;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.utils.PasswordUtil;
import com.demo.demo_integradora.utils.SesionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String VISTA = "/login.jsp";
    private static final int MAX_INTENTOS = 3;
    private static final int MINUTOS_BLOQUEO = 15;
    private static final String ERROR_GENERICO = "Correo o contraseña incorrectos.";

    private final UsuarioDao usuarioDao = new UsuarioDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correoElectronico");
        String contrasena = request.getParameter("contrasena");

        Usuario usuario = usuarioDao.getByCorreo(correo);

        if (usuario == null) {
            mostrarError(request, response, ERROR_GENERICO);
            return;
        }

        if (usuario.estaBloqueado()) {
            mostrarError(request, response, "Cuenta bloqueada temporalmente. Intenta más tarde.");
            return;
        }

        if (!PasswordUtil.verificar(contrasena, usuario.getContrasenaHash())) {
            registrarIntentoFallido(usuario);
            mostrarError(request, response, ERROR_GENERICO);
            return;
        }

        usuarioDao.resetearIntentos(usuario.getId());
        SesionUtil.iniciarSesion(request, usuario);


        response.sendRedirect(request.getContextPath() + "/busqueda");
    }

    private void registrarIntentoFallido(Usuario usuario) {
        usuarioDao.incrementarIntentosFallidos(usuario.getId());
        int intentosActualizados = usuario.getIntentosFallidos() + 1;
        if (intentosActualizados >= MAX_INTENTOS) {
            usuarioDao.bloquearHasta(usuario.getId(), LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEO));
        }
    }

    private void mostrarError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.getRequestDispatcher(VISTA).forward(request, response);
    }
}
