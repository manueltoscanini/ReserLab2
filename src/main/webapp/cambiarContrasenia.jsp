<%-- cambiarContrasenia.jsp: --%>
<%--
  Created by IntelliJ IDEA.
  User: Asus
  Date: 23/10/2025
  Time: 0:11
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="cambiarContraseniaModal" class="modal-overlay" style="display:none;">
    <div class="modal">
        <h2>Cambiar contraseña</h2>
        <form id="formCambiarContrasenia" method="post">
            <label for="actual">Contraseña actual:</label>
            <input type="password" id="actual" name="actual" required minlength="8">

            <label for="nueva">Nueva contraseña:</label>
            <input type="password" id="nueva" name="nueva" required minlength="8">

            <label for="confirmar">Confirmar nueva contraseña:</label>
            <input type="password" id="confirmar" name="confirmar" required minlength="8">

            <div class="acciones">
                <button type="submit">Guardar cambios</button>
                <button type="button" onclick="cerrarModal()">Cancelar</button>
            </div>
        </form>
    </div>
</div>



