package com.example.appweb;
import DAO.ActividadDAO;
import ConectionDB.ConnectionDB;
import com.example.appweb.util.MailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "CambiarEstadoServlet", value = "/cambiar-estado")
public class CambiarEstadoServlet extends HttpServlet {

    private ActividadDAO actividadDAO = new ActividadDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idActividadParam = request.getParameter("idActividad");
            String nuevoEstado = request.getParameter("nuevoEstado");
            String pageParam = request.getParameter("page");

            System.out.println("=== CambiarEstadoServlet ===");
            System.out.println("ID Actividad: " + idActividadParam);
            System.out.println("Nuevo Estado: " + nuevoEstado);
            System.out.println("Página: " + pageParam);

            if (idActividadParam == null || nuevoEstado == null) {
                System.out.println("Error: Parámetros faltantes");
                response.getWriter().println("<h3>Error: Parámetros requeridos</h3>");
                return;
            }

            int idActividad = Integer.parseInt(idActividadParam);

            // Cambiar el estado en la base de datos
            boolean exito = false;
            if ("aprobada".equals(nuevoEstado)) {
                System.out.println("Intentando aprobar actividad: " + idActividad);
                exito = actividadDAO.aprobarActividad(idActividad);
                String emailPersona = actividadDAO.buscarEmail(idActividad);
                MailService.sendReservationAcceptedAsync(
                        emailPersona, "Reserva aceptada");
            } else if ("rechazada".equals(nuevoEstado)) {
                System.out.println("Intentando rechazar actividad: " + idActividad);
                exito = rechazarActividad(idActividad);
                String emailPersona = actividadDAO.buscarEmail(idActividad);
                MailService.sendReservationAcceptedAsync(
                        emailPersona, "Reserva rechazada");
            }

            System.out.println("Resultado: " + (exito ? "ÉXITO" : "FALLO"));

            if (exito) {
                // Redirigir de vuelta a la página de reservas
                String redirectUrl = "reservas";
                if (pageParam != null) {
                    redirectUrl += "?page=" + pageParam;
                }
                response.sendRedirect(redirectUrl);
            } else {
                response.getWriter().println("<h3>Error al cambiar el estado de la reserva</h3>");
            }


            // MailService.sendReservationAcceptedAsync(
            //  usuario.getEmail(),
            //  usuario.getNombre(),
            //  String.valueOf(reserva.getId()),
            //  reserva.getFecha().toString(),
            //reserva.getRecurso()
            // );

        } catch (Exception e) {
            System.err.println("Error en CambiarEstadoServlet: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().println("<h3>Error al cambiar el estado: " + e.getMessage() + "</h3>");
        }
    }

    private boolean rechazarActividad(int idActividad) {
        String sql = "UPDATE actividad SET estado = 'rechazada' WHERE id_actividad = ? AND estado = 'en_espera'";
        try {
            java.sql.PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setInt(1, idActividad);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al rechazar actividad", e);
        }
    }





}
