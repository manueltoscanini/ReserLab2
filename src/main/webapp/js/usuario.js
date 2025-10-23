/*==========================================================================================
    Código general para la interfaz de usuario (barra lateral, popups, carga dinámica)
============================================================================================*/

document.addEventListener("DOMContentLoaded", () => {
    // --- Selecciones ---
    const contenido = document.querySelector('.contenido');

    // Botones de la barra
    const btnReservas = document.getElementById('opciones-reserva');
    const btnEquipos = document.getElementById('opciones-equipos');
    const btnOtros   = document.getElementById('opciones-otros');
    const btnPerfil  = document.getElementById('opciones-perfil');

    // Popups
    const popupEquipos  = document.getElementById('popupEquipos');
    const popupPerfil   = document.getElementById('popupPerfil');
    const popupReservas = document.getElementById('popupReservas');
    const popupOtros    = document.getElementById('popupOtros');

    // Botones internos
    const btnListar = document.getElementById('btnListarEquipos');
    const btnVerPerfil = document.getElementById('btnVerPerfil');
    const btnCambiarDatos = document.getElementById('btnCambiarDatos');
    const btnCambiarContrasena = document.getElementById('btnCambiarContraseña');
    const btnEliminarCuenta = document.getElementById('btnEliminarCuenta');
    const btnHacerReserva = document.getElementById('btnHacerReserva');
    const btnMisReservas = document.getElementById('btnMisReservas');
    const btnHistorialReservas = document.getElementById('btnHistorialReservas');
    const btnReclamo = document.getElementById('btnReclamo');

    // --- Foto de perfil ---
    const btnCambiarFoto = document.getElementById('btnCambiarFoto');
    const inputFoto = document.getElementById('inputFoto');
    const fotoPerfil = document.getElementById('fotoPerfil');
    const iconoPerfil = document.getElementById('iconoPerfil');

    if (btnCambiarFoto && inputFoto) {
        btnCambiarFoto.addEventListener('click', () => {
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

    // --- Función utilitaria para cerrar todos los popups ---
    function cerrarTodosLosPopups() {
        [popupEquipos, popupPerfil, popupReservas, popupOtros].forEach(p => {
            if (p) p.classList.add('oculto');
        });
    }

    // --- Seguridad: si un botón o popup no existe, no agregar el listener ---
    if (btnEquipos && popupEquipos) {
        btnEquipos.addEventListener('click', () => {
            // Toggle: si queremos cerrar los otros y abrir éste:
            const estaOculto = popupEquipos.classList.contains('oculto');
            cerrarTodosLosPopups();
            if (estaOculto) popupEquipos.classList.remove('oculto');
            else popupEquipos.classList.add('oculto');
        });
    }

    // PERFIL
    if (btnPerfil && popupPerfil) {
        btnPerfil.addEventListener('click', () => {
            const estaOculto = popupPerfil.classList.contains('oculto');
            cerrarTodosLosPopups();
            if (estaOculto) popupPerfil.classList.remove('oculto');
            else popupPerfil.classList.add('oculto');
        });
    }

    // RESERVAS
    if (btnReservas && popupReservas) {
        btnReservas.addEventListener('click', () => {
            const estaOculto = popupReservas.classList.contains('oculto');
            cerrarTodosLosPopups();
            if (estaOculto) popupReservas.classList.remove('oculto');
            else popupReservas.classList.add('oculto');
        });
    }

    // OTROS
    if (btnOtros && popupOtros) {
        btnOtros.addEventListener('click', () => {
            const estaOculto = popupOtros.classList.contains('oculto');
            cerrarTodosLosPopups();
            if (estaOculto) popupOtros.classList.remove('oculto');
            else popupOtros.classList.add('oculto');
        });
    }

    /* ======================================================
       SUBMENÚ PERFIL
    ====================================================== */
    if (btnVerPerfil && contenido) {
        btnVerPerfil.addEventListener('click', () => {
            cerrarTodosLosPopups();
            // TODO: implementar ver perfil
        });
    }

    if (btnCambiarDatos && contenido) {
        btnCambiarDatos.addEventListener('click', () => {
            cerrarTodosLosPopups();
            // TODO: implementar cambiar datos
        });
    }

    if (btnCambiarContrasena && contenido) {
        btnCambiarContrasena.addEventListener('click', () => {
            cerrarTodosLosPopups();
            // TODO: implementar cambiar contraseña
        });
    }

    if (btnEliminarCuenta && contenido) {
        btnEliminarCuenta.addEventListener('click', () => {
            cerrarTodosLosPopups();
            // TODO: implementar eliminar cuenta
        });
    }

    /* ======================================================
       SUBMENÚ RESERVAS
    ====================================================== */
    if (btnHacerReserva && contenido) {
        btnHacerReserva.addEventListener('click', () => {
            cerrarTodosLosPopups();
            // TODO: implementar hacer reserva
        });
    }

    if (btnMisReservas && contenido) {
        btnMisReservas.addEventListener('click', () => {
            cerrarTodosLosPopups();
            cargarReservasActivas();
        });
    }

    if (btnHistorialReservas && contenido) {
        btnHistorialReservas.addEventListener('click', () => {
            cerrarTodosLosPopups();
            cargarHistorialReservas();
            // TODO: implementar historial de reservas
        });
    }

    /* ======================================================
       SUBMENÚ EQUIPOS
    ====================================================== */
    if (btnListarEquipos && contenido) {
        btnListarEquipos.addEventListener('click', async () => {
            cerrarTodosLosPopups();
            if (popupEquipos) popupEquipos.classList.add('oculto');

            try {
                const resp = await fetch("EquiposServlet");
                if (!resp.ok) throw new Error('Error al cargar equipos');
                const html = await resp.text();
                contenido.innerHTML = html;
            } catch (err) {
                contenido.innerHTML = `<p class="error-mensaje">No se pudo cargar la lista de equipos.</p>`;
                console.error(err);
            }
        });
    }

    /* ======================================================
       SUBMENÚ OTROS
    ====================================================== */
    if (btnReclamo && contenido) {
        btnReclamo.addEventListener('click', () => {
            cerrarTodosLosPopups();
            // TODO: implementar reclamos
        });
    }

    // Cerrar popups al hacer click fuera de ellos
    document.addEventListener('click', (e) => {
        // si el click no es en la barra lateral ni en ninguno de los popups, cerramos todo
        const barra = document.querySelector('.barraLateral');
        if (!barra) return;
        if (!barra.contains(e.target) && ![popupEquipos, popupPerfil, popupReservas, popupOtros].some(p => p && p.contains(e.target))) {
            cerrarTodosLosPopups();
        }
    });
});

/*==========================================================================================
    Código específico para la gestión de reservas activas
============================================================================================*/
// Función para cargar reservas activas
function cargarReservasActivas() {
    const contenido = document.querySelector('.contenido');
    
    // Mostrar indicador de carga
    contenido.innerHTML = `
        <div class="contenido-reservas">
            <h1>Cargando reservas activas...</h1>
            <div class="sin-reservas">
                <i class="fa-solid fa-spinner fa-spin"></i>
            </div>
        </div>
    `;
    
    fetch('ReservasActivasServlet', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        console.log('Response status:', response.status);
        if (!response.ok) {
            throw new Error('Error en la respuesta del servidor (Status: ' + response.status + ')');
        }
        return response.json();
    })
    .then(data => {
        console.log('Datos recibidos:', data);
        // Verificar si hay error en la respuesta
        if (data.error) {
            throw new Error(data.error);
        }
        mostrarReservasActivas(data);
    })
    .catch(error => {
        console.error('Error completo:', error);
        mostrarError('Error al cargar las reservas activas: ' + error.message);
    });
}

// Función para mostrar las reservas activas en el contenido
function mostrarReservasActivas(reservas) {
    console.log('Reservas recibidas:', reservas);
    const contenido = document.querySelector('.contenido');
    
    // Verificar si reservas es un array válido
    if (!Array.isArray(reservas)) {
        console.error('Las reservas no son un array:', reservas);
        mostrarError('Error: Formato de datos inválido');
        return;
    }
    
    if (reservas.length === 0) {
        contenido.innerHTML = `
            <div class="contenido-reservas">
                <h1>Reservas activas</h1>
                <div class="sin-reservas">
                    <i class="fa-solid fa-calendar-xmark"></i>
                    <p>No tienes reservas activas en este momento</p>
                </div>
            </div>
        `;
        return;
    }
    
    let html = `
        <div class="contenido-reservas">
            <h1>Reservas activas</h1>
            <div class="grid-reservas">
    `;
    
    reservas.forEach(reserva => {
        const fecha = formatearFecha(reserva.fecha);
        const horaInicio = formatearHora(reserva.horaInicio);
        const horaFin = formatearHora(reserva.horaFin);
        
        html += `
            <div class="tarjeta-reserva">
                <div class="icono-reserva">
                     <img src="imagenes/logo.png" alt="Logo ReserLab" class="logo-ficha">
                </div>
                <div class="detalles-reserva">
                    <div class="detalle">
                        <i class="fa-solid fa-calendar-days"></i>
                        <span>${fecha}</span>
                    </div>
                    <div class="detalle">
                        <i class="fa-solid fa-clock"></i>
                        <span>${horaInicio} - ${horaFin}</span>
                    </div>
                    <div class="detalle">
                        <i class="fa-solid fa-map-marker-alt"></i>
                        <span>${reserva.carreraCliente || 'Sede no disponible'}</span>
                    </div>
                </div>
                <div class="botones-reserva">
                    <button class="btn-cancelar" onclick="cancelarReserva(${reserva.idActividad})">
                        Cancelar
                    </button>
                    <button class="btn-editar" onclick="editarReserva(${reserva.idActividad})">
                        Editar reserva
                    </button>
                </div>
            </div>
        `;
    });
    
    html += `
            </div>
        </div>
    `;
    
    contenido.innerHTML = html;
}

// Función para mostrar errores
function mostrarError(mensaje) {
    const contenido = document.querySelector('.contenido');
    contenido.innerHTML = `
        <div class="contenido-reservas">
            <h1>Error</h1>
            <div class="error-mensaje">
                <i class="fa-solid fa-exclamation-triangle"></i>
                <p>${mensaje}</p>
            </div>
        </div>
    `;
}

// Funciones auxiliares para formatear fechas y horas
function formatearFecha(fecha) {
    if (!fecha) return '—';
    const partes = fecha.split('-');
    if (partes.length === 3) {
        return `${partes[2]}/${partes[1]}/${partes[0]}`; // dd/mm/yyyy
    }
    return fecha;
}


function formatearHora(hora) {
    if (!hora) return '—';

    // Normalizar el texto
    hora = hora.toLowerCase().replace(/\s+/g, ''); // quita espacios
    let periodo = '';

    if (hora.includes('am') || hora.includes('a.m.')) periodo = 'AM';
    if (hora.includes('pm') || hora.includes('p.m.')) periodo = 'PM';

    // Extraer la parte HH:MM:SS
    const match = hora.match(/(\d{1,2}):(\d{2})/);
    if (!match) return hora; // por si no matchea

    let h = parseInt(match[1]);
    const m = match[2];

    if (periodo === 'PM' && h < 12) h += 12;
    if (periodo === 'AM' && h === 12) h = 0;

    return `${h.toString().padStart(2, '0')}:${m}`;
}


// Funciones para los botones de acción
function cancelarReserva(idActividad) {
    if (confirm('¿Estás seguro de que quieres cancelar esta reserva?')) {
        // Aquí implementarías la lógica para cancelar la reserva
        alert('Funcionalidad de cancelación en desarrollo');
    }
}

function editarReserva(idActividad) {
    // Aquí implementarías la lógica para editar la reserva
    alert('Funcionalidad de edición en desarrollo');
}


/* ------------------------ */

// Función para cargar historial de reservas
function cargarHistorialReservas() {
    const contenido = document.querySelector('.contenido');
    
    // Mostrar indicador de carga
    contenido.innerHTML = `
        <div class="contenido-reservas">
            <h1>Cargando historial...</h1>
            <div class="sin-reservas">
                <i class="fa-solid fa-spinner fa-spin"></i>
            </div>
        </div>
    `;
    
    fetch('HistorialReservasServlet', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        console.log('Response status:', response.status);
        if (!response.ok) {
            throw new Error('Error en la respuesta del servidor (Status: ' + response.status + ')');
        }
        return response.json();
    })
    .then(data => {
        console.log('Datos recibidos:', data);
        // Verificar si hay error en la respuesta
        if (data.error) {
            throw new Error(data.error);
        }
        mostrarHistorial(data);
    })
    .catch(error => {
        console.error('Error completo:', error);
        mostrarError('Error al cargar historial: ' + error.message);
    });
}

// Función para mostrar el historial de reservas
function mostrarHistorial(reservas) {
    console.log('Historial recibido:', reservas);
    const contenido = document.querySelector('.contenido');
    
    // Verificar si reservas es un array válido
    if (!Array.isArray(reservas)) {
        console.error('El historial no es un array:', reservas);
        mostrarError('Error: Formato de datos inválido');
        return;
    }

    if (reservas.length === 0) {
        contenido.innerHTML = `
            <div class="contenido-reservas">
                <h1>Historial de reservas</h1>
                <div class="sin-reservas">
                    <i class="fa-solid fa-calendar-xmark"></i>
                    <p>No tienes reservas en el historial</p>
                </div>
            </div>
        `;
        return;
    }

    let html = `
        <div class="contenido-reservas">
            <h1>Historial de reservas</h1>
            <div class="grid-reservas">
    `;

    reservas.forEach(reserva => {
        const fecha = formatearFecha(reserva.fecha);
        const horaInicio = formatearHora(reserva.horaInicio);
        const horaFin = formatearHora(reserva.horaFin);

        html += `
            <div class="tarjeta-reserva">
                <div class="icono-reserva">
                     <img src="imagenes/logo.png" alt="Logo ReserLab" class="logo-ficha">
                </div>
                <div class="detalles-reserva">
                    <div class="detalle">
                        <i class="fa-solid fa-calendar-days"></i>
                        <span>${fecha}</span>
                    </div>
                    <div class="detalle">
                        <i class="fa-solid fa-clock"></i>
                        <span>${horaInicio} - ${horaFin}</span>
                    </div>
               
                    <div class="detalle">
                        <i class="fa-solid fa-bell"></i>
                        <span>${reserva.estado}</span>
                    </div>
                </div>
                
            </div>
        `;
    });

    html += `
            </div>
        </div>
    `;

    contenido.innerHTML = html;
}