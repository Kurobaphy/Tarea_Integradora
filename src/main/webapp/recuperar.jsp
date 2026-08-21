<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Recuperar contraseña -  Grimorio Gamer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wiki-theme.css">
</head>
<body>
    <jsp:include page="/header.jsp" />
    <main class="ds-container">
    <h1>Recuperar contraseña</h1>

    <div class="card auth-card">
    <c:choose>

        <c:when test="${not empty mensaje}">
            <p style="color:green;">${mensaje}</p>
            <p><a href="${pageContext.request.contextPath}/verificar-codigo">Ya tengo mi código</a></p>
        </c:when>

        <c:otherwise>
            <p>Escribe tu correo electrónico o tu nombre de usuario y te enviaremos
               instrucciones para recuperar el acceso a tu cuenta.</p>

            <form method="post" action="${pageContext.request.contextPath}/recuperar">
                <label>Correo electrónico o nombre de usuario:<br>
                    <input type="text" name="emailOUsuario" required="required" autocomplete="off">
                </label><br><br>
                <button type="submit">Enviar instrucciones</button>
            </form>
        </c:otherwise>
    </c:choose>

    <p><a href="${pageContext.request.contextPath}/login">Volver a iniciar sesión</a></p>
    </div>
    </main>
    <jsp:include page="/footer.jsp" />
</body>
</html>
