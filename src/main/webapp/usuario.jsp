<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
            <i class="fa-solid fa-user-circle iconoPerfil"></i>
            <h2 class="nombreUsuario"><%= nombreUsuario %></h2>
        </div>

        <nav class="menu">
            <button id="opciones-reserva"><i class="fa-solid fa-calendar-days"></i> Reservas</button>
            <button><i class="fa-solid fa-laptop"></i> Equipos</button>
            <button><i class="fa-solid fa-ellipsis-h"></i> Otros</button>
            <button><i class="fa-solid fa-user"></i> Perfil</button>
        </nav>

        <!-- Panel desplegable para opciones de reserva -->
        <div id="submenu-reservas" class="submenu">
            <button>Hacer una reserva</button>
            <button>Mis reservas activas</button>
            <button>Historial de reservas</button>
        </div>

        <form class="logout" action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit"><i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión</button>
        </form>
    </aside>

    <div id="popupEquipos" class="popupEquipos oculto">
        <div class="flechaAzul"></div>
        <div class="rectAzul">
            <button id="btnListarEquipos" class="btnVerde">Listar Equipos</button>
        </div>
    </div>

    <main class="contenido">
        <!-- Contenido principal -->
    </main>
</div>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        const btnEquipos = document.querySelector('.menu button:nth-child(2)'); // segundo botón = Equipos
        const popup = document.getElementById('popupEquipos');
        const btnListar = document.getElementById('btnListarEquipos');
        const contenido = document.querySelector('.contenido');

        // Mostrar/ocultar el rectángulo azul al presionar "Equipos"
        btnEquipos.addEventListener('click', () => {
            popup.classList.toggle('oculto');
        });

        // Al presionar "Listar Equipos"
        btnListar.addEventListener('click', async () => {
            popup.classList.add('oculto'); // desaparece el popup

            // Carga la lista de equipos
            const resp = await fetch("EquiposServlet");
            const html = await resp.text();
            contenido.innerHTML = html;
        });
    });
</script>

<script src="js/usuario.js"></script>

</body>
</html>
