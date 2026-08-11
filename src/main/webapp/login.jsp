<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión - Wiki de Videojuegos</title>
</head>
<body>
    <jsp:include page="/header.jsp" />
    <h1>Iniciar sesión</h1>

    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>
    <c:if test="${param.registrado == '1'}">
        <p style="color:green;">Cuenta creada correctamente, ya puedes iniciar sesión.</p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <label>Correo electrónico:
            <input type="email" name="correoElectronico" required="required">
        </label><br>
        <label>Contraseña:
            <input type="password" name="contrasena" required="required">
        </label><br>
        <button type="submit">Entrar</button>
    </form>

    <p>¿No tienes cuenta? <a href="${pageContext.request.contextPath}/registro">Regístrate</a></p>
</body>
</html>
