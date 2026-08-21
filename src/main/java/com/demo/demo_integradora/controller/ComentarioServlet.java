package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Comentario;
import com.demo.demo_integradora.model.Dao.ComentarioDao;
import com.demo.demo_integradora.model.Rol;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.utils.SesionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/comentario")
public class ComentarioServlet extends HttpServlet {

    private final ComentarioDao comentarioDao = new ComentarioDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // AuthFilter ya garantiza que hay sesión iniciada antes de llegar aquí.
        Usuario usuario = SesionUtil.obtenerUsuario(request);

        if ("eliminar".equals(request.getParameter("accion"))) {
            eliminar(request, response, usuario);
            return;
        }

        Long articuloId = parseId(request.getParameter("articuloId"));
        String texto = request.getParameter("texto");

        if (articuloId == null || texto == null || texto.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el artículo o el texto del comentario.");
            return;
        }

        Comentario comentario = new Comentario();
        comentario.setArticuloId(articuloId);
        comentario.setAutor(usuario);
        comentario.setTexto(texto.trim());
        comentarioDao.create(comentario);

        response.sendRedirect(request.getContextPath() + "/articulo?id=" + articuloId);
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws IOException {

        Long comentarioId = parseId(request.getParameter("comentarioId"));
        if (comentarioId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el comentario a eliminar.");
            return;
        }

        Comentario comentario = comentarioDao.getById(comentarioId);
        if (comentario == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Comentario no encontrado.");
            return;
        }

        boolean esAutor = comentario.getAutor().getId().equals(usuario.getId());
        boolean esAdministrador = usuario.getRol() == Rol.ADMINISTRADOR;
        if (!esAutor && !esAdministrador) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Solo el autor del comentario o un Administrador pueden eliminarlo.");
            return;
        }

        comentarioDao.delete(comentarioId);
        response.sendRedirect(request.getContextPath() + "/articulo?id=" + comentario.getArticuloId());
    }

    private Long parseId(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        try {
            return Long.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
