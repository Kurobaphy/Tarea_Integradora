document.addEventListener("DOMContentLoaded", () => {
    // Buscar todos los elementos con la clase .carga
    const botones = document.querySelectorAll(".carga");

    botones.forEach(boton => {
        boton.addEventListener("click", function(e) {
            // Guardamos el elemento actual en una variable
            const elemento = this;

            // Retraso de 10 milisegundos para permitir que el formulario o evento se procese
            setTimeout(() => {
                // Si es un botón o input nativo
                if (elemento.tagName === "BUTTON" || elemento.tagName === "INPUT") {
                    elemento.disabled = true;

                    // Feedback visual opcional
                    if (elemento.tagName === "BUTTON") elemento.innerHTML = "Procesando...";
                    if (elemento.tagName === "INPUT") elemento.value = "Procesando...";
                }
                // Si usaste un enlace <a> con estilo de botón
                else if (elemento.tagName === "A") {
                    elemento.classList.add("disabled"); // Añade clase CSS para verse deshabilitado
                    elemento.style.pointerEvents = "none"; // Evita que se le vuelva a dar clic
                    elemento.style.opacity = "0.6";
                    elemento.innerText = "Procesando...";
                }
            }, 10);
        });
    });
});