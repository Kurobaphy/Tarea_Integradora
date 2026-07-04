<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Búsqueda Avanzada de Videojuegos</title>
</head>
<body>
<h2>Motor de Búsqueda Modular</h2>

<form action="BuscarServlet" method="GET">
    <div class="search-box">
        <input type="text" name="query" placeholder="Escribe al menos 3 caracteres para buscar..." style="width: 70%; padding: 8px;">
        <button type="submit" style="padding: 8px;"> Buscar</button>
    </div>

    <br>

    <div class="filtros" style="display: flex; gap: 15px;">
        <label>Año:
            <input type="number" name="anio" placeholder="Ej: 2026">
        </label>

        <label>Consola:
            <select name="consola">
                <option value="">Todas</option>
                <option value="switch">Nintendo Switch</option>
                <option value="ps5">PlayStation 5</option>
                <option value="pc">PC</option>
            </select>
        </label>

        <label>Género:
            <input type="text" name="genero" placeholder="Ej: RPG">
        </label>

        <label>Orden Alfabético:
            <select name="orden">
                <option value="ASC">A - Z (Ascendente)</option>
                <option value="DESC">Z - A (Descendente)</option>
            </select>
        </label>
    </div>
</form>

<hr>
<h3>Resultados de Búsqueda (Simulados)</h3>
<ul>
    <li><a href="ver-articulo.jsp">Zelda: Breath of the Wild</a> - Consola: Nintendo Switch</li>
</ul>
</body>
</html>