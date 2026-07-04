<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nombre del Videojuego - Wiki</title>
    <style>
        .layout { display: flex; gap: 20px; }
        .contenido { flex: 3; }
        .ficha-tecnica { flex: 1; border: 1px solid #ccc; padding: 15px; background: #f9f9f9; }
    </style>
</head>
<body>
<header>
    <h1>Zelda: Breath of the Wild</h1>
    <p>Etiquetas: <span class="tag">Aventura</span>, <span class="tag">Mundo Abierto</span></p>
</header>

<div class="layout">
    <main class="contenido">
        <article>
            <p>Aquí va el cuerpo de texto enriquecido que los editores redactan sobre el juego...</p>
            <div class="video-container">
                <iframe width="560" height="315" src="https://www.youtube.com/embed/placeholder" frameborder="0" allowfullscreen></iframe>
            </div>
        </article>

        <hr>

        <section class="comentarios-seccion">
            <h3>Comunidad y Discusión</h3>

            <div class="valoracion">
                <p>Calificar este juego: </p>
                <form action="ValorarServlet" method="POST">
                    <select name="estrellas">
                        <option value="5"> (5)</option>
                        <option value="4"> (4)</option>
                        <option value="3"> (3)</option>
                        <option value="2"> (2)</option>
                        <option value="1"> (1)</option>
                    </select>
                    <button type="submit">Calificar</button>
                </form>
            </div>

            <form action="ComentarioServlet" method="POST">
                <textarea name="comentario" rows="3" placeholder="Escribe un comentario para debatir..." required></textarea><br>
                <button type="submit">Publicar Comentario</button>
            </form>
        </section>
    </main>

    <aside class="ficha-tecnica">
        <h3>Ficha Técnica</h3>
        <table>
            <tr><td><strong>Desarrollador:</strong></td><td>Nintendo</td></tr>
            <tr><td><strong>Lanzamiento:</strong></td><td>03/03/2017</td></tr>
            <tr><td><strong>Plataformas:</strong></td><td>Nintendo Switch, Wii U</td></tr>
            <tr><td><strong>Género:</strong></td><td>Acción-aventura</td></tr>
            <tr><td><strong>Puntuación Base:</strong></td><td>4.9 / 5</td></tr>
        </table>
        <br>
        <button onclick="alert('Añadido a tus Favoritos')"> Añadir a Favoritos / Seguir</button>
    </aside>
</div>
</body>
</html>