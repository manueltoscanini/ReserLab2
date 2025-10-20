<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Models.Usuario" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Perfil - ReserLab</title>
    <link rel="stylesheet" href="estilos/usuario2.css?v=1.1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

<div class="contenedorPrincipal">
    <aside class="barraLateral">
        <a href="perfil-admin" class="perfil-link">
            <div class="perfil">
                <i class="fa-solid fa-user-shield iconoPerfil"></i>
                <h2 class="nombreUsuario">Administrador</h2>
            </div>
        </a>

        <nav class="menu">
            <a href="reservas"><button><i class="fa-solid fa-calendar-days"></i> Reservas</button></a>
            <button><i class="fa-solid fa-users"></i> Usuarios</button>
            <button><i class="fa-solid fa-laptop"></i> Equipos</button>
            <button><i class="fa-solid fa-graduation-cap"></i> Carreras</button>
        </nav>

        <form class="logout" action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit"><i class="fa-solid fa-right-from-bracket"></i> Cerrar sesión</button>
        </form>

    </aside>

    <main class="contenido">
        <div class="contenido-perfil">
            <h2 class="titulo-seccion"><i class="fa-solid fa-user-circle"></i> Mi Perfil</h2>

            <%
                Usuario usuario = (Usuario) session.getAttribute("usuario");
                if (usuario == null) {
                    response.sendRedirect("login.jsp");
                    return;
                }

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

            <div class="perfil-card">
                <div class="perfil-header-card">
                    <i class="fa-solid fa-user-shield icono-perfil-grande"></i>
                    <h3>Información del Administrador</h3>
                </div>

                <form id="formPerfil" action="perfil-admin" method="post">
                    <div class="form-perfil">
                        <div class="form-row">
                            <div class="form-group">
                                <label for="nombre">
                                    <i class="fa-solid fa-user"></i> Nombre Completo
                                </label>
                                <input type="text" id="nombre" name="nombre" 
                                       value="<%= usuario.getNombre() %>" 
                                       readonly required>
                            </div>

                            <div class="form-group">
                                <label for="email">
                                    <i class="fa-solid fa-envelope"></i> Email
                                </label>
                                <input type="email" id="email" name="email" 
                                       value="<%= usuario.getEmail() %>" 
                                       readonly required>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="cedula">
                                    <i class="fa-solid fa-id-card"></i> Cédula
                                </label>
                                <input type="text" id="cedula" name="cedula" 
                                       value="<%= usuario.getCedula() %>" 
                                       readonly disabled>
                            </div>
                        </div>

                        <div class="form-row password-section" id="passwordSection" style="display: none;">
                            <div class="form-group password-group">
                                <label for="passwordActual">
                                    <i class="fa-solid fa-lock"></i> Contraseña Actual
                                </label>
                                <input type="password" id="passwordActual" name="passwordActual" 
                                       placeholder="Ingrese su contraseña actual">
                                <small>Requerido para cambiar la contraseña</small>
                            </div>

                            <div class="form-group password-group">
                                <label for="passwordNueva">
                                    <i class="fa-solid fa-key"></i> Nueva Contraseña
                                </label>
                                <input type="password" id="passwordNueva" name="passwordNueva" 
                                       placeholder="Ingrese la nueva contraseña">
                                <small>Dejar vacío si no desea cambiar</small>
                            </div>
                        </div>
                    </div>

                    <div class="perfil-actions">
                        <button type="button" id="btnEditar" class="btn-editar" onclick="habilitarEdicion()">
                            <i class="fa-solid fa-edit"></i> Editar
                        </button>
                        <button type="submit" id="btnGuardar" class="btn-guardar-perfil" style="display: none;">
                            <i class="fa-solid fa-save"></i> Guardar Cambios
                        </button>
                        <button type="button" id="btnCancelar" class="btn-cancelar-perfil" style="display: none;" onclick="cancelarEdicion()">
                            <i class="fa-solid fa-times"></i> Cancelar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>

<script>
    let valoresOriginales = {
        nombre: document.getElementById('nombre').value,
        email: document.getElementById('email').value
    };

    function habilitarEdicion() {
        // Habilitar campos editables
        document.getElementById('nombre').removeAttribute('readonly');
        document.getElementById('email').removeAttribute('readonly');
        
        // Mostrar sección de contraseña
        document.getElementById('passwordSection').style.display = 'block';
        
        // Cambiar botones
        document.getElementById('btnEditar').style.display = 'none';
        document.getElementById('btnGuardar').style.display = 'inline-flex';
        document.getElementById('btnCancelar').style.display = 'inline-flex';
        
        // Focus en el primer campo
        document.getElementById('nombre').focus();
    }

    function cancelarEdicion() {
        // Restaurar valores originales
        document.getElementById('nombre').value = valoresOriginales.nombre;
        document.getElementById('email').value = valoresOriginales.email;
        document.getElementById('passwordActual').value = '';
        document.getElementById('passwordNueva').value = '';
        
        // Deshabilitar campos
        document.getElementById('nombre').setAttribute('readonly', 'readonly');
        document.getElementById('email').setAttribute('readonly', 'readonly');
        
        // Ocultar sección de contraseña
        document.getElementById('passwordSection').style.display = 'none';
        
        // Cambiar botones
        document.getElementById('btnEditar').style.display = 'inline-flex';
        document.getElementById('btnGuardar').style.display = 'none';
        document.getElementById('btnCancelar').style.display = 'none';
    }

    // Validación del formulario
    document.getElementById('formPerfil').addEventListener('submit', function(e) {
        const passwordNueva = document.getElementById('passwordNueva').value;
        const passwordActual = document.getElementById('passwordActual').value;

        if (passwordNueva && !passwordActual) {
            e.preventDefault();
            alert('Debe ingresar la contraseña actual para cambiar la contraseña');
            document.getElementById('passwordActual').focus();
        }
    });
</script>

</body>
</html>
