<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="DAO.CarreraDAO" %>

<div class="contenido-carreras">
    <div class="header-seccion">
        <h2 class="titulo-seccion">Carreras</h2>
        <small style="color:#666;">Vista de solo lectura</small>
    </div>

    <%
        DAO.CarreraDAO carreraDAO = new DAO.CarreraDAO();
        List<String[]> filas = null;
        try {
            filas = carreraDAO.obtenerCarrerasConSedeTabla();
        } catch (Exception e) {
            filas = java.util.Collections.emptyList();
        }
    %>

    <div class="tabla-wrapper" style="margin-top:12px;">
        <table class="tabla-carreras" style="width:100%; border-collapse:collapse;">
            <thead>
                <tr style="background:#f7f7f7; text-align:left;">
                    <th style="padding:10px; border-bottom:1px solid #eaeaea;">Carrera</th>
                    <th style="padding:10px; border-bottom:1px solid #eaeaea;">Sede</th>
                    <th style="padding:10px; border-bottom:1px solid #eaeaea;">Departamento</th>
                </tr>
            </thead>
            <tbody>
            <%
                if (filas != null && !filas.isEmpty()) {
                    for (String[] f : filas) {
            %>
                <tr>
                    <td style="padding:10px; border-bottom:1px solid #f0f0f0;"><%= f[0] %></td>
                    <td style="padding:10px; border-bottom:1px solid #f0f0f0;"><%= f[1] %></td>
                    <td style="padding:10px; border-bottom:1px solid #f0f0f0;"><%= f[2] %></td>
                </tr>
            <%
                    }
                } else {
            %>
                <tr>
                    <td colspan="3" style="padding:16px; color:#666; text-align:center;">
                        No hay carreras registradas.
                    </td>
                </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</div>
