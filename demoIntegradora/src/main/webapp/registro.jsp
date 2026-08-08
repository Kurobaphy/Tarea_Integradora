<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Crear cuenta - Wiki de Videojuegos</title>
</head>
<body>
    <jsp:include page="/header.jsp" />
    <h1>Crear cuenta</h1>

    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/registro">
        <label>Nombre de usuario:
            <input type="text" name="nombreUsuario" value="${nombreUsuario}" required="required">
        </label><br>
        <label>Correo electrónico:
            <input type="email" name="correoElectronico" value="${correoElectronico}" required="required">
        </label><br>
        <label>Contraseña:
            <input type="password" name="contrasena" required="required">
        </label><br>
        <button type="submit">Crear cuenta</button>
    </form>

    <p>¿Ya tienes cuenta? <a href="${pageContext.request.contextPath}/login">Inicia sesión</a></p>
</body>
</html>
