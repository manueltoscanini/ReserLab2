<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String fotoUsuario = (String) session.getAttribute("fotoUsuario");
    String section = request.getParameter("section");
    
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
    <style>
    /* Toasts */
    #toast-container { position: fixed; top: 16px; right: 16px; z-index: 9999; display: flex; flex-direction: column; gap: 10px; }
    .toast { min-width: 260px; max-width: 360px; padding: 12px 14px; border-radius: 10px; box-shadow: 0 10px 20px rgba(16,24,40,.1), 0 6px 6px rgba(16,24,40,.06); color: #0b0f18; display:flex; gap:10px; align-items:flex-start; background:#fff; border:1px solid #eaecf0; opacity: 0; transform: translateY(-8px); transition: opacity .2s ease, transform .2s ease; }
    .toast.show { opacity: 1; transform: translateY(0); }
    .toast .icon { font-size: 18px; margin-top: 2px; }
    .toast .content { flex:1; }
    .toast .title { font-weight: 600; margin-bottom: 2px; }
    .toast .message { font-size: 14px; color: #475467; }
    .toast.success { border-color: #12b76a; }
    .toast.success .icon { color: #12b76a; }
    .toast.error { border-color: #f04438; }
    .toast.error .icon { color: #f04438; }
    .toast.info { border-color: #2e90fa; }
    .toast.info .icon { color: #2e90fa; }
    .toast .close { background: transparent; border: 0; cursor: pointer; color: #98a2b3; }

    /* Espaciado general de modales */
    .modal .modal-content { padding: 24px; border-radius: 12px; }
    .modal .modal-header { margin-bottom: 16px; }
    .modal .modal-body { margin: 8px 0 12px 0; }
    .modal .modal-footer { margin-top: 16px; padding-top: 16px; display:flex; gap:12px; justify-content: flex-end; }
    </style>
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
                <h2 class="nombreUsuario">Administrador</h2>
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
            <button type="submit"><i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión</button>
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
</script>
</body>
</html>