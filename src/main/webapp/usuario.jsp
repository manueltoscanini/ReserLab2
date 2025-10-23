<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Interfaz Usuario - ReserLab</title>
    <link rel="stylesheet" href="estilos/usuario.css?v=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

<div class="contenedorPrincipal">
    <aside class="barraLateral">
        <div class="perfil">
            <div class="foto-perfil-container">
                <% if (fotoUsuario != null && !fotoUsuario.isEmpty()) { %>
                    <img src="<%= fotoUsuario %>" alt="Foto de perfil" class="fotoPerfil" id="fotoPerfil">
                <% } else { %>
                    <i class="fa-solid fa-user-circle iconoPerfil" id="iconoPerfil"></i>
                <% } %>
                <button class="btn-cambiar-foto" id="btnCambiarFoto" title="Cambiar foto">
                    <i class="fa-solid fa-camera"></i>
                </button>
                <input type="file" id="inputFoto" accept="image/*" style="display: none;">
            </div>
            <h2 class="nombreUsuario"><%= nombreUsuario %></h2>
        </div>

        <nav class="menu">
            <button id="opciones-perfil"><i class="fa-solid fa-user"></i> Perfil</button>
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

    <!-- Popup Perfil -->
    <div id="popupPerfil" class="popup oculto">
        <div class="flechaAzul"></div>
        <div class="rectAzul">
            <div class="submenu-vertical">
                <button id="btnVerPerfil" class="btnVerde">Ver perfil</button>
                <button id="btnCambiarDatos" class="btnVerde">Cambiar mis datos</button>
                <button id="btnCambiarContraseña" class="btnVerde">Cambiar contraseña</button>
                <button id="btnEliminarCuenta" class="btnVerde">Eliminar cuenta</button>
            </div>
        </div>
    </div>

    <!-- Popup Reservas -->
    <div id="popupReservas" class="popup oculto">
        <div class="flechaAzul"></div>
        <div class="rectAzul">
            <div class="submenu-vertical">
                <button id="btnHacerReserva" class="btnVerde">Hacer reserva</button>
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
        <!-- Contenido principal -->
    </main>
</div>

<script src="js/usuario.js?v=1.0" defer></script>


</body>
</html>
