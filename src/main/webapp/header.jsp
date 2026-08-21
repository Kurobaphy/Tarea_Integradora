<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="ds-header">

    <a href="${pageContext.request.contextPath}/busqueda" class="ds-logo">
        📓 Grimorio Gamer
    </a>

    <nav class="ds-nav">
        <c:choose>
            <c:when test="${not empty sessionScope.usuarioSesion}">
                <c:if test="${sessionScope.usuarioSesion.rol == 'EDITOR' || sessionScope.usuarioSesion.rol == 'ADMINISTRADOR'}">
                    <a href="${pageContext.request.contextPath}/articulo?accion=nuevo" class="ds-btn ds-btn-nuevo">
                        + Nuevo artículo
                    </a>
                </c:if>
                <span class="ds-saludo">Hola, <c:out value="${sessionScope.usuarioSesion.nombreUsuario}"/></span>
                <a href="${pageContext.request.contextPath}/favorito">Mis favoritos</a>

                <a href="${pageContext.request.contextPath}/logout" class="ds-btn ds-btn-salir">
                    Cerrar sesión
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login">Iniciar sesión</a>
                <a href="${pageContext.request.contextPath}/registro">Crear cuenta</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
