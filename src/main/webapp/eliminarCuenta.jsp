@@ -0,0 +1,22 @@
<%-- eliminarCuenta.jsp: --%>
<%--
  Created by IntelliJ IDEA.
  User: Asus
  Date: 29/10/2025
  Time: 10:07
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="eliminarCuentaModal" class="modal-overlay" style="display:none;">
    <div class="modal">
        <h2>¿Estás seguro de que quieres eliminar tu cuenta?</h2>
        <p style="text-align: center; color: #666; margin: 20px 0; line-height: 1.5;">
            Tu cuenta será desactivada y no podrás volver a iniciar sesión.
        </p>
        <div class="acciones">
            <button class="btnRojo" >Sí, eliminar</button>
            <button type="button" onclick="cerrarModal()">Cancelar</button>
        </div>
    </div>
</div>