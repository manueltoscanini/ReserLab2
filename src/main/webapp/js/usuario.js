// Funcionalidad del menú desplegable de reservas
document.addEventListener('DOMContentLoaded', function() {
    const botonReservas = document.getElementById('opciones-reserva');
    const submenuReservas = document.getElementById('submenu-reservas');

    // Función para alternar la visibilidad del submenú
    function toggleSubmenu() {
        if (submenuReservas.style.display === 'block') {
            submenuReservas.style.display = 'none';
            botonReservas.classList.remove('activo');
        } else {
            submenuReservas.style.display = 'block';
            botonReservas.classList.add('activo');
        }
    }

    // Event listener para el botón de reservas
    if (botonReservas) {
        botonReservas.addEventListener('click', function(e) {
            e.preventDefault();
            toggleSubmenu();
        });
    }

    // Cerrar el submenú al hacer clic fuera de él
    document.addEventListener('click', function(e) {
        if (!botonReservas.contains(e.target) && !submenuReservas.contains(e.target)) {
            submenuReservas.style.display = 'none';
            botonReservas.classList.remove('activo');
        }
    });

    // Event listeners para los botones del submenú
    const botonesSubmenu = submenuReservas.querySelectorAll('button');
    botonesSubmenu.forEach(function(boton) {
        boton.addEventListener('click', function(e) {
            e.preventDefault();
            // Aquí puedes agregar la lógica específica para cada opción
            const opcion = this.textContent.trim();
            console.log('Opción seleccionada:', opcion);

            // Ejemplo de manejo de cada opción
            switch(opcion) {
                case 'Hacer una reserva':
                    // Lógica para hacer una reserva
                    alert('Redirigiendo a hacer una reserva...');
                    break;
                case 'Mis reservas activas':
                    // Cargar reservas activas
                    cargarReservasActivas();
                    break;
                case 'Historial de reservas':
                    // Lógica para ver historial
                    alert('Mostrando historial de reservas...');
                    break;
            }
        });
    });
});

// Función para cargar reservas activas
function cargarReservasActivas() {
    fetch('ReservasActivasServlet', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error en la respuesta del servidor');
        }
        return response.json();
    })
    .then(data => {
        mostrarReservasActivas(data);
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarError('Error al cargar las reservas activas');
    });
}

// Función para mostrar las reservas activas en el contenido
function mostrarReservasActivas(reservas) {
    console.log('Reserva recibida:', reservas);
    const fecha = formatearFecha(reservas.fecha);
    const horaInicio = formatearHora(reservas.horaInicio);
    const horaFin = formatearHora(reservas.horaFin);
    const contenido = document.querySelector('.contenido');
    
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
                    <i class="fa-solid fa-flask"></i>
                    <i class="fa-solid fa-calendar-days icono-calendario"></i>
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
                        <span>${reserva.carreraCliente || 'Sede de la carrera'}</span>
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