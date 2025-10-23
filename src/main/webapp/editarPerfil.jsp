<%--editarPerfil.jsp: --%>
<%--
  Created by IntelliJ IDEA.
  User: Asus
  Date: 22/10/2025
  Time: 15:55
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="modal-overlay" id="editarPerfilModal">
    <div class="modal">
        <h2>Editar datos personales</h2>
        <form id="formEditarPerfil" action="EditarPerfilServlet" method="post">
            <div class="form-row">
                <label for="nombre">Nombre completo:</label>
                <input type="text" id="nombre" name="nombre" value="${nombre}" required>
            </div>

            <div class="form-row">
                <label for="tipo_cliente">Tipo de cliente:</label>
                <select id="tipo_cliente" name="tipo_cliente" required>
                    <option value="">Seleccione un tipo</option>
                    <option value="estudiante" ${tipo == 'estudiante' ? 'selected' : ''}>Estudiante</option>
                    <option value="emprendedor" ${tipo == 'emprendedor' ? 'selected' : ''}>Emprendedor</option>
                    <option value="docente" ${tipo == 'docente' ? 'selected' : ''}>Docente</option>
                    <option value="invitado" ${tipo == 'invitado' ? 'selected' : ''}>Invitado</option>
                </select>
            </div>

            <div class="form-row" id="divCarrera" style="display: ${tipo == 'estudiante' ? 'block' : 'none'};">
                <label for="carrera">Carrera:</label>
                <select id="carrera" name="carrera">
                    <option value="">Seleccione una carrera</option>
                    <c:forEach var="c" items="${listaCarreras}">
                        <option value="${c}" ${c == carrera ? 'selected' : ''}>${c}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="acciones">
                <button type="submit">Guardar cambios</button>
                <button type="button" onclick="cerrarModal()">Cancelar</button>
            </div>
        </form>
    </div>
</div>