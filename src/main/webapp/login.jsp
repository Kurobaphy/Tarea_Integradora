<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión -  Grimorio Gamer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wiki-theme.css">
</head>
<body>
    <jsp:include page="/header.jsp" />
    <main class="ds-container">
    <h1>Iniciar sesión</h1>

    <div class="card auth-card">
    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>
    <c:if test="${param.registrado == '1'}">
        <p style="color:green;">Cuenta creada correctamente, ya puedes iniciar sesión.</p>
    </c:if>
    <c:if test="${param.contrasenaActualizada == '1'}">
        <p style="color:green;">Tu contraseña se actualizó correctamente, ya puedes iniciar sesión.</p>
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

    <p><a href="${pageContext.request.contextPath}/recuperar">¿Olvidaste tu contraseña?</a></p>
    <p>¿No tienes cuenta? <a href="${pageContext.request.contextPath}/registro">Regístrate</a></p>
    </div>
    </main>
    <jsp:include page="/footer.jsp" />
</body>
</html>
