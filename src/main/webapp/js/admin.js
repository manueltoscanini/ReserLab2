function mostrarModalCrearReserva() {
    document.getElementById('modalCrearReserva').style.display = 'flex';
    // Establecer fecha mínima como hoy
    const hoy = new Date().toISOString().split('T')[0];
    document.getElementById('fecha').setAttribute('min', hoy);
}



function cerrarModalCrearReserva() {
    document.getElementById('modalCrearReserva').style.display = 'none';
    document.getElementById('formCrearReserva').reset();
}

// Cerrar modal al hacer clic fuera de él
window.onclick = function(event) {
    const modal = document.getElementById('modalCrearReserva');
    if (event.target === modal) {
        cerrarModalCrearReserva();
    }
}

// Validar que hora fin sea mayor que hora inicio
document.getElementById('formCrearReserva').addEventListener('submit', function(e) {
    const horaInicio = document.getElementById('horaInicio').value;
    const horaFin = document.getElementById('horaFin').value;

    if (horaInicio && horaFin && horaInicio >= horaFin) {
        e.preventDefault();
        alert('La hora de fin debe ser posterior a la hora de inicio');
    }
});

// --- Subir foto de perfil ---
document.addEventListener('DOMContentLoaded', () => {
    const btnCambiarFoto = document.getElementById('btnCambiarFoto');
    const inputFoto = document.getElementById('inputFoto');
    const fotoPerfil = document.getElementById('fotoPerfil');
    const iconoPerfil = document.getElementById('iconoPerfil');

    if (btnCambiarFoto && inputFoto) {
        btnCambiarFoto.addEventListener('click', (e) => {
            e.preventDefault();
            inputFoto.click();
        });

        inputFoto.addEventListener('change', async (e) => {
            const archivo = e.target.files[0];
            if (!archivo) return;

            // Validar que sea una imagen
            if (!archivo.type.startsWith('image/')) {
                alert('Por favor selecciona un archivo de imagen válido');
                return;
            }

            // Validar tamaño (máximo 10MB)
            if (archivo.size > 10 * 1024 * 1024) {
                alert('La imagen no debe superar los 10MB');
                return;
            }

            // Mostrar indicador de carga
            btnCambiarFoto.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i>';
            btnCambiarFoto.disabled = true;

            try {
                const formData = new FormData();
                formData.append('foto', archivo);

                const response = await fetch('SubirFotoServlet', {
                    method: 'POST',
                    body: formData
                });

                const resultado = await response.json();

                if (resultado.success) {
                    // Actualizar la imagen en la interfaz
                    if (fotoPerfil) {
                        fotoPerfil.src = resultado.fotoUrl;
                    } else if (iconoPerfil) {
                        // Reemplazar el icono con la imagen
                        const container = document.querySelector('.foto-perfil-container');
                        iconoPerfil.style.display = 'none';
                        const nuevaImg = document.createElement('img');
                        nuevaImg.src = resultado.fotoUrl;
                        nuevaImg.alt = 'Foto de perfil';
                        nuevaImg.className = 'fotoPerfil';
                        nuevaImg.id = 'fotoPerfil';
                        container.insertBefore(nuevaImg, container.firstChild);
                    }
                    alert('Foto actualizada correctamente');
                } else {
                    alert('Error al subir la foto: ' + resultado.message);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Error al subir la foto. Por favor intenta de nuevo.');
            } finally {
                // Restaurar el botón
                btnCambiarFoto.innerHTML = '<i class="fa-solid fa-camera"></i>';
                btnCambiarFoto.disabled = false;
                // Limpiar el input para permitir subir la misma imagen nuevamente
                inputFoto.value = '';
            }
        });
    }
});

// util para sacar el context
function getCtx() {
    if (window.CTX) return window.CTX;
    const fromBody = document.body && (document.body.dataset.context || document.body.getAttribute('data-context'));
    if (fromBody) return fromBody;
    return '';
}

// abrir modal de edición de equipo
async function editarEquipo(idEquipo) {
    const CTX = getCtx();
    try {
        const resp = await fetch(CTX + '/EditarEquipoServlet?id=' + idEquipo, {
            method: 'GET'
        });

        if (!resp.ok) {
            alert('No se pudo cargar el formulario de edición');
            return;
        }

        const html = await resp.text();

        // borrar modal viejo
        const viejo = document.getElementById('modalEditarEquipo');
        if (viejo) viejo.remove();

        document.body.insertAdjacentHTML('beforeend', html);
        inicializarModalEdicion();

    } catch (e) {
        console.error(e);
        alert('Error al abrir el editor de equipo');
    }
}

function cerrarModalEquipo() {
    const modal = document.getElementById('modalEditarEquipo');
    if (modal) modal.remove();
}

async function recargarListaEquiposAdmin(page) {
    const CTX = getCtx();
    // ESTE es el servlet que sí pone request.setAttribute("equipos"...)
    const url = page
        ? `${CTX}/equipos?page=${page}`
        : `${CTX}/equipos`;

    try {
        const resp = await fetch(url, {
            method: 'GET',
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        });
        const html = await resp.text();

        // parseo el HTML que devolvió el servlet
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');

        // en la respuesta que te manda el servlet, la parte copada es .contenido
        const nuevoContenido = doc.querySelector('.contenido');
        const soloEquipos = doc.querySelector('.contenido-equipos');

        // tu admin.jsp tiene <main class="contenido"> ... </main>
        const actual = document.querySelector('.contenido');
        if (nuevoContenido && actual) {
            actual.innerHTML = nuevoContenido.innerHTML;
        } else if (soloEquipos && actual) {
            actual.innerHTML = soloEquipos.outerHTML;
        }
    } catch (err) {
        console.error('Error al recargar equipos:', err);
        mostrarToast('No se pudo recargar la lista de equipos', true);
    }
}

function inicializarModalEdicion() {
    const form = document.getElementById('formEditarEquipo');
    if (!form) return;

    form.addEventListener('submit', async function (e) {
        e.preventDefault();

        const ctx = getCtx();
        const params = new URLSearchParams(new FormData(this));

        try {
            const resp = await fetch(ctx + '/EditarEquipoServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
                },
                body: params.toString()
            });

            const data = await resp.json();

            if (data.success) {
                if (typeof cerrarModalEquipo === 'function') {
                    cerrarModalEquipo();
                }
                if (typeof recargarListaEquiposAdmin === 'function') {
                    await recargarListaEquiposAdmin();
                }
                mostrarToast('Equipo actualizado correctamente');
            } else {
                mostrarToast(data.message || 'No se pudo actualizar el equipo', true);
            }
        } catch (err) {
            console.error('Error en fetch:', err);
            mostrarToast('Error al contactar el servidor', true);
        }
    });
}