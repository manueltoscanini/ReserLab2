<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Models.Equipo" %>

<%
    List<Equipo> equipos = (List<Equipo>) request.getAttribute("equipos");
%>

<h2 class="titulo-equipos">Lista de equipos</h2>

<div class="lista-equipos">
    <%
        if (equipos == null || equipos.isEmpty()) {
    %>
    <p class="mensaje-vacio">No hay equipos registrados.</p>
    <%
    } else {
        for (Equipo eq : equipos) {
    %>
    <div class="tarjeta-equipo">
        <div class="imagen-equipo"><img src="eq.getFotoEquipo">
        </div>
        <p><strong>Nombre:</strong> <%= eq.getNombre() %></p>
        <p><strong>Tipo:</strong> <%= eq.getTipo() %></p>
        <p><strong>Precauciones:</strong> <%= eq.getPrecauciones() == null ? "No hay" : eq.getPrecauciones() %></p>
    </div>
    <%
            }
        }
    %>
</div>





