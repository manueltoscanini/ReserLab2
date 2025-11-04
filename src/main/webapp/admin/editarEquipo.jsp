<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Models.Equipo" %>
<%
    Models.Equipo equipo = (Models.Equipo) request.getAttribute("equipo");
%>

<div class="modal" id="modalEditarEquipo" style="display:flex;">
    <div class="modal-content modal-equipo">
        <div class="modal-header">
            <h3><i class="fa-solid fa-laptop"></i> Editar equipo</h3>
            <button type="button" class="btn-cerrar-modal" onclick="cerrarModalEquipo()">×</button>
        </div>

        <form id="formEditarEquipo" class="modal-body-equipo">
            <input type="hidden" name="id" value="<%= equipo.getId() %>"/>

            <div class="form-group">
                <label for="eq-nombre">Nombre</label>
                <input type="text"
                       id="eq-nombre"
                       name="nombre"
                       value="<%= equipo.getNombre() %>"
                       required>
            </div>

            <div class="form-group">
                <label for="eq-tipo">Tipo</label>
                <input type="text"
                       id="eq-tipo"
                       name="tipo"
                       value="<%= equipo.getTipo() %>"
                       required>
            </div>

            <div class="form-group">
                <label for="eq-prec">Precauciones</label>
                <textarea id="eq-prec"
                          name="precauciones"
                          rows="4"><%= equipo.getPrecauciones() == null ? "" : equipo.getPrecauciones() %></textarea>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn-cancelar" onclick="cerrarModalEquipo()">Cancelar</button>
                <button type="submit" class="btn-guardar">Guardar cambios</button>
            </div>
        </form>
    </div>
</div>

