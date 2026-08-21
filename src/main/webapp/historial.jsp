<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Historial de versiones - Grimorio Gamer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wiki-theme.css">
</head>
<body>
    <jsp:include page="/header.jsp" />
    <main class="ds-container">
    <h1>Historial de versiones</h1>
    <p><a href="${pageContext.request.contextPath}/articulo?id=${articuloId}">Volver al artículo</a></p>

    <div class="card">
        <table>
            <tr>
                <th>Fecha</th>
                <th>Tipo</th>
                <th>Autor</th>
                <c:if test="${not empty sessionScope.usuarioSesion && sessionScope.usuarioSesion.rol == 'ADMINISTRADOR'}">
                    <th>Acción</th>
                </c:if>
            </tr>
            <c:forEach var="v" items="${versiones}">
                <tr>
                    <td>${v.fechaHora}</td>
                    <td>${v.tipoAccion}</td>
                    <td><c:out value="${v.autorEdicion.nombreUsuario}"/></td>
                    <c:if test="${not empty sessionScope.usuarioSesion && sessionScope.usuarioSesion.rol == 'ADMINISTRADOR'}">
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/historial" style="display:inline;">
                                <input type="hidden" name="articuloId" value="${articuloId}">
                                <input type="hidden" name="versionId" value="${v.id}">
                                <button type="submit" onclick="return confirm('¿Revertir el artículo a esta versión?');">
                                    Revertir
                                </button>
                            </form>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
            <c:if test="${empty versiones}">
                <tr><td colspan="4">Este artículo todavía no tiene historial.</td></tr>
            </c:if>
        </table>
    </div>
    </main>
    <jsp:include page="/footer.jsp" />
</body>
</html>
