/* usuario.js: */
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
    
    // Cargar reservas activas al inicio
    cargarReservasActivas();

    // Popups
    const popupEquipos  = document.getElementById('popupEquipos');
    const popupReservas = document.getElementById('popupReservas');
    const popupOtros    = document.getElementById('popupOtros');

    // Botones internos
    const btnListarEquipos = document.getElementById('btnListarEquipos');
    const btnHacerReserva = document.getElementById('btnHacerReserva');
    const btnMisReservas = document.getElementById('btnMisReservas');
    const btnHistorialReservas = document.getElementById('btnHistorialReservas');
    const btnReclamo = document.getElementById('btnReclamo');

    // --- Función utilitaria para cerrar todos los popups ---
    function cerrarTodosLosPopups() {
        [popupEquipos, popupReservas, popupOtros].forEach(p => {
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

    if (btnPerfil && contenido) {
        btnPerfil.addEventListener("click", function (e) {
            e.preventDefault(); // evita que el enlace recargue la página
            cerrarTodosLosPopups(); // cierra los submenús abiertos si los hay

            fetch("./VerPerfilServlet")
                .then(response => {
                    if (!response.ok) throw new Error("Error al obtener perfil");
                    return response.text();
                })
                .then(html => {
                    const contenido = document.querySelector('.contenido');
                    contenido.innerHTML = html;
                })
                .catch(error => console.error("Error al cargar el perfil:", error));
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
            cargarFormularioConsultas();
        });
    }

    // Cerrar popups al hacer click fuera de ellos
    document.addEventListener('click', (e) => {
        // si el click no es en la barra lateral ni en ninguno de los popups, cerramos todo
        const barra = document.querySelector('.barraLateral');
        if (!barra) return;
        if (!barra.contains(e.target) && ![popupEquipos, popupReservas, popupOtros].some(p => p && p.contains(e.target))) {
            cerrarTodosLosPopups();
        }
    });

    // 🔹 Ocultar mensajes de JSP automáticamente
    const mensajes = document.querySelectorAll(".mensaje-exito, .mensaje-error");
    mensajes.forEach(msg => {
        setTimeout(() => {
            msg.style.transition = "opacity 0.8s";
            msg.style.opacity = "0";
            setTimeout(() => msg.remove(), 800);
        }, 3000);
    });
});

/*==========================================================================================
    Código para el popup de edición de perfil
============================================================================================*/
function abrirEditarPerfil() {
    // En lugar de cargar el JSP directo, llamamos al Servlet para que envíe los datos
    fetch("EditarPerfilServlet")
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al cargar el formulario de edición");
            }
            return response.text();
        })
        .then(html => {
            const modalPrevio = document.getElementById("editarPerfilModal");
            if (modalPrevio) modalPrevio.remove();

            // Agregamos el modal al body
            document.body.insertAdjacentHTML("beforeend", html);
            const modal = document.getElementById("editarPerfilModal");
            modal.style.display = "flex";

            // Mostrar/ocultar carrera según el tipo de cliente actual
            const tipoSelect = document.getElementById("tipo_cliente");
            const divCarrera = document.getElementById("divCarrera");
            const carreraSelect = document.getElementById("carrera");

            if (tipoSelect && divCarrera) {
                // Función para manejar el cambio de tipo
                const manejarCambioTipo = () => {
                    if (tipoSelect.value === "estudiante") {
                        divCarrera.style.display = "block";
                        carreraSelect.required = true;
                    } else {
                        divCarrera.style.display = "none";
                        carreraSelect.required = false;
                        carreraSelect.value = "";
                    }
                };

                // Agregar el listener
                tipoSelect.addEventListener("change", manejarCambioTipo);

                // Ejecutar una vez para establecer el estado inicial
                manejarCambioTipo();
            }

            // Capturar el envío del formulario sin recargar la página
            const form = document.getElementById("formEditarPerfil");
            form.addEventListener("submit", function (e) {
                e.preventDefault();
                
                // Validación básica
                const nombre = document.getElementById("nombre").value.trim();
                const tipo = document.getElementById("tipo_cliente").value;
                
                if (!nombre) {
                    alert("Por favor, ingrese su nombre completo.");
                    return;
                }
                
                if (!tipo) {
                    alert("Por favor, seleccione un tipo de cliente.");
                    return;
                }
                
                if (tipo === "estudiante") {
                    const carrera = document.getElementById("carrera").value;
                    if (!carrera) {
                        alert("Por favor, seleccione una carrera.");
                        return;
                    }
                }
                
                // Crear FormData y agregar todos los campos
                const formData = new FormData();
                formData.append("nombre", nombre);
                formData.append("tipo_cliente", tipo);
                
                if (tipo === "estudiante") {
                    const carrera = document.getElementById("carrera").value;
                    formData.append("carrera", carrera);
                } else {
                    formData.append("carrera", "");
                }
                
                console.log("Enviando datos:", {
                    nombre: nombre,
                    tipo_cliente: tipo,
                    carrera: tipo === "estudiante" ? document.getElementById("carrera").value : ""
                });
                
                guardarCambiosPerfil(formData);
            });

            // Cerrar modal al hacer click fuera de él
            modal.addEventListener("click", function(e) {
                if (e.target === modal) {
                    cerrarModal();
                }
            });

            // Cerrar modal con tecla Escape
            document.addEventListener("keydown", function(e) {
                if (e.key === "Escape") {
                    cerrarModal();
                }
            });
        })
        .catch(err => {
            console.error("Error al abrir el editor de perfil:", err);
            alert("Error al cargar el formulario de edición. Por favor, inténtalo de nuevo.");
        });
}

function guardarCambiosPerfil(formData) {
    const params = new URLSearchParams();
    for (const [key, value] of formData.entries()) {
        params.append(key, value);
    }

    fetch("EditarPerfilServlet", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: params
    })
        .then(response => {
            if (!response.ok) throw new Error("Error en la respuesta del servidor");
            return response.text();
        })
        .then(html => {
            cerrarModal();
            const contenido = document.querySelector(".contenido");
            contenido.innerHTML = html;

            // 🔹 Actualizar el nombre mostrado en la barra lateral
            const nuevoNombre = document.getElementById("nombre").value.trim();
            const nombreLateral = document.getElementById("nombreUsuario");
            if (nombreLateral && nuevoNombre) {
                nombreLateral.textContent = nuevoNombre;
            }

            // 🔹 Mostrar mensaje temporal de éxito (lo hacemos abajo)
            mostrarMensajeTemporal("Datos actualizados correctamente.", "exito");
        })
        .catch(error => {
            console.error("Error al guardar cambios:", error);
            alert("Error al guardar los cambios. Por favor, inténtalo de nuevo.");
        });
}

function cerrarModal() {
    // Buscar cualquier modal abierto
    const modal = document.querySelector(".modal-overlay");
    if (modal) {
        modal.style.transition = "all 0.2s ease";
        modal.style.opacity = "0";
        modal.style.transform = "translateY(-20px) scale(0.95)";
        setTimeout(() => {
            modal.remove();
        }, 200);
    }
}

// 🔹 Función para mostrar mensajes temporales (éxito o error)
function mostrarMensajeTemporal(texto, tipo = "exito") {
    const mensaje = document.createElement("div");
    mensaje.className = tipo === "exito" ? "mensaje-exito" : "mensaje-error";
    mensaje.textContent = texto;

    // Lo agregamos al contenido principal
    const contenido = document.querySelector(".contenido") || document.body;
    contenido.prepend(mensaje);

    // Desvanecer después de 3 segundos
    setTimeout(() => {
        mensaje.style.transition = "opacity 0.8s";
        mensaje.style.opacity = "0";
        setTimeout(() => mensaje.remove(), 800);
    }, 3000);
}

// ======================================================
// CAMBIAR CONTRASEÑA
// ======================================================
document.addEventListener("click", (e) => {
    if (e.target && e.target.id === "btnCambiarContrasenia") {
        fetch("CambiarContraseniaServlet")
            .then(res => res.text())
            .then(html => {

                const modalPrevio = document.getElementById("editarPerfilModal");
                if (modalPrevio) modalPrevio.remove();

                document.body.insertAdjacentHTML("beforeend", html);
                const modal = document.getElementById("cambiarContraseniaModal");
                modal.style.display = "flex";

                const form = document.getElementById("formCambiarContrasenia");
                form.addEventListener("submit", async (ev) => {
                    ev.preventDefault();
                    const formData = new FormData(form);
                    const params = new URLSearchParams(formData);

                    const resp = await fetch("CambiarContraseniaServlet", {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded" },
                        body: params
                    });

                    const mensaje = await resp.text();
                    const [tipo, texto] = mensaje.split(":");
                    cerrarModal();
                    mostrarMensajeTemporal(texto, tipo === "exito" ? "exito" : "error");
                });
                // Cerrar modal al hacer click fuera de él
                modal.addEventListener("click", function(e) {
                    if (e.target === modal) {
                        cerrarModal();
                    }
                });

                // Cerrar modal con tecla Escape
                document.addEventListener("keydown", function(e) {
                    if (e.key === "Escape") {
                        cerrarModal();
                    }
                });
            })
            .catch(err => {
                console.error("Error al cargar el cambio de contraseña:", err);
                mostrarMensajeTemporal("Error al cargar el formulario.", "error");
            });
    }
});


/*==========================================================================================
    Código específico para la gestión de reservas activas
============================================================================================*/
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
    Swal.fire({
        title: '¿Estás seguro?',
        text: '¿Estás seguro de que quieres cancelar esta reserva?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc2626',
        cancelButtonColor: '#6b7280',
        confirmButtonText: 'Sí, cancelar',
        cancelButtonText: 'No, mantener',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            // Aquí implementarías la lógica para cancelar la reserva
            Swal.fire({
                title: 'Funcionalidad en desarrollo',
                text: 'La funcionalidad de cancelación está en desarrollo',
                icon: 'info',
                confirmButtonText: 'Entendido'
            });
        }
    });
}

function editarReserva(idActividad) {
    // Aquí implementarías la lógica para editar la reserva
    alert('Funcionalidad de edición en desarrollo');
}


/* ------------------------ */

// Función para cargar el historial de reservas al inicio (sin cambiar el contenido completo)
function cargarHistorialInicio() {
    fetch('HistorialReservasServlet', {
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
        mostrarHistorialInicio(data);
    })
    .catch(error => {
        console.error('Error:', error);
        mostrarErrorHistorialInicio('Error al cargar el historial de reservas');
    });
}

// Función para mostrar el historial en el inicio (solo actualiza la sección del historial)
function mostrarHistorialInicio(reservas) {
    console.log('Reservas recibidas:', reservas);
    const contenedorHistorial = document.getElementById('contenedor-historial');
    
    if (!contenedorHistorial) {
        console.error('No se encontró el contenedor de historial');
        return;
    }
    
    if (reservas.length === 0) {
        contenedorHistorial.innerHTML = `
            <div class="sin-reservas">
                <i class="fa-solid fa-calendar-xmark"></i>
                <p>No tienes reservas en tu historial</p>
            </div>
        `;
        return;
    }
    
    let html = '';
    
    reservas.forEach(reserva => {
        const fecha = formatearFecha(reserva.fecha);
        const horaInicio = formatearHora(reserva.horaInicio);
        const horaFin = formatearHora(reserva.horaFin);
        
        html += `
            <div class="tarjeta-reserva">
                <div class="icono-reserva">
                    <i class="fa-solid fa-flask"></i>
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
                        <span class="estado estado-${reserva.estado.toLowerCase()}">${reserva.estado}</span>
                    </div>
                </div>
            </div>
        `;
    });
    
    contenedorHistorial.innerHTML = html;
}

// Función para mostrar error en el historial del inicio
function mostrarErrorHistorialInicio(mensaje) {
    const contenedorHistorial = document.getElementById('contenedor-historial');
    if (contenedorHistorial) {
        contenedorHistorial.innerHTML = `
            <div class="error-mensaje">
                <i class="fa-solid fa-exclamation-triangle"></i>
                <p>${mensaje}</p>
            </div>
        `;
    }
}

/* ------------------------ */

// Función para cargar reservas activas
function cargarHistorialReservas() {
    fetch('HistorialReservasServlet', {
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
            mostrarHistorial(data);
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al cargar historial');
        });
}

// Función para mostrar las reservas activas en el contenido
function mostrarHistorial(reservas) {
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

/*==========================================================================================
    Código específico para consultas y reclamos
============================================================================================*/
function cargarFormularioConsultas() {
    const contenido = document.querySelector('.contenido');
    
    // Pantalla 1: Botón inicial
    contenido.innerHTML = `
        <div class="contenedor-consultas">
            <div class="pantalla-inicial" id="pantallaInicial">
                <button class="btn-iniciar-consulta" id="btnIniciar">
                    Enviar consulta o reclamo
                </button>
            </div>
            
            <div class="pantalla-seleccion" id="pantallaSeleccion">
                <h2>Seleccione el tipo de comunicación:</h2>
                <div class="botones-tipo">
                    <button class="btn-tipo" data-tipo="consulta">
                        Consulta
                    </button>
                    <button class="btn-tipo" data-tipo="queja">
                        Queja/Reclamo
                    </button>
                </div>
                
                <div class="rect-azul-consultas" id="formularioConsulta">
                    <h2 id="tituloFormulario">Enviar Consulta o Reclamo por email</h2>
                    
                    <form id="formConsultaReclamo">
                        <input type="hidden" id="tipoMensaje" name="tipo" value="">
                        
                        <div class="form-group">
                            <label>Destinatario:</label>
                            <select id="selectAdmin" name="admin" required>
                                <option value="">Cargando administradores...</option>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label>Descripcion:</label>
                            <textarea id="descripcion" name="descripcion" required></textarea>
                        </div>
                        
                        <div class="btn-enviar-container">
                            <button type="submit" class="btn-enviar">Enviar</button>
                        </div>
                    </form>
                    
                    <div class="mensaje-estado" id="mensajeEstado"></div>
                </div>
            </div>
        </div>
    `;
    
    // Inicializar eventos
    inicializarConsultas();
}

function inicializarConsultas() {
    const btnIniciar = document.getElementById('btnIniciar');
    const pantallaInicial = document.getElementById('pantallaInicial');
    const pantallaSeleccion = document.getElementById('pantallaSeleccion');
    const botonesType = document.querySelectorAll('.btn-tipo');
    const formulario = document.getElementById('formularioConsulta');
    const tipoMensajeInput = document.getElementById('tipoMensaje');
    const tituloFormulario = document.getElementById('tituloFormulario');
    const form = document.getElementById('formConsultaReclamo');
    
    // Botón inicial - ir a pantalla de selección
    if (btnIniciar) {
        btnIniciar.addEventListener('click', function() {
            pantallaInicial.style.display = 'none';
            pantallaSeleccion.classList.add('visible');
        });
    }
    
    // Cargar administradores inmediatamente
    cargarAdministradoresConsulta();
    
    // Manejar selección de tipo
    botonesType.forEach(btn => {
        btn.addEventListener('click', function() {
            // Remover clase activo de todos
            botonesType.forEach(b => b.classList.remove('activo'));
            // Agregar clase activo al seleccionado
            this.classList.add('activo');
            
            const tipo = this.dataset.tipo;
            tipoMensajeInput.value = tipo;
            
            // Actualizar título y mostrar formulario
            if (tipo === 'consulta') {
                tituloFormulario.textContent = 'Formulario de Consulta';
            } else {
                tituloFormulario.textContent = 'Formulario de Queja/Reclamo';
            }
            
            formulario.classList.add('visible');
        });
    });
    
    // Manejar envío del formulario
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const tipo = tipoMensajeInput.value;
            const selectAdmin = document.getElementById('selectAdmin');
            const descripcion = document.getElementById('descripcion').value;
            const btnEnviar = form.querySelector('.btn-enviar');
            
            if (!tipo) {
                mostrarMensajeConsulta('Por favor seleccione el tipo de comunicación', 'error');
                return;
            }
            
            if (!selectAdmin.value) {
                mostrarMensajeConsulta('Por favor seleccione un administrador', 'error');
                return;
            }
            
            // Deshabilitar botón durante el envío
            btnEnviar.disabled = true;
            btnEnviar.textContent = 'Enviando...';
            
            try {
                const adminData = JSON.parse(selectAdmin.value);
                
                const formData = new URLSearchParams();
                formData.append('tipo', tipo);
                formData.append('adminEmail', adminData.email);
                formData.append('adminNombre', adminData.nombre);
                formData.append('descripcion', descripcion);
                
                const response = await fetch('ConsultaReclamoServlet', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData
                });
                
                const result = await response.json();
                
                if (result.success) {
                    mostrarMensajeConsulta('Su mensaje ha sido enviado exitosamente', 'exito');
                    form.reset();
                    tipoMensajeInput.value = '';
                    formulario.classList.remove('visible');
                    botonesType.forEach(b => b.classList.remove('activo'));
                } else {
                    mostrarMensajeConsulta(result.mensaje || 'Error al enviar el mensaje', 'error');
                }
                
            } catch (error) {
                console.error('Error:', error);
                mostrarMensajeConsulta('Error de conexión. Por favor intente nuevamente.', 'error');
            } finally {
                btnEnviar.disabled = false;
                btnEnviar.textContent = 'Enviar';
            }
        });
    }
}

async function cargarAdministradoresConsulta() {
    const selectAdmin = document.getElementById('selectAdmin');
    
    if (!selectAdmin) return;
    
    try {
        const response = await fetch('ConsultaReclamoServlet');
        const administradores = await response.json();
        
        selectAdmin.innerHTML = '<option value="">-- Seleccione un administrador --</option>';
        
        administradores.forEach(admin => {
            const option = document.createElement('option');
            option.value = JSON.stringify({
                email: admin.email,
                nombre: admin.nombre
            });
            option.textContent = `${admin.nombre} (${admin.email})`;
            selectAdmin.appendChild(option);
        });
        
    } catch (error) {
        console.error('Error al cargar administradores:', error);
        selectAdmin.innerHTML = '<option value="">Error al cargar administradores</option>';
    }
}

function mostrarMensajeConsulta(mensaje, tipo) {
    const mensajeEstado = document.getElementById('mensajeEstado');
    if (!mensajeEstado) return;
    
    mensajeEstado.textContent = mensaje;
    mensajeEstado.className = `mensaje-estado ${tipo} visible`;
    
    // Ocultar después de 5 segundos
    setTimeout(() => {
        mensajeEstado.classList.remove('visible');
    }, 5000);
}