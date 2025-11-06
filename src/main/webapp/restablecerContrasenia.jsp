<%-- restablecerContrasenia.jsp: --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Restablecer Contraseña - ReserLab</title>
    <link rel="stylesheet" type="text/css" href="estilos/login.css?v=1.1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
<main>
    <div id="contenedorLogin">
        <h2>Restablecer Contraseña</h2>
        
        <c:if test="${param.error == 'token'}">
            <div class="mensaje-error">El enlace para restablecer la contraseña es inválido o ha expirado.</div>
        </c:if>
        
        <c:if test="${param.error == 'contrasenia'}">
            <div class="mensaje-error">Las contraseñas no coinciden.</div>
        </c:if>
        
        <c:if test="${param.msg == 'exito'}">
            <div class="mensaje-exito">Su contraseña ha sido restablecida correctamente. Ahora puede iniciar sesión.</div>
        </c:if>
        
        <c:if test="${param.msg != 'exito'}">
            <form id="resetForm" action="ResetearContraseniaServlet" method="post">
                <input type="hidden" name="token" value="${param.token}">
                
                <label for="nuevaContrasenia">Nueva Contraseña:</label>
                <div class="password-container">
                    <input type="password" id="nuevaContrasenia" name="nuevaContrasenia" required>
                    <span class="eye-icon" onclick="togglePasswordVisibility('nuevaContrasenia', 'eyeIcon1')">
                        <i id="eyeIcon1" class="fas fa-eye"></i>
                    </span>
                </div><br>
                
                <label for="confirmarContrasenia">Confirmar Contraseña:</label>
                <div class="password-container">
                    <input type="password" id="confirmarContrasenia" name="confirmarContrasenia" required>
                    <span class="eye-icon" onclick="togglePasswordVisibility('confirmarContrasenia', 'eyeIcon2')">
                        <i id="eyeIcon2" class="fas fa-eye"></i>
                    </span>
                </div><br>
                
                <input type="submit" value="Restablecer Contraseña">
            </form>
        </c:if>
        
        <div class="btn-volver-container">
            <button type="button" class="btn-volver" onclick="window.location.href='login.jsp'">
                <i class="fa-solid fa-arrow-left"></i> Volver al inicio de sesión
            </button>
        </div>
    </div>
</main>

<script>
    function togglePasswordVisibility(inputId, eyeIconId) {
        const passwordInput = document.getElementById(inputId);
        const eyeIcon = document.getElementById(eyeIconId);
        
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
    
    // Validar que las contraseñas coincidan antes de enviar el formulario
    document.getElementById('resetForm').addEventListener('submit', function(e) {
        const nuevaContrasenia = document.getElementById('nuevaContrasenia').value;
        const confirmarContrasenia = document.getElementById('confirmarContrasenia').value;
        
        if (nuevaContrasenia !== confirmarContrasenia) {
            e.preventDefault();
            alert('Las contraseñas no coinciden. Por favor, inténtelo de nuevo.');
            return false;
        }
        
        // Validar que la contraseña no esté vacía
        if (nuevaContrasenia.length === 0) {
            e.preventDefault();
            alert('La contraseña no puede estar vacía.');
            return false;
        }
    });
</script>
</body>
</html>