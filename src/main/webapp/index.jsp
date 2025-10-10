<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ReserLab</title>
    <link rel="stylesheet" type="text/css" href="estilos/index.css">
</head>
<body>
<header>
    <div id="contenedorSuperior">
        <h1 id="tituloPrincipal">ReserLab</h1>
    </div>
</header>
<main>
    <div id="contIniciarSesion">
        <input type="button" value="Iniciar sesión" onclick="window.location.href='login.jsp'">
    </div>
    <div id="contRegistrarse">
        <input type="button" value="Registrarse" onclick="window.location.href='registro.jsp'">

    </div>
</main>
<h1>Hello World!</h1>
<br/>
<a href="hello-servlet">Hello Servlet</a>
</body>
</html>