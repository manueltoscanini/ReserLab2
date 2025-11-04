package com.example.appweb;

import DAO.ClienteDAO;
import Models.Usuario;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;

@WebServlet(name = "CancelarReservaServlet", value = "/CancelarReservaServlet")
public class CancelarReservaServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"ok\":false,\"msg\":\"No autenticado\"}");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String cedula = usuario.getCedula();

        String idParam = request.getParameter("idActividad");
        String fechaParam = request.getParameter("fecha");
        String horaInicioParam = request.getParameter("horaInicio");
        String horaFinParam = request.getParameter("horaFin");

        if (idParam == null || fechaParam == null || horaInicioParam == null || horaFinParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"ok\":false,\"msg\":\"Parámetros incompletos\"}");
            return;
        }

        try {
            int idActividad = Integer.parseInt(idParam);
            LocalDate fecha = LocalDate.parse(fechaParam);
            Time horaInicio = parseTimeFlexible(horaInicioParam);
            Time horaFin = parseTimeFlexible(horaFinParam);

            // Verificar que la reserva sea al menos 24 horas en el futuro
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.LocalDateTime reservationDateTime = java.time.LocalDateTime.of(fecha, horaInicio.toLocalTime());
            java.time.Duration duration = java.time.Duration.between(now, reservationDateTime);
            
            // Si la diferencia es menor a 24 horas (86400 segundos), no permitir cancelación
            if (duration.getSeconds() < 86400) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"ok\":false,\"msg\":\"No se puede cancelar la reserva con menos de 24 horas de anticipación\"}");
                return;
            }

            boolean exito = clienteDAO.cancelarReserva(cedula, idActividad, fecha, horaInicio, horaFin);
            if (exito) {
                response.getWriter().write("{\"ok\":true}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"ok\":false,\"msg\":\"Reserva no encontrada\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String msg = e.getMessage() == null ? "Error al cancelar" : ("Error al cancelar: " + e.getMessage().replace("\"","\\\""));
            response.getWriter().write("{\"ok\":false,\"msg\":\"" + msg + "\"}");
        }
    }

    private Time parseTimeFlexible(String value) {
        String v = value == null ? null : value.trim();
        if (v == null || v.isEmpty()) throw new IllegalArgumentException("hora vacía");

        // Normalizar español: quitar puntos y espacios, y detectar am/pm
        String compact = v.toLowerCase()
                .replace(".", "")
                .replace(" ", ""); // ej: "12:00p m" -> "12:00pm"
        boolean isPM = compact.contains("pm") || compact.contains("p m");
        boolean isAM = compact.contains("am") || compact.contains("a m");

        // Extraer hh, mm, ss si existen
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("^(\\d{1,2})(?::(\\d{2}))?(?::(\\d{2}))?")
                .matcher(compact);
        if (m.find()) {
            int h = Integer.parseInt(m.group(1));
            int min = m.group(2) != null ? Integer.parseInt(m.group(2)) : 0;
            int sec = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;

            if (isPM && h < 12) h += 12;
            if (isAM && h == 12) h = 0;

            String normalized = String.format(java.util.Locale.ROOT, "%02d:%02d:%02d", h, min, sec);
            return Time.valueOf(normalized);
        }

        // Manejo de formatos HH:mm o HH:mm:ss puros
        if (v.matches("^\\d{2}:\\d{2}$")) return Time.valueOf(v + ":00");
        if (v.matches("^\\d{2}:\\d{2}:\\d{2}\\.\\d+$")) return Time.valueOf(v.substring(0, 8));
        if (v.matches("^\\d{2}:\\d{2}:\\d{2}$")) return Time.valueOf(v);

        throw new IllegalArgumentException("formato de hora no soportado: " + value);
    }
}
