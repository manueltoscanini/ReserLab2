<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Models.Equipo" %>

<%
    List<Equipo> equipos = (List<Equipo>) request.getAttribute("equipos");
%>

<div class="contenido-equipos">
    <div class="header-seccion">
        <h2 class="titulo-seccion"><i class="fa-solid fa-laptop"></i> Lista de Equipos del Laboratorio</h2>
    </div>

    <div class="lista-equipos">
        <%
            if (equipos == null || equipos.isEmpty()) {
        %>
        <div class="sin-equipos">
            <i class="fa-solid fa-laptop-medical"></i>
            <p>No hay equipos registrados en el laboratorio.</p>
        </div>
        <%
        } else {
            for (Equipo eq : equipos) {
        %>
        <div class="tarjeta-equipo">
            <div class="imagen-equipo">
                <%
                    if (eq.getFoto_Equipo() != null && !eq.getFoto_Equipo().isEmpty()) {
                %>
                <img src="<%= eq.getFoto_Equipo() %>" alt="<%= eq.getNombre() %>" class="foto-equipo">
                <%
                    } else {
                %>
                <i class="fa-solid fa-laptop-medical icono-equipo"></i>
                <%
                    }
                %>
            </div>
            <div class="info-equipo">
                <h3 class="nombre-equipo"><%= eq.getNombre() %></h3>
                <div class="detalle-equipo">
                    <i class="fa-solid fa-tag"></i>
                    <span><strong>Tipo:</strong> <%= eq.getTipo() %></span>
                </div>
                <div class="detalle-equipo">
                    <i class="fa-solid fa-exclamation-triangle"></i>
                    <span><strong>Precauciones:</strong> <%= eq.getPrecauciones() == null || eq.getPrecauciones().isEmpty() ? "Ninguna" : eq.getPrecauciones() %></span>
                </div>
            </div>
        </div>
        <%
                }
            }
        %>
    </div>
</div>





