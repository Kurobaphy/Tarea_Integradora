package mx.edu.utez.wikivideojuegos.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    UsuarioDao dao = new UsuarioDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String contra = request.getParameter("contra");

        boolean esValido = dao.login(email, contra);

        if (esValido) {
            HttpSession session = request.getSession(true); // true = crea una sesión si no existe
            session.setAttribute("usuario", email);
            response.sendRedirect("gestion-usuarios.jsp");
        } else {
            request.setAttribute("error", "Usuario o contraseña incorrectos. Inténtalo de nuevo.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
