package com.demo.demo_integradora.controller;

import com.demo.demo_integradora.model.Dao.UsuarioDao;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.utils.CodigoRecuperacionUtil;
import com.demo.demo_integradora.utils.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/recuperar")
public class RecuperarServlet extends HttpServlet {

    private static final String VISTA = "/recuperar.jsp";
    private static final int MINUTOS_VALIDEZ_CODIGO = 15;
    private static final String MENSAJE_GENERICO =
            "Si el email o nombre de usuario se encuentra registrado, te llegará un correo electrónico con instrucciones.";

    private final UsuarioDao usuarioDao = new UsuarioDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String valor = request.getParameter("emailOUsuario");

        if (valor != null && !valor.trim().isEmpty()) {
            Usuario usuario = usuarioDao.getByCorreoOUsuario(valor.trim());

            if (usuario != null) {
                String codigo = CodigoRecuperacionUtil.generar();
                LocalDateTime expiracion = LocalDateTime.now().plusMinutes(MINUTOS_VALIDEZ_CODIGO);

                boolean guardado = usuarioDao.guardarCodigoRecuperacion(usuario.getId(), codigo, expiracion);

                if (guardado) {
                    // Paso 3.3: enviar el correo con el código y el enlace de verificación
                    String enlace = construirEnlaceVerificacion(request, codigo);
                    EmailUtil.enviarCodigoRecuperacion(
                            usuario.getCorreoElectronico(),
                            usuario.getNombreUsuario(),
                            codigo,
                            enlace
                    );
                }
            }
        }


        request.setAttribute("mensaje", MENSAJE_GENERICO);
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    private String construirEnlaceVerificacion(HttpServletRequest request, String codigo) {
        int puerto = request.getServerPort();
        String base = request.getScheme() + "://" + request.getServerName()
                + (puerto == 80 || puerto == 443 ? "" : ":" + puerto)
                + request.getContextPath();
        return base + "/verificar-codigo?codigo=" + codigo;
    }
}
