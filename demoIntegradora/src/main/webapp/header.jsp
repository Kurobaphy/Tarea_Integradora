<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header style="display:flex; justify-content:space-between; align-items:center;
               padding:10px 16px; border-bottom:1px solid #ccc; font-family:sans-serif;">

    <a href="${pageContext.request.contextPath}/busqueda"
       style="font-weight:bold; text-decoration:none; color:#222;">
        Wiki de Videojuegos
    </a>

    <nav style="display:flex; align-items:center; gap:14px;">
        <c:choose>
            <c:when test="${not empty sessionScope.usuarioSesion}">
                <c:if test="${sessionScope.usuarioSesion.rol == 'EDITOR' || sessionScope.usuarioSesion.rol == 'ADMINISTRADOR'}">
                    <a href="${pageContext.request.contextPath}/articulo?accion=nuevo"
                       style="display:inline-block; padding:6px 14px; background:#2e7d32;
                              color:#fff; text-decoration:none; border-radius:4px;">
                        + Nuevo artículo
                    </a>
                </c:if>
                <span>Hola, <c:out value="${sessionScope.usuarioSesion.nombreUsuario}"/></span>
                <a href="${pageContext.request.contextPath}/favorito">Mis favoritos</a>

                <a href="${pageContext.request.contextPath}/logout"
                   style="display:inline-block; padding:6px 14px; background:#c0392b;
                          color:#fff; text-decoration:none; border-radius:4px;">
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
