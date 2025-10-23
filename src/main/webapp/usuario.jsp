<%--usuario.jsp: --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="DAO.ActividadDAO" %>
<%@ page import="Models.Actividad" %>
<%@ page import="Models.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    // Evita que se guarde en caché (por seguridad)
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    // Obtiene el nombre de usuario de la sesión
    String nombreUsuario = (String) session.getAttribute("nombreUsuario");

    // Si no hay usuario logueado, redirige al login
    if (nombreUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // Obtener el usuario completo de la sesión
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    
    // Cargar las reservas del usuario desde la base de datos
    List<Actividad> reservas = null;
    if (usuario != null) {
        ActividadDAO actividadDAO = new ActividadDAO();
        reservas = actividadDAO.historialReservasPorCi(usuario.getCedula());
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Interfaz Usuario - ReserLab</title>
    <link rel="stylesheet" href="estilos/usuario.css?v=1.0">
    <link rel="stylesheet" href="estilos/consultas.css?v=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

<div class="contenedorPrincipal">
    <aside class="barraLateral">
        <div class="perfil">
            <a href="#" id="opciones-perfil"><i class="fa-solid fa-user-circle iconoPerfil"></i></a>
            <h2 id="nombreUsuario" class="nombreUsuario"><%= nombreUsuario %></h2>
        </div>

        <nav class="menu">
            <button id="opciones-reserva"><i class="fa-solid fa-calendar-days"></i> Reservas</button>
            <button id="opciones-equipos"><i class="fa-solid fa-laptop"></i> Equipos</button>
            <button id="opciones-otros"><i class="fa-solid fa-ellipsis-h"></i> Otros</button>
        </nav>

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
            <div class="contenido-reservas">
                <div class="header-reservas">
                    <h2 class="titulo-seccion">Historial de Reservas completo</h2>
                    <button class="btn-crear-reserva" onclick="mostrarModalCrearReserva()">
                        <i class="fa-solid fa-plus"></i> Crear Reserva
                    </button>
                </div>

                <%-- Mostrar mensajes de éxito o error --%>
                <%
                String mensajeExito = (String) session.getAttribute("exito");
                String mensajeError = (String) session.getAttribute("error");

                if (mensajeExito != null) {
                    session.removeAttribute("exito");
            %>
            <div class="mensaje-exito">
                <i class="fa-solid fa-check-circle"></i>
                <%= mensajeExito %>
            </div>
                <%
                }

                if (mensajeError != null) {
                    session.removeAttribute("error");
            %>
            <div class="mensaje-error">
                <i class="fa-solid fa-exclamation-circle"></i>
                <%= mensajeError %>
            </div>
                <%
                }
            %>

            <%-- Sección de Todas las Reservas (Historial) --%>
            <div class="seccion-historial">

                <div id="contenedor-historial" class="grid-reservas">
                    <%
                        if (reservas == null || reservas.isEmpty()) {
                    %>
                    <div class="sin-reservas">
                        <i class="fa-solid fa-calendar-xmark"></i>
                        <p>No tienes reservas en tu historial</p>
                    </div>
                    <%
                        } else {
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            for (Actividad reserva : reservas) {
                                String fecha = reserva.getFecha().format(dateFormatter);
                                String horaInicio = reserva.getHoraInicio().toString().substring(0, 5);
                                String horaFin = reserva.getHoraFin().toString().substring(0, 5);
                                String estado = reserva.getEstado();
                                String estadoClass = "estado-" + estado.toLowerCase().replace("_", "-");
                    %>
                    <div class="tarjeta-reserva">
                        <div class="icono-reserva">
                            <img src="imagenes/logo.png" alt="Logo ReserLab" class="logo-ficha">
                        </div>
                        <div class="detalles-reserva">
                            <div class="detalle">
                                <i class="fa-solid fa-calendar-days"></i>
                                <span><%= fecha %></span>
                            </div>
                            <div class="detalle">
                                <i class="fa-solid fa-clock"></i>
                                <span><%= horaInicio %> - <%= horaFin %></span>
                            </div>
                            <div class="detalle">
                                <i class="fa-solid fa-bell"></i>
                                <span class="estado <%= estadoClass %>"><%= estado %></span>
                            </div>
                        </div>
                    </div>
                    <%
                            }
                        }
                    %>
                </div>
            </div>
        </div>
    </main>
</div>

<script src="js/usuario.js?v=1.0" defer></script>

<!-- Modal para crear reserva -->
<div id="modalCrearReserva" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3><i class="fa-solid fa-calendar-plus"></i> Crear Nueva Reserva</h3>
            <button class="btn-cerrar-modal" onclick="cerrarModalCrearReserva()">
                <i class="fa-solid fa-times"></i>
            </button>
        </div>
        <form id="formCrearReserva" action="reserva_cliente" method="post">
            <div class="form-grid">


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
                           min="1" max="50" required>
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

<script>
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

</script>

</body>
</html>
