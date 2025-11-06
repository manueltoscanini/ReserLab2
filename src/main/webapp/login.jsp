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

        <!-- Enlace para recuperar contraseña -->
        <div class="recuperar-container">
            <a href="#" id="recuperarLink">¿Olvidaste tu contraseña?</a>
        </div>

        <div class="btn-volver-container">
            <button type="button" class="btn-volver" onclick="window.location.href='index.jsp'">
                <i class="fa-solid fa-arrow-left"></i> Volver al inicio
            </button>
        </div>


        <!-- <p><a href="registro.jsp">¿No tienes cuenta? Regístrate</a></p> -->
    </div>
</main>

<!-- Modal para recuperar contraseña -->
<div id="recuperarModal" class="modal-overlay" style="display: none;">
    <div class="modal">
        <div class="modal-header">
            <h3><i class="fas fa-key"></i> Recuperar Contraseña</h3>
            <button class="btn-cerrar-modal" onclick="cerrarModalRecuperar()">&times;</button>
        </div>
        <div class="modal-body">
            <p>Ingrese su correo electrónico y le enviaremos un enlace para restablecer su contraseña.</p>
            <form id="recuperarForm">
                <div class="form-group">
                    <label for="emailRecuperar">Correo Electrónico:</label>
                    <input type="email" id="emailRecuperar" name="email" required>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn-cancelar" onclick="cerrarModalRecuperar()">Cancelar</button>
                    <button type="submit" class="btn-guardar" id="btnEnviarEnlace">Enviar Enlace</button>
                </div>
            </form>
        </div>
    </div>
</div>


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
    
    // Funciones para el modal de recuperación de contraseña
    function abrirModalRecuperar() {
        document.getElementById('recuperarModal').style.display = 'flex';
    }
    
    function cerrarModalRecuperar() {
        document.getElementById('recuperarModal').style.display = 'none';
    }
    
    // Funciones para el modal de credenciales incorrectas
    function abrirModalCredenciales() {
        document.getElementById('errorCredencialesModal').style.display = 'flex';
    }
    
    function cerrarModalCredenciales() {
        document.getElementById('errorCredencialesModal').style.display = 'none';
    }
    
    // Abrir modal al hacer clic en el enlace
    document.getElementById('recuperarLink').addEventListener('click', function(e) {
        e.preventDefault();
        abrirModalRecuperar();
    });
    
    // Cerrar modales al hacer clic fuera de ellos
    window.addEventListener('click', function(e) {
        const modalRecuperar = document.getElementById('recuperarModal');
        const modalCredenciales = document.getElementById('errorCredencialesModal');
        
        if (e.target === modalRecuperar) {
            cerrarModalRecuperar();
        }
        
        if (e.target === modalCredenciales) {
            cerrarModalCredenciales();
        }
    });
    
    // Mostrar modal de credenciales incorrectas si es necesario
    window.addEventListener('DOMContentLoaded', function() {
        <c:if test="${param.error == 'credenciales'}">
            abrirModalCredenciales();
        </c:if>
    });
    
    // Manejar el envío del formulario de recuperación
    document.getElementById('recuperarForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const emailInput = document.getElementById('emailRecuperar');
        const email = emailInput.value.trim();
        const btnEnviar = document.getElementById('btnEnviarEnlace');
        
        console.log("=== INICIO PROCESO RECUPERACIÓN ===");
        console.log("Email ingresado: '" + email + "'");
        
        // Validar que se haya ingresado un email
        if (!email) {
            alert('Por favor, ingrese su correo electrónico.');
            emailInput.focus();
            console.log("ERROR: Email vacío");
            return;
        }
        
        // Validar formato de email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            alert('Por favor, ingrese un correo electrónico válido.');
            emailInput.focus();
            console.log("ERROR: Formato de email inválido");
            return;
        }
        
        console.log("Email válido, procediendo con el envío");
        
        // Deshabilitar el botón y mostrar mensaje de carga
        btnEnviar.disabled = true;
        btnEnviar.textContent = 'Enviando...';
        
        // Crear parámetros en formato URL encoded (como se hace en otros formularios)
        const params = 'email=' + encodeURIComponent(email);
        
        console.log("Enviando solicitud a RecuperarContraseniaServlet");
        console.log("Datos enviados: " + params);
        
        // Enviar la solicitud usando fetch
        fetch('RecuperarContraseniaServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: params
        })
        .then(response => {
            console.log("Respuesta recibida del servidor");
            console.log("Status: " + response.status);
            console.log("Status Text: " + response.statusText);
            return response.json(); // Cambiado a json() en lugar de text()
        })
        .then(data => {
            console.log("Datos recibidos:", data);
            
            // Rehabilitar el botón
            btnEnviar.disabled = false;
            btnEnviar.textContent = 'Enviar Enlace';
            
            // Verificar si la respuesta tiene el formato esperado
            if (data && typeof data === 'object') {
                if (data.success) {
                    alert(data.mensaje || 'Se ha enviado un enlace para restablecer su contraseña.');
                    cerrarModalRecuperar();
                    // Limpiar el campo de email
                    document.getElementById('emailRecuperar').value = '';
                } else {
                    alert(data.mensaje || 'Error al procesar la solicitud.');
                }
            } else {
                // Si no es un objeto JSON válido, mostrar el texto tal cual
                alert('Error al procesar la respuesta del servidor.');
            }
        })
        .catch(error => {
            console.error('Error en la solicitud:', error);
            
            // Rehabilitar el botón en caso de error
            btnEnviar.disabled = false;
            btnEnviar.textContent = 'Enviar Enlace';
            
            alert('Error al enviar la solicitud. Por favor, inténtelo más tarde.');
        });
        
        console.log("=== FIN PROCESO RECUPERACIÓN ===");
    });
</script>
</body>
</html>