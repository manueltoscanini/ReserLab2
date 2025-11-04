package com.example.appweb;

import DAO.ActividadDAO;
import Models.Actividad;
import Models.EquipoUso;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "EditarReservaServlet", value = "/editar-reserva")
public class EditarReservaServlet extends HttpServlet {

    private ActividadDAO actividadDAO = new ActividadDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de reserva no proporcionado");
            return;
        }
        
        try {
            int idActividad = Integer.parseInt(idStr);
            
            // Obtener la actividad
            Actividad actividad = actividadDAO.obtenerActividadPorId(idActividad);
            
            if (actividad == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reserva no encontrada");
                return;
            }
            
            // Obtener los equipos de la actividad
            List<EquipoUso> equipos = actividadDAO.obtenerEquiposDeActividad(idActividad);
            
            // Convertir a JSON manualmente
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"id\":").append(actividad.getIdActividad()).append(",");
            json.append("\"fecha\":\"").append(actividad.getFecha().toString()).append("\",");
            json.append("\"horaInicio\":\"").append(actividad.getHoraInicio().toString().substring(0, 5)).append("\",");
            json.append("\"horaFin\":\"").append(actividad.getHoraFin().toString().substring(0, 5)).append("\",");
            json.append("\"cantidadParticipantes\":").append(actividad.getCantidadParticipantes()).append(",");
            json.append("\"equipos\":[");
            
            for (int i = 0; i < equipos.size(); i++) {
                EquipoUso equipo = equipos.get(i);
                if (i > 0) json.append(",");
                json.append("{");
                json.append("\"id\":").append(equipo.getIdEquipo()).append(",");
                json.append("\"nombre\":\"").append(escapeJson(equipo.getNombre())).append("\",");
                json.append("\"tipo\":\"").append(escapeJson(equipo.getTipo())).append("\",");
                json.append("\"uso\":\"").append(escapeJson(equipo.getUso())).append("\"");
                json.append("}");
            }
            
            json.append("]");
            json.append("}");
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de reserva inválido");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener la reserva");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Obtener parámetros del formulario
            String idStr = request.getParameter("idActividad");
            String fechaStr = request.getParameter("fecha");
            String horaInicioStr = request.getParameter("horaInicio");
            String horaFinStr = request.getParameter("horaFin");
            String cantidadParticipantesStr = request.getParameter("cantidadParticipantes");

            // Validar que todos los campos estén presentes
            if (idStr == null || fechaStr == null || horaInicioStr == null || 
                horaFinStr == null || cantidadParticipantesStr == null) {
                request.getSession().setAttribute("error", "Todos los campos son obligatorios");
                response.sendRedirect(request.getHeader("Referer"));
                return;
            }

            int idActividad = Integer.parseInt(idStr);
            LocalDate fecha = LocalDate.parse(fechaStr);
            Time horaInicio = Time.valueOf(horaInicioStr + ":00");
            Time horaFin = Time.valueOf(horaFinStr + ":00");
            int cantidadParticipantes = Integer.parseInt(cantidadParticipantesStr);

            // Validar que la hora de fin sea mayor que la hora de inicio
            if (horaFin.before(horaInicio) || horaFin.equals(horaInicio)) {
                request.getSession().setAttribute("error", 
                    "La hora de fin debe ser posterior a la hora de inicio");
                response.sendRedirect(request.getHeader("Referer"));
                return;
            }

            // Validar que no haya conflicto de horarios (excluyendo la reserva actual)
            if (actividadDAO.existeConflictoReservaExcluyendo(fecha, horaInicio, horaFin, idActividad)) {
                request.getSession().setAttribute("error", 
                    "Ya existe una reserva en ese horario");
                response.sendRedirect(request.getHeader("Referer"));
                return;
            }

            // Actualizar la reserva
            boolean actualizado = actividadDAO.actualizarReserva(
                idActividad, fecha, horaInicio, horaFin, cantidadParticipantes
            );

            if (actualizado) {
                // Eliminar equipos anteriores
                actividadDAO.eliminarEquiposDeActividad(idActividad);
                
                // Procesar equipos seleccionados
                String[] equiposIds = request.getParameterValues("equiposIds");
                String[] equiposUsos = request.getParameterValues("equiposUsos");
                
                if (equiposIds != null && equiposUsos != null && equiposIds.length == equiposUsos.length) {
                    for (int i = 0; i < equiposIds.length; i++) {
                        try {
                            int equipoId = Integer.parseInt(equiposIds[i]);
                            String uso = equiposUsos[i];
                            actividadDAO.vincularEquipoAActividad(idActividad, equipoId, uso);
                        } catch (NumberFormatException e) {
                            System.err.println("Error al procesar equipo ID: " + equiposIds[i]);
                        }
                    }
                }
                
                request.getSession().setAttribute("exito", 
                    "Reserva actualizada exitosamente");
            } else {
                request.getSession().setAttribute("error", 
                    "No se pudo actualizar la reserva. Intente nuevamente.");
            }

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", 
                "Error en el formato de los datos numéricos");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", 
                "Error al actualizar la reserva: " + e.getMessage());
        }

        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : "usuario.jsp");
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
