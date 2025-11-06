<%-- login.jsp: --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión - ReserLab</title>
    <link rel="stylesheet" type="text/css" href="estilos/login.css?v=1.1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
<main>
    <div id="contenedorLogin">

        <h2>Iniciar sesión</h2>

        <form action="LoginServlet" method="post">
            <label for="email">Correo:</label>
            <input type="email" id="email" name="email" required><br>

            <label for="password">Contraseña:</label>
            <div class="password-container">
                <input type="password" id="password" name="password" required>
                <span class="eye-icon" onclick="togglePasswordVisibility()">
                    <i id="eyeIcon" class="fas fa-eye"></i>
                </span>
            </div><br>

            <input type="submit" value="Iniciar sesión">
        </form>

        <div class="btn-volver-container">
            <button type="button" class="btn-volver" onclick="window.location.href='index.jsp'">
                <i class="fa-solid fa-arrow-left"></i> Volver al inicio
            </button>
        </div>


        <!-- <p><a href="registro.jsp">¿No tienes cuenta? Regístrate</a></p> -->
    </div>
</main>

<!-- Script para mostrar alertas con SweetAlert -->
<script>
    // Función para obtener parámetros de la URL
    function getParameterByName(name, url = window.location.href) {
        name = name.replace(/[\[\]]/g, '\\$&');
        var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, ' '));
    }

    // Mostrar alertas con SweetAlert según los parámetros de la URL
    window.addEventListener('DOMContentLoaded', function() {
        var error = getParameterByName('error');
        var exito = getParameterByName('exito');
        var msg = getParameterByName('msg');
        
        if (error) {
            var errorMessage = '';
            if (error === 'credenciales') {
                errorMessage = 'Correo o contraseña incorrectos.';
            } else if (error === 'camposVacios') {
                errorMessage = 'Por favor, complete todos los campos.';
            } else if (error === 'servidor') {
                errorMessage = 'Error interno del servidor. Intente nuevamente más tarde.';
            } else {
                errorMessage = error; // Mensaje personalizado
            }
            
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: errorMessage,
                confirmButtonText: 'Aceptar'
            });
        }
        
        if (exito) {
            Swal.fire({
                icon: 'success',
                title: 'Éxito',
                text: exito,
                confirmButtonText: 'Aceptar'
            });
        }
        
        if (msg && msg === 'cuentaDesactivada') {
            Swal.fire({
                icon: 'error',
                title: 'Cuenta desactivada',
                text: 'Tu cuenta ha sido desactivada. Contacta al administrador.',
                confirmButtonText: 'Aceptar'
            });
        }
    });
</script>

<script>
    function togglePasswordVisibility() {
        const passwordInput = document.getElementById('password');
        const eyeIcon = document.getElementById('eyeIcon');
        
        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            eyeIcon.classList.remove('fa-eye');
            eyeIcon.classList.add('fa-eye-slash');
        } else {
            passwordInput.type = 'password';
            eyeIcon.classList.remove('fa-eye-slash');
            eyeIcon.classList.add('fa-eye');
        }
    }
</script>
</body>
</html>