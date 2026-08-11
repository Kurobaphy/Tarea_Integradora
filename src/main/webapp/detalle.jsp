<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${articulo.titulo}"/> - Wiki de Videojuegos</title>
</head>
<body>
    <jsp:include page="/header.jsp" />
    <h1><c:out value="${articulo.titulo}"/></h1>

    <c:if test="${param.errorEliminar == '1'}">
        <p style="color:red;">No se pudo eliminar el artículo, intenta de nuevo.</p>
    </c:if>

    <c:if test="${not empty sessionScope.usuarioSesion &&
                  (sessionScope.usuarioSesion.rol == 'EDITOR' || sessionScope.usuarioSesion.rol == 'ADMINISTRADOR')}">
        <p>
            <a href="${pageContext.request.contextPath}/articulo?accion=editar&id=${articulo.id}">Editar</a> |
            <a href="${pageContext.request.contextPath}/historial?articuloId=${articulo.id}">Ver historial</a>
            <c:if test="${sessionScope.usuarioSesion.rol == 'ADMINISTRADOR'}">
                |
                <form method="post" action="${pageContext.request.contextPath}/articulo" style="display:inline;">
                    <input type="hidden" name="accion" value="eliminar">
                    <input type="hidden" name="id" value="${articulo.id}">
                    <button type="submit"
                            onclick="return confirm('¿Eliminar este artículo permanentemente? También se pierde su historial de versiones.');">
                        Eliminar artículo
                    </button>
                </form>
            </c:if>
        </p>
    </c:if>

    <section>
        <h2>Ficha técnica</h2>
        <ul>
            <li>Desarrollador: <c:out value="${articulo.fichaTecnica.desarrollador}"/></li>
            <li>Fecha de lanzamiento: ${articulo.fichaTecnica.fechaLanzamiento}</li>
            <li>Género: <c:out value="${articulo.fichaTecnica.genero}"/></li>
            <li>Plataformas:
                <c:forEach var="p" items="${articulo.fichaTecnica.plataformas}" varStatus="st">
                    <c:out value="${p}"/><c:if test="${!st.last}">, </c:if>
                </c:forEach>
            </li>
            <li>Puntuación promedio: ${articulo.fichaTecnica.puntuacionPromedio}</li>
        </ul>
    </section>

    <section>
        <h2>Descripción</h2>
        <%-- cuerpoTexto viene del editor de texto enriquecido (Requerimiento 2.1) y
             se muestra sin escapar a propósito, para que el HTML/formato se renderice.
             En un despliegue real esto debería pasar por un sanitizador de HTML
             (allow-list de tags) para evitar XSS almacenado si un Editor malicioso
             mete <script> en el cuerpo; no está implementado aquí todavía. --%>
        <div>${articulo.cuerpoTexto}</div>
    </section>

    <section>
        <h2>Etiquetas</h2>
        <c:forEach var="e" items="${articulo.etiquetas}">
            <a href="${pageContext.request.contextPath}/busqueda?etiquetaId=${e.id}"><c:out value="${e.nombre}"/></a>&nbsp;
        </c:forEach>
    </section>

    <c:if test="${not empty sessionScope.usuarioSesion}">
        <section>
            <h2>Tu valoración</h2>
            <form method="post" action="${pageContext.request.contextPath}/valoracion">
                <input type="hidden" name="articuloId" value="${articulo.id}">
                <select name="puntuacion">
                    <c:forEach var="n" begin="1" end="5">
                        <option value="${n}" ${miValoracion == n ? 'selected' : ''}>${n} estrella(s)</option>
                    </c:forEach>
                </select>
                <button type="submit">Calificar</button>
            </form>

            <c:if test="${miValoracion > 0}">
                <form method="post" action="${pageContext.request.contextPath}/valoracion" style="display:inline;">
                    <input type="hidden" name="accion" value="eliminar">
                    <input type="hidden" name="articuloId" value="${articulo.id}">
                    <button type="submit">Quitar mi valoración</button>
                </form>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/favorito">
                <input type="hidden" name="articuloId" value="${articulo.id}">
                <button type="submit">
                    <c:choose>
                        <c:when test="${esFavorito}">Quitar de favoritos</c:when>
                        <c:otherwise>Añadir a favoritos</c:otherwise>
                    </c:choose>
                </button>
            </form>
        </section>
    </c:if>

    <section>
        <h2>Comentarios</h2>
        <c:forEach var="c" items="${comentarios}">
            <p><strong><c:out value="${c.autor.nombreUsuario}"/></strong> (${c.fechaHora}):
                <c:out value="${c.texto}"/>
                <c:if test="${not empty sessionScope.usuarioSesion &&
                              (sessionScope.usuarioSesion.id == c.autor.id || sessionScope.usuarioSesion.rol == 'ADMINISTRADOR')}">
                    <form method="post" action="${pageContext.request.contextPath}/comentario" style="display:inline;">
                        <input type="hidden" name="accion" value="eliminar">
                        <input type="hidden" name="comentarioId" value="${c.id}">
                        <input type="hidden" name="articuloId" value="${articulo.id}">
                        <button type="submit" onclick="return confirm('¿Eliminar este comentario?');">Eliminar</button>
                    </form>
                </c:if>
            </p>
        </c:forEach>

        <c:if test="${not empty sessionScope.usuarioSesion}">
            <form method="post" action="${pageContext.request.contextPath}/comentario">
                <input type="hidden" name="articuloId" value="${articulo.id}">
                <textarea name="texto" rows="3" cols="50" required="required"></textarea><br>
                <button type="submit">Comentar</button>
            </form>
        </c:if>
        <c:if test="${empty sessionScope.usuarioSesion}">
            <p><a href="${pageContext.request.contextPath}/login">Inicia sesión</a> para comentar.</p>
        </c:if>
    </section>
</body>
</html>
