
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editor de Artículos - Wiki</title>
</head>
<body>
<h2>Formulario de Edición de Videojuego</h2>

<form action="GuardarArticuloServlet" method="POST" enctype="multipart/form-data">
    <fieldset>
        <legend>Contenido del Artículo</legend>
        <label>Título del Artículo *</label><br>
        <input type="text" name="titulo" style="width: 100%;" required><br><br>

        <label>Cuerpo de Texto (Editor Enriquecido Mock) *</label><br>
        <textarea name="cuerpo" rows="10" style="width: 100%;" required></textarea><br><br>

        <label>Subir Archivos Multimedia (JPG, PNG):</label>
        <input type="file" name="multimedia" accept=".jpg,.png" multiple><br><br>

        <label>Enlace de Video para Embeber:</label>
        <input type="url" name="video_url" placeholder="https://youtube.com/..."><br>
    </fieldset>

    <br>

    <fieldset>
        <legend>Datos Obligatorios de la Ficha Técnica</legend>
        <label>Desarrollador *</label><br>
        <input type="text" name="desarrollador" required><br><br>

        <label>Fecha de Lanzamiento *</label><br>
        <input type="date" name="fecha_lanzamiento" required><br><br>

        <label>Plataformas / Consolas *</label><br>
        <input type="text" name="plataformas" placeholder="Ej: PS5, PC, Xbox" required><br><br>

        <label>Género *</label><br>
        <input type="text" name="genero" required><br><br>

        <label>Puntuación Inicial:</label><br>
        <input type="number" name="puntuacion_base" min="1" max="5" step="0.1"><br>
    </fieldset>

    <br>
    <button type="submit" style="padding: 10px 20px; background-color: green; color: white;">Publicar / Guardar Cambios</button>
</form>
</body>
</html>