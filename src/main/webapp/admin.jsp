<%@ page import="DAO.UsuarioDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String fotoUsuario = (String) session.getAttribute("fotoUsuario");
    String section = request.getParameter("section");
    String nombreAdmin = (String) session.getAttribute("nombreUsuario");

    // Si no hay section pero hay datos de reservas, significa que viene del servlet
    if (section == null && request.getAttribute("reservas") != null) {
        section = "reservas";
    }
    
    // Si no hay section pero hay datos de usuarios, significa que viene del servlet
    if (section == null && request.getAttribute("usuarios") != null) {
        section = "usuarios";
    }
    
    // Si no hay section pero hay datos de equipos, significa que viene del servlet
    if (section == null && request.getAttribute("equipos") != null) {
        section = "equipos";
    }
    
    if (section == null) section = "reservas";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Interfaz Admin - ReserLab</title>
    <link rel="stylesheet" href="estilos/usuario2.css?v=1.2">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="estilos/admin.css?v=1.2">
</head>
<body data-context="<%= request.getContextPath() %>">

<div id="toast-container"></div>

<div class="contenedorPrincipal">
    <aside class="barraLateral">
        <a href="perfil-admin" class="perfil-link">
            <div class="perfil">
                <div class="foto-perfil-container">
                    <% if (fotoUsuario != null && !fotoUsuario.isEmpty()) { %>
                        <img src="<%= fotoUsuario %>" alt="Foto de perfil" class="fotoPerfil" id="fotoPerfil">
                    <% } else { %>
                        <i class="fa-solid fa-user-shield iconoPerfil" id="iconoPerfil"></i>
                    <% } %>
                    <button class="btn-cambiar-foto" id="btnCambiarFoto" title="Cambiar foto">
                        <i class="fa-solid fa-camera"></i>
                    </button>
                    <input type="file" id="inputFoto" accept="image/*" style="display: none;">
                </div>
                <h2 class="nombreUsuario">
                    ${empty sessionScope.nombreUsuario ? 'Administrador' : sessionScope.nombreUsuario}
                </h2>
            </div>
        </a>

        <nav class="menu">
            <a href="reservas" class="<%= "reservas".equals(section) ? "active" : "" %>">
                <button><i class="fa-solid fa-calendar-days"></i> Reservas</button>
            </a>
            <a href="usuarios" class="<%= "usuarios".equals(section) ? "active" : "" %>">
                <button><i class="fa-solid fa-users"></i> Usuarios</button>
            </a>
            <a href="equipos" class="<%= "equipos".equals(section) ? "active" : "" %>">
                <button><i class="fa-solid fa-laptop"></i> Equipos</button>
            </a>
            <a href="admin.jsp?section=carreras" class="<%= "carreras".equals(section) ? "active" : "" %>">
                <button><i class="fa-solid fa-graduation-cap"></i> Carreras</button>
            </a>
        </nav>

        <form class="logout" action="${pageContext.request.contextPath}/logout" method="post">
            <button id="botonCerrarSesion"type="submit"><i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión</button>
        </form>

    </aside>

    <main class="contenido">
        <% if ("reservas".equals(section)) { %>
            <jsp:include page="admin/reservas.jsp" />
        <% } else if ("usuarios".equals(section)) { %>
            <jsp:include page="admin/usuarios.jsp" />
        <% } else if ("equipos".equals(section)) { %>
            <jsp:include page="admin/equipos.jsp" />
        <% } else if ("carreras".equals(section)) { %>
            <jsp:include page="admin/carreras.jsp" />
        <% } %>
    </main>
</div>
<div id="modalConfirmLogout" class="modal" style="display:none;">
    <div class="modal-content" role="dialog" aria-modal="true" aria-labelledby="logoutTitle">
        <div class="modal-header">
            <h3 id="logoutTitle">
                <i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión
            </h3>
            <button class="btn-cerrar-modal" id="closeLogoutModal" aria-label="Cerrar">
                <i class="fa-solid fa-times"></i>
            </button>
        </div>

        <div class="modal-body">
            <p>¿Seguro que querés cerrar sesión?</p>
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
<!-- 1) defino el contexto global ANTES de cargar admin.js -->
<script>
    window.CTX = '<%= request.getContextPath() %>';
</script>

<!-- 2) ahora sí cargo el JS -->
<script src="<%= request.getContextPath() %>/js/admin.js?v=1.1" defer></script>

<!-- Toast global fijo -->
<div id="toast-reserlab" class="toast-reserlab"></div>

<script>
    window.mostrarToast = function (mensaje, esError = false) {
        const toast = document.getElementById('toast-reserlab');
        if (!toast) {
            console.log('toast no encontrado, muestro alert');
            alert(mensaje);
            return;
        }
        toast.textContent = mensaje;
        toast.className = 'toast-reserlab' + (esError ? ' error' : '');
        // forzar reflow
        void toast.offsetWidth;
        toast.classList.add('show');

        setTimeout(() => {
            toast.classList.remove('show');
        }, 2500);
    };

    document.addEventListener('DOMContentLoaded', function () {
        const modal = document.getElementById('modalConfirmLogout');
        if (!modal) return;

        const btnConfirm = document.getElementById('btnConfirmLogout');
        const btnCancel  = document.getElementById('btnCancelLogout');
        const btnCloseX  = document.getElementById('closeLogoutModal');

        let lastLogoutForm = null;

        // 1) Interceptar todos los formularios de logout
        document.querySelectorAll('form.logout').forEach(form => {
            form.addEventListener('submit', function (e) {
                e.preventDefault();
                lastLogoutForm = this;
                abrirModal();
            });
        });

        // 2) Acciones del modal
        function abrirModal() {
            modal.style.display = 'flex';
            // foco por accesibilidad
            setTimeout(() => btnConfirm && btnConfirm.focus(), 0);
        }
        function cerrarModal() {
            modal.style.display = 'none';
        }

        if (btnConfirm) {
            btnConfirm.addEventListener('click', function () {
                cerrarModal();
                if (lastLogoutForm) lastLogoutForm.submit();
            });
        }
        if (btnCancel) btnCancel.addEventListener('click', cerrarModal);
        if (btnCloseX) btnCloseX.addEventListener('click', cerrarModal);

        // Cerrar clickeando fuera del contenido
        modal.addEventListener('click', (e) => {
            if (e.target === modal) cerrarModal();
        });

        // Cerrar con Escape
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && modal.style.display !== 'none') cerrarModal();
        });
    });
</script>
</body>
</html>