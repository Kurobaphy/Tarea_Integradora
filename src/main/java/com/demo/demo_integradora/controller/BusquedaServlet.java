package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Articulo;
import com.demo.demo_integradora.model.Dao.ArticuloDao;
import com.demo.demo_integradora.model.Dao.FichaTecnicaDao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/busqueda")
public class BusquedaServlet extends HttpServlet {

    private static final String VISTA = "/resultados.jsp";

    private final ArticuloDao articuloDao = new ArticuloDao();
    private final FichaTecnicaDao fichaTecnicaDao = new FichaTecnicaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long etiquetaId = parseId(request.getParameter("etiquetaId"));
        List<Articulo> resultados;

        if (etiquetaId != null) {
            resultados = articuloDao.getByEtiquetaId(etiquetaId);
        } else {
            String nombre = request.getParameter("nombre");
            String plataforma = request.getParameter("plataforma");
            String genero = request.getParameter("genero");
            String desarrollador = request.getParameter("desarrollador");
            String orden = request.getParameter("orden");
            Integer anio = parseAnio(request.getParameter("anio"));

            resultados = articuloDao.buscar(nombre, anio, plataforma, genero, desarrollador, orden);
            request.setAttribute("nombre", nombre);
            request.setAttribute("genero", genero);
        }

        request.setAttribute("generosDisponibles", fichaTecnicaDao.obtenerGenerosDistintos());
        request.setAttribute("resultados", resultados);
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    private Integer parseAnio(String valor) {
        if (valor == null || valor.trim().isEmpty()) return null;
        try {
            return Integer.valueOf(valor.trim());
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
