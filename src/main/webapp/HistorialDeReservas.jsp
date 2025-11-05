<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Models.Actividad" %>
<%@ page import="Models.Usuario" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="DAO.ActividadDAO" %>

<%
    // Evita que se guarde en caché (por seguridad)
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Obtiene el nombre de usuario de la sesión
    String nombreUsuario = (String) session.getAttribute("nombreUsuario");
    String fotoUsuario = (String) session.getAttribute("fotoUsuario");

    // Si no hay usuario logueado, redirige al login
    if (nombreUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Obtener el usuario completo de la sesión
    Usuario usuario = (Usuario) session.getAttribute("usuario");

    // Obtener lista de reservas desde el servlet
    List<Actividad> reservas = null;
    if (usuario != null) {
        ActividadDAO actividadDAO = new ActividadDAO();
        // Actualizar estados antes de consultar
        actividadDAO.actualizarEstadosSegunTiempo();

        reservas = (List<Actividad>) request.getAttribute("historialReservas");
    }

    // Obtener mensajes de éxito o error (si los hubiera)
    String mensajeExito = (String) session.getAttribute("exito");
    String mensajeError = (String) session.getAttribute("error");

    // Evitar que se repitan los mensajes al recargar
    session.removeAttribute("exito");
    session.removeAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Historial de Reservas - ReserLab</title>
    <link rel="stylesheet" href="estilos/usuario.css?v=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

<div class="contenedorPrincipal">
    <aside class="barraLateral">
        <div class="perfil">
            <div class="foto-perfil-container">
                <% if (fotoUsuario != null && !fotoUsuario.isEmpty()) { %>
                <img src="<%= fotoUsuario %>" alt="Foto de perfil"
                     class="fotoPerfil" id="fotoPerfil"
                     title="Ver mi perfil">
                <% } else { %>
                <i class="fa-solid fa-user-circle iconoPerfil"
                   id="iconoPerfil" title="Ver mi perfil"></i>
                <% } %>

                <button class="btn-cambiar-foto" id="btnCambiarFoto" title="Cambiar foto">
                    <i class="fa-solid fa-camera"></i>
                </button>
                <input type="file" id="inputFoto" accept="image/*" style="display: none;">
            </div>

            <h2 class="nombreUsuario" id="nombreUsuario"><%= nombreUsuario %>
            </h2>
        </div>

        <nav class="menu">
            <button id="opciones-reserva"><i class="fa-solid fa-calendar-days"></i> Reservas</button>
            <button id="opciones-equipos"><i class="fa-solid fa-laptop"></i> Equipos</button>
            <button id="opciones-otros"><i class="fa-solid fa-ellipsis-h"></i> Otros</button>
        </nav>

        <!-- Botón de modo oscuro -->
        <div class="modoOscuroContainer">
            <button id="btnModoOscuro" class="btnModoOscuro">
                <i class="fa-solid fa-moon"></i> Modo oscuro
            </button>
        </div>

        <form class="logout" action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit"><i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión</button>
        </form>
    </aside>

    <div id="popupEquipos" class="popup oculto">
        <div class="flechaAzul"></div>
        <div class="rectAzul">
            <button id="btnListarEquipos" class="btnVerde">Listar Equipos</button>
        </div>
    </div>

    <!-- Popup Reservas -->
    <div id="popupReservas" class="popup oculto">
        <div class="flechaAzul"></div>
        <div class="rectAzul">
            <div class="submenu-vertical">
                <button class="btn-crear-reserva" onclick="mostrarModalCrearReserva()">
                    <i class="fa-solid fa-plus"></i> Hacer reserva
                </button>
                <button id="btnMisReservas" class="btnVerde">Mis reservas activas</button>
                <button id="btnHistorialReservas" class="btnVerde">Historial de reservas</button>
            </div>
        </div>
    </div>

    <!-- Popup Otros -->
    <div id="popupOtros" class="popup oculto">
        <div class="flechaAzul"></div>
        <div class="rectAzul">
            <div class="submenu-vertical">
                <button id="btnReclamo" class="btnVerde">Enviar reclamo o consulta</button>
            </div>
        </div>
    </div>

    <main class="contenido">
        <%-- DEBUG: Visual indicator that we're on history page --%>
        <script>console.log('DEBUG: HistorialDeReservas.jsp CARGADO');</script>

        <div class="contenido-reservas">
            <div class="header-reservas">
                <h2 class="titulo-seccion"> Historial de Reservas</h2>
                <button class="btn-crear-reserva" onclick="mostrarModalCrearReserva()">
                    <i class="fa-solid fa-plus"></i> Crear Reserva
                </button>
            </div>

            <%-- Filtros de búsqueda --%>
            <div class="filtros-container">
                <div class="filtro-grupo">
                    <label for="filtroFecha">
                        <i class="fa-solid fa-calendar"></i> Filtrar por Fecha:
                    </label>
                    <input type="date" id="filtroFecha" class="input-filtro">
                    <button class="btn-limpiar-filtro" onclick="limpiarFiltroFecha()" title="Limpiar filtro">
                        <i class="fa-solid fa-times"></i>
                    </button>
                </div>

                <div class="filtro-grupo">
                    <label for="filtroEstado">
                        <i class="fa-solid fa-filter"></i> Filtrar por Estado:
                    </label>
                    <select id="filtroEstado" class="select-filtro">
                        <option value="">Todos los estados</option>
                        <option value="en_espera">En Espera</option>
                        <option value="aceptada">Aceptada</option>
                        <option value="rechazada">Rechazada</option>
                        <option value="en_curso">En Curso</option>
                        <option value="finalizada">Finalizada</option>
                        <option value="no_asistió">No Asistió</option>
                        <option value="desactivada">Desactivada</option>
                    </select>
                </div>
            </div>

            <%-- Mostrar mensajes si existen --%>
            <% if (mensajeExito != null) { %>
            <div class="mensaje-exito">
                <i class="fa-solid fa-check-circle"></i>
                <%= mensajeExito %>
            </div>
            <% } %>

            <% if (mensajeError != null) { %>
            <div class="mensaje-error">
                <i class="fa-solid fa-exclamation-circle"></i>
                <%= mensajeError %>
            </div>
            <% } %>

            <%-- Si no hay reservas --%>
            <% if (reservas == null || reservas.isEmpty()) { %>
            <div class="sin-reservas">
                <i class="fa-solid fa-calendar-xmark"></i>
                <h3>No hay reservas disponibles</h3>
                <p>No se encontraron reservas en tu historial.</p>
            </div>
            <% } else { %>
            <div class="grid-reservas" id="gridReservas">
                <%
                    for (Actividad reserva : reservas) {
                        String estado = reserva.getEstado();
                        boolean puedeAccionar = !("en_curso".equalsIgnoreCase(estado) || "finalizada".equalsIgnoreCase(estado));
                %>
                <div class="tarjeta-reserva"
                     data-fecha="<%= reserva.getFecha().toString() %>"
                     data-estado="<%= reserva.getEstado().toLowerCase() %>">
                    <div class="icono-reserva">
                        <img src="imagenes/logo.png" alt="Logo ReserLab" class="logo-ficha">
                    </div>
                    <div class="detalles-reserva">
                        <div class="detalle">
                            <i class="fa-solid fa-calendar-days"></i>
                            <span><%= reserva.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) %></span>
                        </div>
                        <div class="detalle">
                            <i class="fa-solid fa-clock"></i>
                            <span><%= reserva.getHoraInicio().toString().substring(0, 5) %> - <%= reserva.getHoraFin().toString().substring(0, 5) %></span>
                        </div>
                        <div class="detalle">
                            <i class="fa-solid fa-bell"></i>
                            <span class="estado-<%= reserva.getEstado().toLowerCase().replace("_", "-") %>"><%= reserva.getEstado() %></span>
                        </div>
                        <% if (reserva.getCarreraCliente() != null) { %>
                        <div class="detalle">
                            <i class="fa-solid fa-map-marker-alt"></i>
                            <span><%= reserva.getCarreraCliente() %></span>
                        </div>
                        <% } %>
                    </div>
                    <div class="botones-reserva">
                        <% if (puedeAccionar) { %>
                        <button class="btn-cancelar" onclick="cancelarReserva(<%= reserva.getIdActividad() %>)">
                            <i class="fa-solid fa-times"></i> Cancelar
                        </button>
                        <button class="btn-editar" onclick="editarReserva(<%= reserva.getIdActividad() %>)">
                            <i class="fa-solid fa-edit"></i> Editar
                        </button>
                        <% } %>
                    </div>
                </div>
                <% } %>
            </div>
            <% } %>
        </div>
    </main>
</div>

<!-- Modal para editar reserva -->
<div id="modalEditarReserva" class="modal-overlay" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3><i class="fa-solid fa-calendar-pen"></i> Editar Reserva</h3>
            <button class="btn-cerrar-modal" onclick="cerrarModalEditarReserva()">
                <i class="fa-solid fa-times"></i>
            </button>
        </div>
        <form id="formEditarReserva" action="editar-reserva" method="post">
            <input type="hidden" id="editIdActividad" name="idActividad">
            <div class="form-grid-usuario">

                <div class="form-group">
                    <label for="editFecha">
                        <i class="fa-solid fa-calendar"></i> Fecha
                    </label>
                    <input type="date" id="editFecha" name="fecha" required>
                </div>

                <div class="form-group">
                    <label for="editHoraInicio">
                        <i class="fa-solid fa-clock"></i> Hora Inicio
                    </label>
                    <input type="time" id="editHoraInicio" name="horaInicio" required>
                </div>

                <div class="form-group">
                    <label for="editHoraFin">
                        <i class="fa-solid fa-clock"></i> Hora Fin
                    </label>
                    <input type="time" id="editHoraFin" name="horaFin" required>
                </div>

                <div class="form-group">
                    <label for="editCantidadParticipantes">
                        <i class="fa-solid fa-users"></i> Cantidad de Participantes
                    </label>
                    <input type="number" id="editCantidadParticipantes" name="cantidadParticipantes"
                           min="1" max="10" required>
                </div>

                <div class="form-group equipos-section">
                    <label>
                        <i class="fa-solid fa-laptop"></i> Equipos de Laboratorio
                    </label>
                    <div class="equipos-container">
                        <div class="equipos-header">
                            <button type="button" id="btnAgregarEquipoEdit" class="btn-agregar-equipo">
                                <i class="fa-solid fa-plus"></i> Agregar Equipo
                            </button>
                        </div>
                        <div id="equiposSeleccionadosEdit" class="equipos-seleccionados">
                            <!-- Los equipos seleccionados se mostrarán aquí -->
                        </div>
                    </div>
                </div>

            </div>

            <div class="modal-footer">
                <button type="button" class="btn-cancelar" onclick="cerrarModalEditarReserva()">
                    <i class="fa-solid fa-times"></i> Cancelar
                </button>
                <button type="submit" class="btn-guardar">
                    <i class="fa-solid fa-save"></i> Guardar Cambios
                </button>
            </div>
        </form>
    </div>
</div>

<script src="js/usuario.js?v=1.0" defer></script>

<!-- Modal para crear reserva -->
<div id="modalCrearReserva" class="modal-overlay" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3><i class="fa-solid fa-calendar-plus"></i> Crear Nueva Reserva</h3>
            <button class="btn-cerrar-modal" onclick="cerrarModalCrearReserva()">
                <i class="fa-solid fa-times"></i>
            </button>
        </div>
        <form id="formCrearReserva" action="reserva_cliente" method="post">
            <div class="form-grid-usuario">

                <div class="form-group">
                    <label for="fecha">
                        <i class="fa-solid fa-calendar"></i> Fecha
                    </label>
                    <input type="date" id="fecha" name="fecha" required>
                </div>

                <div class="form-group">
                    <label for="horaInicio">
                        <i class="fa-solid fa-clock"></i> Hora Inicio
                    </label>
                    <input type="time" id="horaInicio" name="horaInicio" required>
                </div>

                <div class="form-group">
                    <label for="horaFin">
                        <i class="fa-solid fa-clock"></i> Hora Fin
                    </label>
                    <input type="time" id="horaFin" name="horaFin" required>
                </div>

                <div class="form-group">
                    <label for="cantidadParticipantes">
                        <i class="fa-solid fa-users"></i> Cantidad de Participantes
                    </label>
                    <input type="number" id="cantidadParticipantes" name="cantidadParticipantes"
                           min="1" max="10" required>
                </div>

                <div class="form-group equipos-section">
                    <label>
                        <i class="fa-solid fa-laptop"></i> Equipos de Laboratorio
                    </label>
                    <div class="equipos-container">
                        <div class="equipos-header">
                            <button type="button" id="btnAgregarEquipo" class="btn-agregar-equipo">
                                <i class="fa-solid fa-plus"></i> Agregar Equipo
                            </button>
                        </div>
                        <div id="equiposSeleccionados" class="equipos-seleccionados">
                            <!-- Los equipos seleccionados se mostrarán aquí -->
                        </div>
                    </div>
                </div>

            </div>

            <div class="modal-footer">
                <button type="button" class="btn-cancelar" onclick="cerrarModalCrearReserva()">
                    <i class="fa-solid fa-times"></i> Cancelar
                </button>
                <button type="submit" class="btn-guardar">
                    <i class="fa-solid fa-save"></i> Crear Reserva
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Modal para seleccionar equipos -->
<div id="modalSeleccionarEquipo" class="modal-overlay" style="display: none;">
    <div class="modal-content">
        <div class="modal-header">
            <h3><i class="fa-solid fa-laptop"></i> Seleccionar Equipo</h3>
            <button class="btn-cerrar-modal" onclick="cerrarModalSeleccionarEquipo()">
                <i class="fa-solid fa-times"></i>
            </button>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label for="selectEquipo">
                    <i class="fa-solid fa-laptop"></i> Equipo
                </label>
                <select id="selectEquipo" required>
                    <option value="">Seleccione un equipo...</option>
                </select>
            </div>
            <div class="form-group">
                <label for="usoEquipo">
                    <i class="fa-solid fa-comment"></i> ¿Para qué va a usar este equipo?
                </label>
                <textarea id="usoEquipo" placeholder="Describa el propósito de uso del equipo..."
                          rows="3" required></textarea>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn-cancelar" onclick="cerrarModalSeleccionarEquipo()">
                <i class="fa-solid fa-times"></i> Cancelar
            </button>
            <button type="button" class="btn-guardar" onclick="agregarEquipoSeleccionado()">
                <i class="fa-solid fa-plus"></i> Agregar Equipo
            </button>
        </div>
    </div>
</div>

<div id="modalConfirmLogout" class="modal-overlay" style="display:none;">
    <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="logoutTitle">
        <div class="modal-header">
            <h3 id="logoutTitle"><i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión</h3>
            <button class="btn-cerrar-modal" id="closeLogoutModal" aria-label="Cerrar">
                <i class="fa-solid fa-times"></i>
            </button>
        </div>
        <div class="modal-body">
            <p>¿Seguro que querés cerrar sesión? Vas a salir de ReserLab.</p>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn-cancelar" id="btnCancelLogout">
                <i class="fa-solid fa-times"></i> Cancelar
            </button>
            <button type="button" class="btn-guardar" id="btnConfirmLogout">
                <i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión
            </button>
        </div>
    </div>
</div>

<script>
    function mostrarModalCrearReserva() {
        document.getElementById('modalCrearReserva').style.display = 'flex';
        // Establecer fecha mínima como hoy
        const hoy = new Date().toISOString().split('T')[0];
        document.getElementById('fecha').setAttribute('min', hoy);

        // Inicializar la visualización de equipos
        actualizarEquiposSeleccionados();
    }

    function cerrarModalCrearReserva() {
        document.getElementById('modalCrearReserva').style.display = 'none';
        document.getElementById('formCrearReserva').reset();
    }

    // Cerrar modal al hacer clic fuera de él
    window.onclick = function (event) {
        const modal = document.getElementById('modalCrearReserva');
        if (event.target === modal) {
            cerrarModalCrearReserva();
        }
    }

    // Validar que hora fin sea mayor que hora inicio
    document.getElementById('formCrearReserva').addEventListener('submit', function (e) {
        const horaInicio = document.getElementById('horaInicio').value;
        const horaFin = document.getElementById('horaFin').value;

        if (horaInicio && horaFin && horaInicio >= horaFin) {
            e.preventDefault();
            alert('La hora de fin debe ser posterior a la hora de inicio');
            return;
        }

        // Agregar equipos seleccionados al formulario
        agregarEquiposAlFormulario();
    });

    // Variables globales para equipos
    let equiposDisponibles = [];
    let equiposSeleccionados = [];

    // Cargar equipos disponibles al cargar la página
    document.addEventListener('DOMContentLoaded', function () {
        cargarEquiposDisponibles();
    });

    // Función para cargar equipos desde el servidor
    async function cargarEquiposDisponibles() {
        try {
            console.log('=== CARGANDO EQUIPOS DESDE SERVIDOR ===');
            console.log('Solicitando equipos desde: equipos?formato=json');
            const response = await fetch('equipos?formato=json');
            console.log('Respuesta recibida:', response.status, response.statusText);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            console.log('Datos JSON recibidos:', data);
            console.log('Tipo de datos:', typeof data);
            console.log('Es array?:', Array.isArray(data));

            if (Array.isArray(data) && data.length > 0) {
                console.log('Primer equipo:', data[0]);
                console.log('Propiedades del primer equipo:', Object.keys(data[0]));
            }

            equiposDisponibles = data;
            console.log('Equipos cargados exitosamente:', equiposDisponibles.length, 'equipos');
            console.log('=== FIN CARGA DE EQUIPOS ===');
        } catch (error) {
            console.error('Error al cargar equipos:', error);
            alert('Error al cargar equipos: ' + error.message);
        }
    }

    // Mostrar modal para seleccionar equipo
    document.getElementById('btnAgregarEquipo').addEventListener('click', function () {
        mostrarModalSeleccionarEquipo();
    });

    function mostrarModalSeleccionarEquipo() {
        const modal = document.getElementById('modalSeleccionarEquipo');
        const select = document.getElementById('selectEquipo');

        console.log('Mostrando modal de equipos');
        console.log('Equipos disponibles:', equiposDisponibles);
        console.log('Equipos ya seleccionados:', equiposSeleccionados);

        // Limpiar opciones anteriores
        select.innerHTML = '<option value="">Seleccione un equipo...</option>';

        if (!equiposDisponibles || equiposDisponibles.length === 0) {
            console.warn('No hay equipos disponibles');
            const option = document.createElement('option');
            option.value = '';
            option.textContent = 'No hay equipos disponibles';
            select.appendChild(option);
        } else {
            // Agregar equipos disponibles (excluyendo los ya seleccionados)
            equiposDisponibles.forEach(equipo => {
                if (!equiposSeleccionados.some(e => e.id === equipo.id)) {
                    const option = document.createElement('option');
                    option.value = equipo.id;
                    option.textContent = equipo.nombre;
                    select.appendChild(option);
                }
            });
        }

        // Limpiar campos
        document.getElementById('usoEquipo').value = '';

        modal.style.display = 'flex';
    }

    function cerrarModalSeleccionarEquipo() {
        document.getElementById('modalSeleccionarEquipo').style.display = 'none';
    }

    function agregarEquipoSeleccionado() {
        const select = document.getElementById('selectEquipo');
        const uso = document.getElementById('usoEquipo').value.trim();

        console.log('=== AGREGANDO EQUIPO ===');
        console.log('Valor seleccionado:', select.value);
        console.log('Uso ingresado:', uso);

        if (!select.value) {
            alert('Por favor seleccione un equipo');
            return;
        }

        if (!uso) {
            alert('Por favor describa para qué va a usar el equipo');
            return;
        }

        const equipoId = parseInt(select.value);
        console.log('ID del equipo a buscar:', equipoId);
        console.log('Equipos disponibles:', equiposDisponibles);

        const equipo = equiposDisponibles.find(e => e.id === equipoId);
        console.log('Equipo encontrado:', equipo);

        if (equipo) {
            const nuevoEquipo = {
                id: equipo.id,
                nombre: equipo.nombre,
                tipo: equipo.tipo || 'Sin tipo especificado',
                uso: uso
            };

            console.log('Equipo a agregar:', nuevoEquipo);
            equiposSeleccionados.push(nuevoEquipo);

            console.log('Equipo agregado a la lista');
            console.log('Lista completa de equipos seleccionados:', equiposSeleccionados);

            actualizarEquiposSeleccionados();
            cerrarModalSeleccionarEquipo();
        } else {
            console.error('ERROR: No se encontró el equipo con ID:', equipoId);
            alert('Error: No se pudo encontrar el equipo seleccionado');
        }
    }

    function actualizarEquiposSeleccionados() {
        console.log('=== ACTUALIZANDO EQUIPOS SELECCIONADOS ===');
        const container = document.getElementById('equiposSeleccionados');

        if (!container) {
            console.error('ERROR: No se encontró el contenedor equiposSeleccionados');
            return;
        }

        console.log('Contenedor encontrado:', container);
        console.log('Número de equipos seleccionados:', equiposSeleccionados.length);
        console.log('Equipos:', equiposSeleccionados);

        container.innerHTML = '';

        if (equiposSeleccionados.length === 0) {
            container.innerHTML = '<p style="color: #666; font-size: 14px; text-align: center; padding: 10px; margin: 0;">No hay equipos agregados</p>';
            console.log('Mostrando mensaje de "No hay equipos"');
            return;
        }

        equiposSeleccionados.forEach((equipo, index) => {
            console.log(`Agregando equipo ${index + 1}:`, equipo);

            const equipoDiv = document.createElement('div');
            equipoDiv.className = 'equipo-seleccionado';

            // Crear elementos de forma segura
            const equipoInfo = document.createElement('div');
            equipoInfo.className = 'equipo-info';

            const equipoNombre = document.createElement('div');
            equipoNombre.className = 'equipo-nombre';
            equipoNombre.textContent = equipo.nombre || 'Sin nombre';

            const equipoTipo = document.createElement('div');
            equipoTipo.className = 'equipo-tipo';
            equipoTipo.textContent = equipo.tipo || 'Sin tipo especificado';

            const equipoUso = document.createElement('div');
            equipoUso.className = 'equipo-uso';
            equipoUso.textContent = 'Uso: ' + (equipo.uso || 'No especificado');

            equipoInfo.appendChild(equipoNombre);
            equipoInfo.appendChild(equipoTipo);
            equipoInfo.appendChild(equipoUso);

            const btnEliminar = document.createElement('button');
            btnEliminar.type = 'button';
            btnEliminar.className = 'btn-eliminar-equipo';
            btnEliminar.onclick = function () {
                eliminarEquipo(index);
            };
            btnEliminar.innerHTML = '<i class="fa-solid fa-trash"></i>';

            equipoDiv.appendChild(equipoInfo);
            equipoDiv.appendChild(btnEliminar);
            container.appendChild(equipoDiv);

            console.log('Equipo agregado al DOM');
        });

        console.log('HTML final del contenedor:', container.innerHTML);
        console.log('=== FIN ACTUALIZACIÓN ===');
    }

    function eliminarEquipo(index) {
        equiposSeleccionados.splice(index, 1);
        actualizarEquiposSeleccionados();
    }

    function agregarEquiposAlFormulario() {
        // Eliminar inputs de equipos anteriores
        const inputsAnteriores = document.querySelectorAll('input[name="equiposIds"], input[name="equiposUsos"]');
        inputsAnteriores.forEach(input => input.remove());

        // Agregar inputs para cada equipo seleccionado
        equiposSeleccionados.forEach(equipo => {
            const inputId = document.createElement('input');
            inputId.type = 'hidden';
            inputId.name = 'equiposIds';
            inputId.value = equipo.id;

            const inputUso = document.createElement('input');
            inputUso.type = 'hidden';
            inputUso.name = 'equiposUsos';
            inputUso.value = equipo.uso;

            document.getElementById('formCrearReserva').appendChild(inputId);
            document.getElementById('formCrearReserva').appendChild(inputUso);
        });
    }

    // Cerrar modal al hacer clic fuera de él
    window.addEventListener('click', function (event) {
        const modalEquipos = document.getElementById('modalSeleccionarEquipo');
        if (event.target === modalEquipos) {
            cerrarModalSeleccionarEquipo();
        }
    });

    // ======= FILTROS DE BÚSQUEDA =======

    // Función para aplicar filtros
    function aplicarFiltros() {
        const filtroFecha = document.getElementById('filtroFecha').value;
        const filtroEstado = document.getElementById('filtroEstado').value.toLowerCase();
        const tarjetas = document.querySelectorAll('.tarjeta-reserva');

        let contadorVisible = 0;

        tarjetas.forEach(tarjeta => {
            const fechaTarjeta = tarjeta.getAttribute('data-fecha');
            const estadoTarjeta = tarjeta.getAttribute('data-estado');

            let mostrarPorFecha = true;
            let mostrarPorEstado = true;

            // Filtro por fecha
            if (filtroFecha && fechaTarjeta !== filtroFecha) {
                mostrarPorFecha = false;
            }

            // Filtro por estado
            if (filtroEstado && estadoTarjeta !== filtroEstado) {
                mostrarPorEstado = false;
            }

            // Mostrar u ocultar tarjeta
            if (mostrarPorFecha && mostrarPorEstado) {
                tarjeta.style.display = 'block';
                contadorVisible++;
            } else {
                tarjeta.style.display = 'none';
            }
        });

        // Mostrar mensaje si no hay resultados
        const gridReservas = document.getElementById('gridReservas');
        let mensajeSinResultados = document.getElementById('mensajeSinResultados');

        if (contadorVisible === 0) {
            if (!mensajeSinResultados) {
                mensajeSinResultados = document.createElement('div');
                mensajeSinResultados.id = 'mensajeSinResultados';
                mensajeSinResultados.className = 'sin-reservas';
                mensajeSinResultados.innerHTML = `
                    <i class="fa-solid fa-filter-circle-xmark"></i>
                    <h3>No se encontraron reservas</h3>
                    <p>No hay reservas que coincidan con los filtros seleccionados.</p>
                `;
                if (gridReservas) {
                    gridReservas.parentNode.insertBefore(mensajeSinResultados, gridReservas.nextSibling);
                }
            }
            if (gridReservas) gridReservas.style.display = 'none';
        } else {
            if (mensajeSinResultados) {
                mensajeSinResultados.remove();
            }
            if (gridReservas) gridReservas.style.display = 'grid';
        }
    }

    // Función para limpiar el filtro de fecha
    function limpiarFiltroFecha() {
        document.getElementById('filtroFecha').value = '';
        aplicarFiltros();
    }

    // ======= EDITAR RESERVA =======

    let equiposSeleccionadosEdit = [];

    // Override the global function for this page
    window.editarReserva = async function (idActividad) {
        console.log('=== EDITAR RESERVA LLAMADO ===');
        console.log('ID de actividad:', idActividad);

        try {
            // Obtener datos de la reserva
            console.log('Solicitando datos de reserva...');
            const response = await fetch('editar-reserva?id=' + idActividad);
            console.log('Respuesta recibida:', response.status);

            if (!response.ok) {
                throw new Error('No se pudo cargar la información de la reserva');
            }

            const reserva = await response.json();
            console.log('Datos de reserva:', reserva);

            // Llenar el formulario
            document.getElementById('editIdActividad').value = reserva.id;
            document.getElementById('editFecha').value = reserva.fecha;
            document.getElementById('editHoraInicio').value = reserva.horaInicio;
            document.getElementById('editHoraFin').value = reserva.horaFin;
            document.getElementById('editCantidadParticipantes').value = reserva.cantidadParticipantes;

            // Cargar equipos seleccionados
            equiposSeleccionadosEdit = reserva.equipos || [];
            console.log('Equipos cargados:', equiposSeleccionadosEdit);
            actualizarEquiposSeleccionadosEdit();

            // Mostrar modal
            document.getElementById('modalEditarReserva').style.display = 'flex';
            console.log('Modal mostrado');

            // Establecer fecha mínima como hoy
            const hoy = new Date().toISOString().split('T')[0];
            document.getElementById('editFecha').setAttribute('min', hoy);

        } catch (error) {
            console.error('Error al cargar reserva:', error);
            alert('Error al cargar la información de la reserva: ' + error.message);
        }
    };

    function cerrarModalEditarReserva() {
        document.getElementById('modalEditarReserva').style.display = 'none';
        document.getElementById('formEditarReserva').reset();
        equiposSeleccionadosEdit = [];
    }

    // Validar formulario de edición
    document.addEventListener('DOMContentLoaded', function () {
        const formEdit = document.getElementById('formEditarReserva');
        if (formEdit) {
            formEdit.addEventListener('submit', function (e) {
                const horaInicio = document.getElementById('editHoraInicio').value;
                const horaFin = document.getElementById('editHoraFin').value;

                if (horaInicio && horaFin && horaInicio >= horaFin) {
                    e.preventDefault();
                    alert('La hora de fin debe ser posterior a la hora de inicio');
                    return;
                }

                // Agregar equipos seleccionados al formulario
                agregarEquiposAlFormularioEdit();
            });
        }
    });

    function actualizarEquiposSeleccionadosEdit() {
        const container = document.getElementById('equiposSeleccionadosEdit');
        if (!container) return;

        container.innerHTML = '';

        if (equiposSeleccionadosEdit.length === 0) {
            container.innerHTML = '<p style="color: #666; font-size: 14px; text-align: center; padding: 10px; margin: 0;">No hay equipos agregados</p>';
            return;
        }

        equiposSeleccionadosEdit.forEach((equipo, index) => {
            const equipoDiv = document.createElement('div');
            equipoDiv.className = 'equipo-seleccionado';

            const equipoInfo = document.createElement('div');
            equipoInfo.className = 'equipo-info';

            const equipoNombre = document.createElement('div');
            equipoNombre.className = 'equipo-nombre';
            equipoNombre.textContent = equipo.nombre || 'Sin nombre';

            const equipoTipo = document.createElement('div');
            equipoTipo.className = 'equipo-tipo';
            equipoTipo.textContent = equipo.tipo || 'Sin tipo especificado';

            const equipoUso = document.createElement('div');
            equipoUso.className = 'equipo-uso';
            equipoUso.textContent = 'Uso: ' + (equipo.uso || 'No especificado');

            equipoInfo.appendChild(equipoNombre);
            equipoInfo.appendChild(equipoTipo);
            equipoInfo.appendChild(equipoUso);

            const btnEliminar = document.createElement('button');
            btnEliminar.type = 'button';
            btnEliminar.className = 'btn-eliminar-equipo';
            btnEliminar.onclick = function () {
                eliminarEquipoEdit(index);
            };
            btnEliminar.innerHTML = '<i class="fa-solid fa-trash"></i>';

            equipoDiv.appendChild(equipoInfo);
            equipoDiv.appendChild(btnEliminar);
            container.appendChild(equipoDiv);
        });
    }

    function eliminarEquipoEdit(index) {
        equiposSeleccionadosEdit.splice(index, 1);
        actualizarEquiposSeleccionadosEdit();
    }

    function agregarEquiposAlFormularioEdit() {
        // Eliminar inputs de equipos anteriores
        const inputsAnteriores = document.querySelectorAll('#formEditarReserva input[name="equiposIds"], #formEditarReserva input[name="equiposUsos"]');
        inputsAnteriores.forEach(input => input.remove());

        // Agregar inputs para cada equipo seleccionado
        equiposSeleccionadosEdit.forEach(equipo => {
            const inputId = document.createElement('input');
            inputId.type = 'hidden';
            inputId.name = 'equiposIds';
            inputId.value = equipo.id;

            const inputUso = document.createElement('input');
            inputUso.type = 'hidden';
            inputUso.name = 'equiposUsos';
            inputUso.value = equipo.uso;

            document.getElementById('formEditarReserva').appendChild(inputId);
            document.getElementById('formEditarReserva').appendChild(inputUso);
        });
    }

    // Agregar equipo en modo edición
    document.addEventListener('DOMContentLoaded', function () {
        const btnAgregarEquipoEdit = document.getElementById('btnAgregarEquipoEdit');
        if (btnAgregarEquipoEdit) {
            btnAgregarEquipoEdit.addEventListener('click', function () {
                mostrarModalSeleccionarEquipoEdit();
            });
        }
    });

    function mostrarModalSeleccionarEquipoEdit() {
        const modal = document.getElementById('modalSeleccionarEquipo');
        const select = document.getElementById('selectEquipo');

        // Limpiar opciones anteriores
        select.innerHTML = '<option value="">Seleccione un equipo...</option>';

        if (!equiposDisponibles || equiposDisponibles.length === 0) {
            const option = document.createElement('option');
            option.value = '';
            option.textContent = 'No hay equipos disponibles';
            select.appendChild(option);
        } else {
            // Agregar equipos disponibles (excluyendo los ya seleccionados)
            equiposDisponibles.forEach(equipo => {
                if (!equiposSeleccionadosEdit.some(e => e.id === equipo.id)) {
                    const option = document.createElement('option');
                    option.value = equipo.id;
                    option.textContent = equipo.nombre;
                    select.appendChild(option);
                }
            });
        }

        // Limpiar campos
        document.getElementById('usoEquipo').value = '';

        // Cambiar el comportamiento del botón de agregar
        const btnAgregarEquipo = document.querySelector('#modalSeleccionarEquipo .btn-guardar');
        btnAgregarEquipo.onclick = agregarEquipoSeleccionadoEdit;

        modal.style.display = 'flex';
    }

    function agregarEquipoSeleccionadoEdit() {
        const select = document.getElementById('selectEquipo');
        const uso = document.getElementById('usoEquipo').value.trim();

        if (!select.value) {
            alert('Por favor seleccione un equipo');
            return;
        }

        if (!uso) {
            alert('Por favor describa para qué va a usar el equipo');
            return;
        }

        const equipoId = parseInt(select.value);
        const equipo = equiposDisponibles.find(e => e.id === equipoId);

        if (equipo) {
            const nuevoEquipo = {
                id: equipo.id,
                nombre: equipo.nombre,
                tipo: equipo.tipo || 'Sin tipo especificado',
                uso: uso
            };

            equiposSeleccionadosEdit.push(nuevoEquipo);
            actualizarEquiposSeleccionadosEdit();
            cerrarModalSeleccionarEquipo();
        } else {
            alert('Error: No se pudo encontrar el equipo seleccionado');
        }
    }

    // Event listeners para los filtros
    document.addEventListener('DOMContentLoaded', function () {
        const filtroFecha = document.getElementById('filtroFecha');
        const filtroEstado = document.getElementById('filtroEstado');

        if (filtroFecha) {
            filtroFecha.addEventListener('change', aplicarFiltros);
        }

        if (filtroEstado) {
            filtroEstado.addEventListener('change', aplicarFiltros);
        }
    });

    // ==== MODO OSCURO ====
    (function initTemaOnce() {
        if (window.__temaInitDone) return;
        window.__temaInitDone = true;

        const body = document.body;
        const btn = document.getElementById('btnModoOscuro');

        // Aplicar preferencia guardada
        try {
            const temaGuardado = localStorage.getItem('temaUsuario');
            if (temaGuardado) body.dataset.theme = temaGuardado;
        } catch (_) {
        }

        // Actualizar label/icono
        function actualizarIcono() {
            if (!btn) return;
            if (body.dataset.theme === 'dark') {
                btn.innerHTML = '<i class="fa-solid fa-sun"></i> Modo claro';
            } else {
                btn.innerHTML = '<i class="fa-solid fa-moon"></i> Modo oscuro';
            }
        }

        actualizarIcono();

        // Toggle
        if (btn && !btn.__hooked) {
            btn.addEventListener('click', () => {
                const nuevo = body.dataset.theme === 'dark' ? 'light' : 'dark';
                body.dataset.theme = nuevo;
                try {
                    localStorage.setItem('temaUsuario', nuevo);
                } catch (_) {
                }
                actualizarIcono();
            });
            btn.__hooked = true;
        }
    })();

    // ==== MODAL LOGOUT (interceptar todos los forms.logout) ====
    (function initLogoutModalOnce() {
        if (window.__logoutInitDone) return;
        window.__logoutInitDone = true;

        const modal = document.getElementById('modalConfirmLogout');
        if (!modal) return;

        const btnConfirm = document.getElementById('btnConfirmLogout');
        const btnCancel = document.getElementById('btnCancelLogout');
        const btnCloseX = document.getElementById('closeLogoutModal');
        let lastLogoutForm = null;

        // Interceptar submit de TODOS los formularios .logout
        document.querySelectorAll('form.logout').forEach(form => {
            if (form.__hasLogoutHook) return;
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                lastLogoutForm = form;
                abrirModal();
            });
            form.__hasLogoutHook = true;
        });

        function abrirModal() {
            modal.style.display = 'flex';
            setTimeout(() => btnConfirm && btnConfirm.focus(), 0);
        }

        function cerrarModal() {
            modal.style.display = 'none';
        }

        if (btnConfirm && !btnConfirm.__hooked) {
            btnConfirm.addEventListener('click', () => {
                cerrarModal();
                if (lastLogoutForm) lastLogoutForm.submit();
            });
            btnConfirm.__hooked = true;
        }
        if (btnCancel && !btnCancel.__hooked) {
            btnCancel.addEventListener('click', cerrarModal);
            btnCancel.__hooked = true;
        }
        if (btnCloseX && !btnCloseX.__hooked) {
            btnCloseX.addEventListener('click', cerrarModal);
            btnCloseX.__hooked = true;
        }

        // Cerrar clic fuera
        modal.addEventListener('click', (e) => {
            if (e.target === modal) cerrarModal();
        });
        // Cerrar con Escape
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && modal.style.display !== 'none') cerrarModal();
        });
    })();
</script>

</body>
</html>
