<%-- login.jsp: --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión - ReserLab</title>
    <link rel="stylesheet" type="text/css" href="estilos/login.css">
</head>
<body>
<main>
    <div id="contenedorLogin">

        <c:if test="${param.msg == 'cuentaDesactivada'}">
            <div class="mensaje-error">Tu cuenta ha sido desactivada. Contacta al administrador si deseas reactivarla.</div>
        </c:if>

        <h2>Iniciar sesión</h2>

        <form action="LoginServlet" method="post">
            <label for="email">Correo:</label>
            <input type="email" id="email" name="email" required><br>

            <label for="password">Contraseña:</label>
            <input type="password" id="password" name="password" required><br>

            <input type="submit" value="Iniciar sesión">
        </form>


        <!-- <p><a href="registro.jsp">¿No tienes cuenta? Regístrate</a></p> -->
    </div>
</main>
</body>
</html>
