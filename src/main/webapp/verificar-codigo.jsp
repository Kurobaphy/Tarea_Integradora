<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Verificar código -  Grimorio Gamer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wiki-theme.css">
</head>
<body>
    <jsp:include page="/header.jsp" />
    <main class="ds-container">
    <h1>Verificar código</h1>

    <div class="card auth-card">
    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>

    <p>Introduce el código de 6 caracteres que te enviamos por correo electrónico.
       Si diste clic en el enlace del correo, ya viene precargado.</p>

    <form method="post" action="${pageContext.request.contextPath}/verificar-codigo">
        <label>Código:<br>
            <input type="text" name="codigo"
                   value="${not empty codigo ? codigo : param.codigo}"
                   maxlength="10" required="required" autocomplete="off"
                   style="text-transform:uppercase; letter-spacing:4px; font-size:1.3rem; text-align:center;">
        </label><br><br>
        <button type="submit">Verificar código</button>
    </form>

    <p><a href="${pageContext.request.contextPath}/recuperar">¿No recibiste el código? Solicítalo de nuevo</a></p>
    </div>
    </main>
    <jsp:include page="/footer.jsp" />
</body>
</html>
