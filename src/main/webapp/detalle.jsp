<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${articulo.titulo}"/> - Grimorio Gamer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wiki-theme.css">
</head>
<body>
    <jsp:include page="/header.jsp" />
    <main class="ds-container">

    <c:if test="${param.errorEliminar == '1'}">
        <p style="color:red;">No se pudo eliminar el artículo, intenta de nuevo.</p>
    </c:if>

    <div class="grid-3">
        <section class="hero-card">
            <div>
                <div class="crumb">
                    <c:out value="${articulo.fichaTecnica.genero}"/> · <c:out value="${articulo.fichaTecnica.desarrollador}"/>
                </div>
                <h1><c:out value="${articulo.titulo}"/></h1>

                <c:if test="${not empty sessionScope.usuarioSesion &&
                              (sessionScope.usuarioSesion.rol == 'EDITOR' || sessionScope.usuarioSesion.rol == 'ADMINISTRADOR')}">
                    <div class="actions">
                        <a href="${pageContext.request.contextPath}/articulo?accion=editar&id=${articulo.id}">Editar</a>
                        <a href="${pageContext.request.contextPath}/historial?articuloId=${articulo.id}">Ver historial</a>
                        <c:if test="${sessionScope.usuarioSesion.rol == 'ADMINISTRADOR'}">
                            <form method="post" action="${pageContext.request.contextPath}/articulo" style="display:inline;">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${articulo.id}">
                                <button type="submit"
                                        onclick="return confirm('¿Eliminar este artículo permanentemente? También se pierde su historial de versiones.');">
                                    Eliminar artículo
                                </button>
                            </form>
                        </c:if>
                    </div>
                </c:if>
            </div>
            <div class="score-badge"><b>${articulo.fichaTecnica.puntuacionPromedio}</b><span>promedio</span></div>
        </section>

        <section class="stat-card">
            <div class="k">Lanzamiento</div>
            <div class="v">${articulo.fichaTecnica.fechaLanzamiento}</div>
        </section>

        <section class="stat-card">
            <div class="k">Plataformas</div>
            <div class="v" style="font-size:.95rem">
                <c:forEach var="p" items="${articulo.fichaTecnica.plataformas}" varStatus="st">
                    <c:out value="${p}"/><c:if test="${!st.last}">, </c:if>
                </c:forEach>
            </div>
        </section>
    </div>

    <section>
        <h2 class="section-title">Descripción</h2>
        <div class="desc">${articulo.cuerpoTexto}</div>
    </section>

    <section>
        <h2 class="section-title">Etiquetas</h2>
        <div class="tags">
            <c:forEach var="e" items="${articulo.etiquetas}">
                <a href="${pageContext.request.contextPath}/busqueda?etiquetaId=${e.id}" class="tag-chip"><c:out value="${e.nombre}"/></a>
            </c:forEach>
        </div>
    </section>

    <div class="grid-2">
        <c:if test="${not empty sessionScope.usuarioSesion}">
            <section>
                <h2 class="section-title">Tu valoración</h2>
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
            <h2 class="section-title">Comentarios</h2>
            <c:forEach var="c" items="${comentarios}">
                <div class="comment">
                    <span class="av"></span>
                    <div style="flex:1">
                        <span class="who"><c:out value="${c.autor.nombreUsuario}"/></span><span class="when">${c.fechaHora}</span>
                        <p><c:out value="${c.texto}"/></p>
                        <c:if test="${not empty sessionScope.usuarioSesion &&
                                      (sessionScope.usuarioSesion.id == c.autor.id || sessionScope.usuarioSesion.rol == 'ADMINISTRADOR')}">
                            <form method="post" action="${pageContext.request.contextPath}/comentario" style="display:inline;">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="comentarioId" value="${c.id}">
                                <input type="hidden" name="articuloId" value="${articulo.id}">
                                <button type="submit" onclick="return confirm('¿Eliminar este comentario?');">Eliminar</button>
                            </form>
                        </c:if>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${not empty sessionScope.usuarioSesion}">
                <form method="post" action="${pageContext.request.contextPath}/comentario" style="margin-top:10px">
                    <input type="hidden" name="articuloId" value="${articulo.id}">
                    <textarea name="texto" rows="2" required="required" placeholder="Escribe tu comentario..."></textarea><br>
                    <button type="submit">Comentar</button>
                </form>
            </c:if>
            <c:if test="${empty sessionScope.usuarioSesion}">
                <p><a href="${pageContext.request.contextPath}/login">Inicia sesión</a> para comentar.</p>
            </c:if>
        </section>
    </div>
    </main>
    <jsp:include page="/footer.jsp" />
</body>
</html>
