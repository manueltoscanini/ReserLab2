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
</head>
<body>
<main>
    <div id="contenedorLogin">

        <c:if test="${param.error == 'credenciales'}">
            <div class="mensaje-error">Correo o contraseña incorrectos.</div>
        </c:if>

        <c:if test="${param.error == 'camposVacios'}">
            <div class="mensaje-error">Por favor, complete todos los campos.</div>
        </c:if>

        <c:if test="${param.error == 'servidor'}">
            <div class="mensaje-error">Error interno del servidor. Intente nuevamente más tarde.</div>
        </c:if>

        <c:if test="${param.msg == 'cuentaDesactivada'}">
            <div class="mensaje-error">Tu cuenta ha sido desactivada. Contacta al administrador.</div>
        </c:if>

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