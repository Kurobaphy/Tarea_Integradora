package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Articulo;
import com.demo.demo_integradora.model.Dao.FavoritoDao;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.utils.SesionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;


@WebServlet("/favorito")
public class FavoritoServlet extends HttpServlet {

    private static final String VISTA = "/favoritos.jsp";

    private final FavoritoDao favoritoDao = new FavoritoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = SesionUtil.obtenerUsuario(request);

        List<Articulo> favoritos = favoritoDao.getArticulosFavoritos(usuario.getId());
        request.setAttribute("favoritos", favoritos);
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = SesionUtil.obtenerUsuario(request);

        Long articuloId = parseId(request.getParameter("articuloId"));
        if (articuloId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el artículo.");
            return;
        }

        favoritoDao.alternar(usuario.getId(), articuloId);

        response.sendRedirect(request.getContextPath() + "/articulo?id=" + articuloId);
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
