<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Buscar videojuegos - Wiki de Videojuegos</title>
</head>
<body>
    <jsp:include page="/header.jsp" />
    <h1>Buscar videojuegos</h1>

    <form method="get" action="${pageContext.request.contextPath}/busqueda">
        <input type="text" id="campoNombre" name="nombre" value="${nombre}"
               placeholder="Nombre del videojuego" autocomplete="off">
        <select name="orden">
            <option value="ASC">A-Z</option>
            <option value="DESC">Z-A</option>
        </select>
        <button type="submit">Buscar</button>
    </form>

    <ul id="listaSugerencias"></ul>

    <hr>

    <ul>
        <c:forEach var="articulo" items="${resultados}">
            <li>
                <a href="${pageContext.request.contextPath}/articulo?id=${articulo.id}">
                    <c:out value="${articulo.titulo}"/>
                </a>
                <c:if test="${not empty articulo.fichaTecnica}">
                    — <c:out value="${articulo.fichaTecnica.genero}"/>, <c:out value="${articulo.fichaTecnica.desarrollador}"/>
                </c:if>
            </li>
        </c:forEach>
        <c:if test="${empty resultados}">
            <li>No se encontraron videojuegos.</li>
        </c:if>
    </ul>

    <script>
        var campo = document.getElementById('campoNombre');
        var lista = document.getElementById('listaSugerencias');
        var contextPath = '${pageContext.request.contextPath}';

        campo.addEventListener('input', function () {
            var texto = campo.value.trim();
            lista.innerHTML = '';
            if (texto.length < 3) return;

            fetch(contextPath + '/busqueda/sugerencias?q=' + encodeURIComponent(texto))
                .then(function (resp) { return resp.json(); })
                .then(function (sugerencias) {
                    sugerencias.forEach(function (s) {
                        var li = document.createElement('li');
                        var a = document.createElement('a');
                        a.href = contextPath + '/articulo?id=' + s.id;
                        a.textContent = s.titulo;
                        li.appendChild(a);
                        lista.appendChild(li);
                    });
                });
        });
    </script>
</body>
</html>
