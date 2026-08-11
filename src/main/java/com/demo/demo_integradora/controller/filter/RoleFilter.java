package com.demo.demo_integradora.controller.filter;

import com.demo.demo_integradora.model.Rol;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.utils.SesionUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class RoleFilter extends HttpFilter {

    private Set<Rol> rolesPermitidos;

    @Override
    public void init(FilterConfig filterConfig) {
        rolesPermitidos = new HashSet<>();
        String parametro = filterConfig.getInitParameter("rolesPermitidos");
        if (parametro != null) {
            for (String nombre : parametro.split(",")) {
                rolesPermitidos.add(Rol.valueOf(nombre.trim()));
            }
        }
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        Usuario usuario = SesionUtil.obtenerUsuario(request);
        if (usuario == null || !rolesPermitidos.contains(usuario.getRol())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para realizar esta acción.");
            return;
        }

        chain.doFilter(request, response);
    }
}
