package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Dao.ArticuloDao;
import com.demo.demo_integradora.model.Dao.VersionArticuloDao;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.model.VersionArticulo;
import com.demo.demo_integradora.utils.SesionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;


@WebServlet("/historial")
public class HistorialServlet extends HttpServlet {

    private static final String VISTA = "/historial.jsp";

    private final VersionArticuloDao versionArticuloDao = new VersionArticuloDao();
    private final ArticuloDao articuloDao = new ArticuloDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long articuloId = parseId(request.getParameter("articuloId"));
        if (articuloId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta articuloId");
            return;
        }

        List<VersionArticulo> versiones = versionArticuloDao.getByArticuloId(articuloId);
        request.setAttribute("articuloId", articuloId);
        request.setAttribute("versiones", versiones);
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = SesionUtil.obtenerUsuario(request);

        Long articuloId = parseId(request.getParameter("articuloId"));
        Long versionId = parseId(request.getParameter("versionId"));
        if (articuloId == null || versionId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan datos");
            return;
        }

        VersionArticulo version = versionArticuloDao.getById(versionId);
        if (version == null || !version.getArticuloId().equals(articuloId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Versión no encontrada");
            return;
        }

        articuloDao.revertirAVersion(articuloId, version, usuario);

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
