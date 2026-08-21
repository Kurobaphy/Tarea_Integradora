package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Articulo;
import com.demo.demo_integradora.model.Dao.ArticuloDao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet("/busqueda/sugerencias")
public class AutocompletadoServlet extends HttpServlet {

    private static final int MINIMO_CARACTERES = 3;
    private static final int MAX_SUGERENCIAS = 8;

    private final ArticuloDao articuloDao = new ArticuloDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String texto = request.getParameter("q");
        response.setContentType("application/json;charset=UTF-8");

        if (texto == null || texto.trim().length() < MINIMO_CARACTERES) {
            response.getWriter().write("[]");
            return;
        }

        List<Articulo> resultados = articuloDao.buscar(texto.trim(), null, null, null, null, "ASC");

        StringBuilder json = new StringBuilder("[");
        int total = Math.min(resultados.size(), MAX_SUGERENCIAS);
        for (int i = 0; i < total; i++) {
            Articulo a = resultados.get(i);
            if (i > 0) json.append(",");
            json.append("{\"id\":").append(a.getId())
                    .append(",\"titulo\":\"").append(escapar(a.getTitulo())).append("\"}");
        }
        json.append("]");

        try (PrintWriter out = response.getWriter()) {
            out.write(json.toString());
        }
    }

    private String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
