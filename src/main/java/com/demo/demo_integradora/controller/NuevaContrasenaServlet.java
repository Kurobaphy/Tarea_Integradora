package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Dao.UsuarioDao;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.utils.EmailUtil;
import com.demo.demo_integradora.utils.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/nueva-contrasena")
public class NuevaContrasenaServlet extends HttpServlet {

    private static final String VISTA = "/nueva-contrasena.jsp";
    private static final int LONGITUD_MINIMA = 8;

    private final UsuarioDao usuarioDao = new UsuarioDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (obtenerUsuarioIdEnProceso(request) == null) {
            response.sendRedirect(request.getContextPath() + "/recuperar");
            return;
        }
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long usuarioId = obtenerUsuarioIdEnProceso(request);
        if (usuarioId == null) {
            response.sendRedirect(request.getContextPath() + "/recuperar");
            return;
        }

        String contrasena = request.getParameter("contrasena");
        String confirmarContrasena = request.getParameter("confirmarContrasena");

        if (contrasena == null || contrasena.isEmpty() || !contrasena.equals(confirmarContrasena)) {
            request.setAttribute("error", "Las contraseñas no coinciden.");
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        if (contrasena.length() < LONGITUD_MINIMA) {
            request.setAttribute("error", "La contraseña debe tener al menos " + LONGITUD_MINIMA + " caracteres.");
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        Usuario usuario = usuarioDao.getById(usuarioId);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/recuperar");
            return;
        }

        boolean actualizado = usuarioDao.actualizarContrasenaYLimpiarCodigo(
                usuario.getId(), PasswordUtil.hash(contrasena));

        if (!actualizado) {
            request.setAttribute("error", "No se pudo actualizar la contraseña, intenta de nuevo.");
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        EmailUtil.enviarConfirmacionCambio(usuario.getCorreoElectronico(), usuario.getNombreUsuario());

        // Ya no se necesita el estado temporal de recuperación
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(VerificarCodigoServlet.ATRIBUTO_SESION_USUARIO_ID);
        }

        response.sendRedirect(request.getContextPath() + "/login?contrasenaActualizada=1");
    }

    private Long obtenerUsuarioIdEnProceso(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (Long) session.getAttribute(VerificarCodigoServlet.ATRIBUTO_SESION_USUARIO_ID);
    }
}
