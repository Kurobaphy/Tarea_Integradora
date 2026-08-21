package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Articulo;
import com.demo.demo_integradora.model.Comentario;
import com.demo.demo_integradora.model.Dao.ArticuloDao;
import com.demo.demo_integradora.model.Dao.ComentarioDao;
import com.demo.demo_integradora.model.Dao.FavoritoDao;
import com.demo.demo_integradora.model.Dao.ValoracionDao;
import com.demo.demo_integradora.model.Etiqueta;
import com.demo.demo_integradora.model.FichaTecnica;
import com.demo.demo_integradora.model.Rol;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.model.Valoracion;
import com.demo.demo_integradora.utils.SesionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/articulo")
public class ArticuloServlet extends HttpServlet {

    private static final String VISTA_EDITOR = "/editor.jsp";
    private static final String VISTA_DETALLE = "/detalle.jsp";

    private final ArticuloDao articuloDao = new ArticuloDao();
    private final ComentarioDao comentarioDao = new ComentarioDao();
    private final ValoracionDao valoracionDao = new ValoracionDao();
    private final FavoritoDao favoritoDao = new FavoritoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("nuevo".equals(accion)) {
            if (!esEditorOAdmin(request, response)) return;
            request.getRequestDispatcher(VISTA_EDITOR).forward(request, response);
            return;
        }

        if ("editar".equals(accion)) {
            if (!esEditorOAdmin(request, response)) return;
            Long id = parseId(request.getParameter("id"));
            Articulo articulo = (id != null) ? articuloDao.getById(id) : null;
            if (articulo == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Artículo no encontrado");
                return;
            }
            request.setAttribute("articulo", articulo);
            request.getRequestDispatcher(VISTA_EDITOR).forward(request, response);
            return;
        }

        // Vista de detalle (pública)
        Long id = parseId(request.getParameter("id"));
        Articulo articulo = (id != null) ? articuloDao.getById(id) : null;
        if (articulo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Artículo no encontrado");
            return;
        }

        List<Comentario> comentarios = comentarioDao.getByArticuloId(id);
        request.setAttribute("articulo", articulo);
        request.setAttribute("comentarios", comentarios);

        Usuario usuario = SesionUtil.obtenerUsuario(request);
        if (usuario != null) {
            Valoracion propia = valoracionDao.getByUsuarioYArticulo(usuario.getId(), id);
            request.setAttribute("miValoracion", propia != null ? propia.getPuntuacion() : 0);
            request.setAttribute("esFavorito", favoritoDao.getByUsuarioYArticulo(usuario.getId(), id) != null);
        }

        request.getRequestDispatcher(VISTA_DETALLE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // RoleFilterArticulo (web.xml) ya garantiza EDITOR/ADMINISTRADOR para todo POST aquí.
        Usuario usuario = SesionUtil.obtenerUsuario(request);

        if ("eliminar".equals(request.getParameter("accion"))) {
            eliminar(request, response, usuario);
            return;
        }

        Long id = parseId(request.getParameter("id"));
        Articulo original = (id != null) ? articuloDao.getById(id) : null;

        Articulo articulo = new Articulo();
        articulo.setTitulo(request.getParameter("titulo"));
        articulo.setCuerpoTexto(request.getParameter("cuerpoTexto"));

        FichaTecnica ficha = new FichaTecnica();
        ficha.setDesarrollador(limpiar(request.getParameter("desarrollador")));
        ficha.setGenero(limpiar(request.getParameter("genero")));
        ficha.setPlataformas(dividirLista(request.getParameter("plataformas")));

        String fechaTexto = request.getParameter("fechaLanzamiento");
        if (fechaTexto != null && !fechaTexto.trim().isEmpty()) {
            ficha.setFechaLanzamiento(LocalDate.parse(fechaTexto.trim()));
        }

        List<Etiqueta> etiquetas = new ArrayList<>();
        for (String nombre : dividirLista(request.getParameter("etiquetas"))) {
            etiquetas.add(new Etiqueta(nombre));
        }
        articulo.setEtiquetas(etiquetas);
        articulo.setFichaTecnica(ficha);

        boolean guardado;
        if (original == null) {
            articulo.setAutor(usuario);
            guardado = articuloDao.create(articulo);
        } else {
            articulo.setId(original.getId());
            articulo.setAutor(original.getAutor());
            ficha.setId(original.getFichaTecnica().getId());
            guardado = articuloDao.update(articulo, usuario);
        }

        if (!guardado) {
            request.setAttribute("error", "No se pudo guardar el artículo, intenta de nuevo.");
            request.setAttribute("articulo", articulo);
            request.getRequestDispatcher(VISTA_EDITOR).forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/articulo?id=" + articulo.getId());
    }
    private void eliminar(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws IOException {

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo un Administrador puede eliminar artículos.");
            return;
        }

        Long id = parseId(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el artículo a eliminar.");
            return;
        }

        boolean eliminado = articuloDao.delete(id);

        if (!eliminado) {
            response.sendRedirect(request.getContextPath() + "/articulo?id=" + id + "&errorEliminar=1");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/busqueda");
    }

    private boolean esEditorOAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = SesionUtil.obtenerUsuario(request);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        if (usuario.getRol() != Rol.EDITOR && usuario.getRol() != Rol.ADMINISTRADOR) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Solo Editor o Administrador pueden crear/editar artículos.");
            return false;
        }
        return true;
    }

    private String limpiar(String valor) {
        return valor != null ? valor.trim() : null;
    }

    private List<String> dividirLista(String textoSeparadoPorComas) {
        List<String> resultado = new ArrayList<>();
        if (textoSeparadoPorComas == null || textoSeparadoPorComas.trim().isEmpty()) return resultado;
        for (String parte : textoSeparadoPorComas.split(",")) {
            String limpio = parte.trim();
            if (!limpio.isEmpty()) resultado.add(limpio);
        }
        return resultado;
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
