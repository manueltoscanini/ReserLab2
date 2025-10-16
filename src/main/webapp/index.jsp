<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>ReserLab</title>
    <link rel="stylesheet" href="estilos/index.css?v=1.0">
</head>
<body>
<div id="contenedorPrincipal">
    <div id="contenedorIzquierdo">
        <img id="logoApp" src="imagenes/logo.png" alt="Logo ReserLab">
        <h1 id="tituloPrincipal">ReserLab</h1>
        <p id="descripcionApp">Aplicación de gestión de reservas del laboratorio A de UTEC ITR Suroeste.
            Permite registrar usuarios, realizar y administrar reservas de forma rápida y organizada, mejorando la eficiencia y el control del uso del laboratorio.</p>
    </div>

    <div id="contenedorDerecho">
        <h1>Bienvenido</h1>
        <input type="button" value="Iniciar sesión" onclick="window.location.href='login.jsp'">
        <input type="button" value="Registrarse" onclick="window.location.href='RegistroServlet'">
    </div>
</div>
</body>
</html>
