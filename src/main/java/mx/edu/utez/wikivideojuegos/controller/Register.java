package mx.edu.utez.wikivideojuegos.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "RegisterServlet", value = "/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String apellidos = request.getParameter("apellidos");
        String email1 = request.getParameter("email1");
        String email2 = request.getParameter("email2");
        String contra1 = request.getParameter("contra1");
        String contra2 = request.getParameter("contra2");
        String fotoPerfil = "default.png";

        if (nombre == null || nombre.isBlank() || email1 == null || email1.isBlank() || email2 == null || email2.isBlank() || contra1 == null || contra1.isBlank() || contra2 == null || contra2.isBlank()) {
            request.setAttribute("error", "Por favor, completa todos los campos obligatorios.");
            request.getRequestDispatcher("registro.jsp").forward(request, response);
            return;
        }
        if (!email1.equals(email2)) {
            request.setAttribute("error", "Los correos no coinciden.");
            request.getRequestDispatcher("registro.jsp").forward(request, response);
            return;
        }
        if (!contra1.equals(contra2)) {
            request.setAttribute("error", "Las contraseñas no coinciden.");
            request.getRequestDispatcher("registro.jsp").forward(request, response);
            return;
        }

        DuenoDao dao = new DuenoDao();

        // Generar un nuevo ID simulando un AUTO_INCREMENT
        int nuevoId = 1;
        List<Dueno> listaActual = dao.getAll();
        for (Dueno d : listaActual) {
            if (d.getId() >= nuevoId) {
                nuevoId = d.getId() + 1;
            }
        }

        Dueno nuevoDueno = new Dueno();
        nuevoDueno.setId(nuevoId);
        nuevoDueno.setNombre(nombre);
        nuevoDueno.setApellidos(apellidos);
        nuevoDueno.setCorreo(email1);
        nuevoDueno.setContrasena(contra1);
        nuevoDueno.setFoto_perfil(fotoPerfil);

        boolean creado = dao.create(nuevoUsuario);

        if (creado) {
            request.setAttribute("mensaje", "¡Cuenta creada con éxito! Ahora puedes iniciar sesión.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Hubo un problema interno al crear tu cuenta. Intenta de nuevo.");
            request.getRequestDispatcher("registro.jsp").forward(request, response);
        }
    }
}
