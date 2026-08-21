<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nueva contraseña -  Grimorio Gamer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wiki-theme.css">
</head>
<body>
    <jsp:include page="/header.jsp" />
    <main class="ds-container">
    <h1>Establece tu nueva contraseña</h1>

    <div class="card auth-card">
    <%-- Paso 5: las contraseñas no coincidieron (o no cumplen el mínimo) --%>
    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/nueva-contrasena">
        <label>Nueva contraseña:<br>
            <input type="password" name="contrasena" required="required">
        </label><br><br>
        <label>Confirmar nueva contraseña:<br>
            <input type="password" name="confirmarContrasena" required="required">
        </label><br><br>
        <button type="submit">Cambiar contraseña</button>
    </form>
    </div>
    </main>
    <jsp:include page="/footer.jsp" />
</body>
</html>
