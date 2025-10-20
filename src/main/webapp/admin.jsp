<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Models.Actividad" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Interfaz Admin - ReserLab</title>
    <link rel="stylesheet" href="estilos/usuario2.css?v=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

<div class="contenedorPrincipal">
    <aside class="barraLateral">
        <div class="perfil">
            <i class="fa-solid fa-user-shield iconoPerfil"></i>
            <h2 class="nombreUsuario">Administrador</h2>
        </div>

        <nav class="menu">
            <%
                String currentPageParam = request.getParameter("page");
                String requestURI = request.getRequestURI();
                boolean isReservasPage = requestURI.contains("reservas") || requestURI.contains("admin.jsp");
            %>
            <a href="reservas" class="<%= isReservasPage ? "active" : "" %>"><button><i class="fa-solid fa-calendar-days"></i> Reservas</button></a>
            <button><i class="fa-solid fa-users"></i> Usuarios</button>
            <button><i class="fa-solid fa-laptop"></i> Equipos</button>
            <button><i class="fa-solid fa-graduation-cap"></i> Carreras</button>
            <button><i class="fa-solid fa-user"></i> Perfil</button>
        </nav>

        <form class="logout" action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit"><i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión</button>
        </form>

    </aside>

    <main class="contenido">
        <div class="contenido-reservas">
            <h2 class="titulo-seccion">Reservas</h2>

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
            %>
            <div class="paginacion">
                <%
                    if (hasPrevPage != null && hasPrevPage) {
                %>
                <a href="reservas?page=<%= currentPage - 1 %>" class="btn-paginacion">
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
                <a href="reservas?page=<%= currentPage + 1 %>" class="btn-paginacion">
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
    </main>
</div>

</body>
</html>
@