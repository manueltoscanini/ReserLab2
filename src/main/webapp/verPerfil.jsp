<%-- verPerfil.jsp: --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: Asus
  Date: 21/10/2025
  Time: 18:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Perfil</title>
    <link rel="stylesheet" href="estilos/usuario.css?v=1.0">
</head>
<body>
<div class="perfil-container">
    <div class="perfil-header">Mi perfil</div>

    <div class="perfil-datos">
        <div class="fila">
            <div class="etiqueta">Nombre:</div>
            <div class="valor">${nombre}</div>
        </div>
        <div class="fila">
            <div class="etiqueta">Email:</div>
            <div class="valor">${email}</div>
        </div>
        <div class="fila">
            <div class="etiqueta">Cédula:</div>
            <div class="valor">${cedula}</div>
        </div>
        <div class="fila">
            <div class="etiqueta">Tipo de cliente:</div>
            <div class="valor">${tipo}</div>
        </div>
        <c:if test="${not empty carrera}">
            <div class="fila">
                <div class="etiqueta">Carrera:</div>
                <div class="valor">${carrera}</div>
            </div>
        </c:if>
    </div>

    <div class="perfil-botones">
        <button id="btnEditarDatos" type="button" onclick="abrirEditarPerfil()">Editar datos</button>
        <button id="btnCambiarContrasenia">Cambiar contraseña</button>
        <button class="eliminar" id="btnEliminarCuenta">Eliminar cuenta</button>
    </div>
</div>
</body>
</html>

