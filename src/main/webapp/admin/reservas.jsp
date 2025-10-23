<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Models.Actividad" %>
<div class="contenido-reservas">
    <div class="header-reservas">
        <h2 class="titulo-seccion">Reservas</h2>
        <div class="controles-reservas">
            <form class="filtro-fecha" method="get" action="reservas">
                <label for="fechaFiltro">
                    <i class="fa-solid fa-filter"></i> Filtrar por fecha:
                </label>
                <input type="date" id="fechaFiltro" name="fecha" value="<%= request.getParameter("fecha") != null ? request.getParameter("fecha") : "" %>">
                <button type="submit" class="btn-filtrar">
                    <i class="fa-solid fa-search"></i> Buscar
                </button>
                <% if (request.getParameter("fecha") != null && !request.getParameter("fecha").isEmpty()) { %>
                <a href="reservas" class="btn-limpiar">
                    <i class="fa-solid fa-times"></i> Limpiar
                </a>
                <% } %>
            </form>
            <button class="btn-crear-reserva" onclick="mostrarModalCrearReserva()">
                <i class="fa-solid fa-plus"></i> Crear Reserva
            </button>
        </div>
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

    <%
        List<Actividad> reservas = (List<Actividad>) request.getAttribute("reservas");
        Integer currentPage = (Integer) request.getAttribute("currentPage");
        Integer totalPages = (Integer) request.getAttribute("totalPages");
        Integer totalReservas = (Integer) request.getAttribute("totalReservas");
        Boolean hasNextPage = (Boolean) request.getAttribute("hasNextPage");
        Boolean hasPrevPage = (Boolean) request.getAttribute("hasPrevPage");

        if (reservas != null && !reservas.isEmpty()) {
    %>
    <div class="grid-reservas">
        <%
            for (Actividad reserva : reservas) {
        %>
        <div class="ficha-reserva">
            <div class="imagen-reserva">
                <img src="imagenes/logo.png" alt="Logo ReserLab" class="logo-ficha">
            </div>
            <div class="info-reserva">
                <div class="horario">
                    <i class="fa-solid fa-clock"></i>
                    <span><%= reserva.getHoraInicio() %> - <%= reserva.getHoraFin() %></span>
                </div>
                <div class="estado estado-<%= reserva.getEstado().toLowerCase() %>">
                    <i class="fa-solid fa-circle"></i>
                    <span><%= reserva.getEstado() %></span>
                </div>
                <div class="cliente">
                    <i class="fa-solid fa-user"></i>
                    <span><%= reserva.getNombreCliente() %></span>
                </div>
                <div class="participantes">
                    <i class="fa-solid fa-users"></i>
                    <span><%= reserva.getCantidadParticipantes() %> participantes</span>
                </div>
                <div class="fecha">
                    <i class="fa-solid fa-calendar"></i>
                    <span><%= reserva.getFecha() %></span>
                </div>
            </div>

            <!-- Botones de cambio de estado -->
            <div class="acciones-reserva">
                <%
                    if ("en_espera".equals(reserva.getEstado())) {
                %>
                <form method="post" action="cambiar-estado" style="display: inline;">
                    <input type="hidden" name="idActividad" value="<%= reserva.getIdActividad() %>">
                    <input type="hidden" name="nuevoEstado" value="aprobada">
                    <input type="hidden" name="page" value="<%= currentPage != null ? currentPage : 1 %>">
                    <button type="submit" class="btn-aprobar">
                        <i class="fa-solid fa-check"></i> Aprobar
                    </button>
                </form>
                <form method="post" action="cambiar-estado" style="display: inline;">
                    <input type="hidden" name="idActividad" value="<%= reserva.getIdActividad() %>">
                    <input type="hidden" name="nuevoEstado" value="rechazada">
                    <input type="hidden" name="page" value="<%= currentPage != null ? currentPage : 1 %>">
                    <button type="submit" class="btn-rechazar">
                        <i class="fa-solid fa-times"></i> Rechazar
                    </button>
                </form>
                <%
                } else {
                %>
                <div class="estado-final">
                    <i class="fa-solid fa-<%= "aprobada".equals(reserva.getEstado()) ? "check-circle" : "times-circle" %>"></i>
                    <span>Estado: <%= reserva.getEstado() %></span>
                </div>
                <%
                    }
                %>
            </div>

        </div>
        <%
            }
        %>
    </div>

    <!-- Paginación -->
    <%
        if (totalPages != null && totalPages > 1) {
            String fechaParam = request.getParameter("fecha");
            String fechaQuery = (fechaParam != null && !fechaParam.isEmpty()) ? "&fecha=" + fechaParam : "";
    %>
    <div class="paginacion">
        <%
            if (hasPrevPage != null && hasPrevPage) {
        %>
        <a href="reservas?page=<%= currentPage - 1 %><%= fechaQuery %>" class="btn-paginacion">
            <i class="fa-solid fa-chevron-left"></i> Anterior
        </a>
        <%
            }
        %>

        <span class="info-paginacion">
            Página <%= currentPage %> de <%= totalPages %> (<%= totalReservas %> reservas)
        </span>

        <%
            if (hasNextPage != null && hasNextPage) {
        %>
        <a href="reservas?page=<%= currentPage + 1 %><%= fechaQuery %>" class="btn-paginacion">
            Siguiente <i class="fa-solid fa-chevron-right"></i>
        </a>
        <%
            }
        %>
    </div>
    <%
        }
    %>
    <%
    } else {
    %>
    <div class="sin-reservas">
        <i class="fa-solid fa-calendar-xmark"></i>
        <h3>No hay reservas disponibles</h3>
        <p>No se encontraron reservas en el sistema.</p>
    </div>
    <%
        }
    %>
</div>

<!-- Modal para crear reserva -->
<div id="modalCrearReserva" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3><i class="fa-solid fa-calendar-plus"></i> Crear Nueva Reserva</h3>
            <button class="btn-cerrar-modal" onclick="cerrarModalCrearReserva()">
                <i class="fa-solid fa-times"></i>
            </button>
        </div>
        <form id="formCrearReserva" action="crear-reserva" method="post">
            <div class="form-grid">
                <div class="form-group">
                    <label for="cedulaCliente">
                        <i class="fa-solid fa-user"></i> Cédula del Cliente
                    </label>
                    <input type="text" id="cedulaCliente" name="cedulaCliente" 
                           placeholder="Ej: 12345678" required pattern="[0-9]{8}">
                    <small>Debe ser un cliente registrado en el sistema</small>
                </div>

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

                <div class="form-group">
                    <label for="estado">
                        <i class="fa-solid fa-circle-check"></i> Estado Inicial
                    </label>
                    <select id="estado" name="estado" required>
                        <option value="en_espera">En Espera</option>
                        <option value="aceptada" selected>Aprobada</option>
                        <option value="rechazada">Rechazada</option>
                    </select>
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
