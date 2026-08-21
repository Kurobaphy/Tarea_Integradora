package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Dao.UsuarioDao;
import com.demo.demo_integradora.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/verificar-codigo")
public class VerificarCodigoServlet extends HttpServlet {

    private static final String VISTA = "/verificar-codigo.jsp";
    public static final String ATRIBUTO_SESION_USUARIO_ID = "recuperacionUsuarioId";

    private final UsuarioDao usuarioDao = new UsuarioDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String codigo = request.getParameter("codigo");

        Usuario usuario = (codigo == null || codigo.trim().isEmpty())
                ? null
                : usuarioDao.getByCodigoRecuperacion(codigo.trim().toUpperCase());

        boolean expirado = usuario != null
                && usuario.getCodigoExpiracion() != null
                && usuario.getCodigoExpiracion().isBefore(LocalDateTime.now());

        if (usuario == null || expirado) {
            request.setAttribute("error", "Código incorrecto o expirado, intenta de nuevo.");
            request.setAttribute("codigo", codigo);
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }


        HttpSession session = request.getSession(true);
        session.setAttribute(ATRIBUTO_SESION_USUARIO_ID, usuario.getId());

        response.sendRedirect(request.getContextPath() + "/nueva-contrasena");
    }
}
