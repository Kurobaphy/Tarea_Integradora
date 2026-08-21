<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Buscar videojuegos -  Grimorio Gamer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wiki-theme.css">
</head>
<body>
    <jsp:include page="/header.jsp" />
    <main class="ds-container">
    <h1>Buscar videojuegos</h1>

    <div class="card">
        <form method="get" action="${pageContext.request.contextPath}/busqueda" style="display:flex;gap:10px;flex-wrap:wrap;align-items:flex-start">
            <input type="text" id="campoNombre" name="nombre" value="${nombre}"
                   placeholder="Nombre del videojuego" autocomplete="off" style="flex:2;min-width:200px">

            <select name="genero" style="flex:1;min-width:160px">
                <option value="">Todos los géneros</option>
                <c:forEach var="g" items="${generosDisponibles}">
                    <option value="${g}" ${g == genero ? 'selected' : ''}><c:out value="${g}"/></option>
                </c:forEach>
            </select>

            <select name="orden" style="flex:1;min-width:120px">
                <option value="ASC">A-Z</option>
                <option value="DESC">Z-A</option>
            </select>
            <button type="submit">Buscar</button>
        </form>

        <ul id="listaSugerencias" class="results"></ul>

        <ul class="results" style="margin-top:4px">
            <c:forEach var="articulo" items="${resultados}">
                <li>
                    <a href="${pageContext.request.contextPath}/articulo?id=${articulo.id}">
                        <c:out value="${articulo.titulo}"/>
                    </a>
                    <c:if test="${not empty articulo.fichaTecnica}">
                        <span class="genre"><c:out value="${articulo.fichaTecnica.genero}"/> · <c:out value="${articulo.fichaTecnica.desarrollador}"/></span>
                    </c:if>
                </li>
            </c:forEach>
            <c:if test="${empty resultados}">
                <li>No se encontraron videojuegos.</li>
            </c:if>
        </ul>
    </div>

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
    </main>
    <jsp:include page="/footer.jsp" />
</body>
</html>
