<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión - Wiki de Videojuegos</title>
    <link rel="stylesheet" href="css/estilos.css"> </head>
<body>
<div class="login-container">
    <h2>Iniciar Sesión en la Wiki</h2>

    <% if(request.getParameter("error") != null) { %>
    <div class="alert-danger" style="color: red; font-weight: bold;">
        Credenciales erróneas o cuenta bloqueada temporalmente (Máx 3 intentos).
    </div>
    <% } %>

    <form action="LoginServlet" method="POST">
        <div class="form-group">
            <label for="correo">Correo Electrónico *</label>
            <input type="email" id="correo" name="correo" required placeholder="ejemplo@correo.com">
        </div>

        <div class="form-group">
            <label for="password">Contraseña *</label>
            <input type="password" id="password" name="password" required>
        </div>

        <button type="submit">Ingresar</button>
    </form>
    <p>¿No tienes cuenta? <a href="registro.jsp">Regístrate aquí</a></p>
</div>
</body>
</html>