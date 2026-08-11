package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Dao.ValoracionDao;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.model.Valoracion;
import com.demo.demo_integradora.utils.SesionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Requerimiento 4.2: calificar con estrellas (1 a 5). Una sola valoración
 * por usuario y artículo: ValoracionDao.guardarOActualizar() la reemplaza
 * si ya existía, y el promedio de la ficha técnica se recalcula solo.
 */
@WebServlet("/valoracion")
public class ValoracionServlet extends HttpServlet {

    private final ValoracionDao valoracionDao = new ValoracionDao();

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
        Integer puntuacion = parsePuntuacion(request.getParameter("puntuacion"));

        if (articuloId == null || puntuacion == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el artículo o la puntuación (1-5).");
            return;
        }

        Valoracion valoracion = new Valoracion(articuloId, usuario.getId(), puntuacion);
        valoracionDao.guardarOActualizar(valoracion);

        response.sendRedirect(request.getContextPath() + "/articulo?id=" + articuloId);
    }

    /**
     * Quita la valoración del usuario logueado para un artículo. No recibe
     * un id de valoración del formulario a propósito: la busca del lado del
     * servidor con (usuario logueado + articuloId), así nadie puede mandar
     * el id de la valoración de otra persona y borrarla por error o a propósito.
     */
    private void eliminar(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws IOException {

        Long articuloId = parseId(request.getParameter("articuloId"));
        if (articuloId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el artículo.");
            return;
        }

        Valoracion propia = valoracionDao.getByUsuarioYArticulo(usuario.getId(), articuloId);
        if (propia != null) {
            valoracionDao.delete(propia.getId());
        }

        response.sendRedirect(request.getContextPath() + "/articulo?id=" + articuloId);
    }

    private Integer parsePuntuacion(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        try {
            int n = Integer.parseInt(valor.trim());
            return (n >= 1 && n <= 5) ? n : null;
        } catch (NumberFormatException e) {
            return null;
        }
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
