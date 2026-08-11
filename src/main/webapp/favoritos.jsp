<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mis favoritos - Wiki de Videojuegos</title>
</head>
<body>
    <jsp:include page="/header.jsp" />
    <h1>Mis favoritos</h1>

    <ul>
        <c:forEach var="a" items="${favoritos}">
            <li><a href="${pageContext.request.contextPath}/articulo?id=${a.id}"><c:out value="${a.titulo}"/></a></li>
        </c:forEach>
        <c:if test="${empty favoritos}">
            <li>Aún no tienes videojuegos favoritos.</li>
        </c:if>
    </ul>
</body>
</html>
