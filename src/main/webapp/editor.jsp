<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><c:choose><c:when test="${empty articulo}">Nuevo artículo</c:when><c:otherwise>Editar artículo</c:otherwise></c:choose> - Grimorio Gamer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wiki-theme.css">
</head>
<body>
    <jsp:include page="/header.jsp" />
    <main class="ds-container">
    <h1><c:choose><c:when test="${empty articulo}">Nuevo artículo</c:when><c:otherwise>Editar artículo</c:otherwise></c:choose></h1>

    <c:if test="${not empty error}">
        <p style="color:red;"><c:out value="${error}"/></p>
    </c:if>

    <div class="card">
    <form method="post" action="${pageContext.request.contextPath}/articulo">
        <c:if test="${not empty articulo}">
            <input type="hidden" name="id" value="${articulo.id}">
        </c:if>

        <label>Título:
            <input type="text" name="titulo" value="${articulo.titulo}" required="required">
        </label><br><br>

        <label>Cuerpo del artículo (admite HTML del editor de texto enriquecido):<br>
            <textarea name="cuerpoTexto" rows="10" cols="60"><c:out value="${articulo.cuerpoTexto}"/></textarea>
        </label><br><br>

        <h2>Ficha técnica</h2>
        <label>Desarrollador:
            <input type="text" name="desarrollador" value="${articulo.fichaTecnica.desarrollador}">
        </label><br>
        <label>Fecha de lanzamiento:
            <input type="date" name="fechaLanzamiento" value="${articulo.fichaTecnica.fechaLanzamiento}">
        </label><br>
        <label>Género:
            <input type="text" name="genero" value="${articulo.fichaTecnica.genero}">
        </label><br>
        <label>Plataformas (separadas por coma):
            <input type="text" name="plataformas" placeholder="ej: PC, PS5, Xbox Series X">
        </label>
        <c:if test="${not empty articulo.fichaTecnica.plataformas}">
            <br><small>Actuales:
            <c:forEach var="p" items="${articulo.fichaTecnica.plataformas}" varStatus="st">${p}<c:if test="${!st.last}">, </c:if></c:forEach>
            — vuelve a escribirlas si quieres conservarlas.</small>
        </c:if>
        <br><br>

        <label>Etiquetas (separadas por coma):
            <input type="text" name="etiquetas" placeholder="ej: RPG, mundo abierto">
        </label>
        <c:if test="${not empty articulo.etiquetas}">
            <br><small>Actuales:
            <c:forEach var="e" items="${articulo.etiquetas}" varStatus="st">${e.nombre}<c:if test="${!st.last}">, </c:if></c:forEach>
            — vuelve a escribirlas si quieres conservarlas.</small>
        </c:if>
        <br><br>

        <button type="submit">Guardar</button>
    </form>
    </div>
    </main>
    <jsp:include page="/footer.jsp" />
</body>
</html>
