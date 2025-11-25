package com.example.appweb;


import DAO.ActividadDAO;
import DAO.ClienteDAO;
import Models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;

// Servlet para crear una reserva de actividad por parte del cliente
@WebServlet(name = "CrearReservaClienteServlet", value = "/reserva_cliente")
public class CrearReservaClienteServlet extends HttpServlet {


    private ActividadDAO actividadDAO = new ActividadDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();

    // Maneja las solicitudes POST para crear una reserva
   @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Obtener el usuario de la sesión (ya está logueado)
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
            String cedulaCliente = usuario.getCedula(); // Usar la cédula del usuario logueado
            String fechaStr = request.getParameter("fecha");
            String horaInicioStr = request.getParameter("horaInicio");
            String horaFinStr = request.getParameter("horaFin");
            String cantidadParticipantesStr = request.getParameter("cantidadParticipantes");
            String estado = "en_espera"; // Estado por defecto

            // Validar que todos los campos estén presentes
            if (fechaStr == null || horaInicioStr == null ||
                    horaFinStr == null || cantidadParticipantesStr == null) {
                request.getSession().setAttribute("error", "Todos los campos son obligatorios");
                response.sendRedirect("usuario.jsp");
                return;
            }

            // Validar que el cliente exista
            if (!actividadDAO.existeClientePorCedula(cedulaCliente)) {
                request.getSession().setAttribute("error",
                        "El cliente con cédula " + cedulaCliente + " no existe en el sistema");
                response.sendRedirect("usuario.jsp");
                return;
            }

            // Parsear fecha y horas
            LocalDate fecha = LocalDate.parse(fechaStr);
            Time horaInicio = Time.valueOf(horaInicioStr + ":00");
            Time horaFin = Time.valueOf(horaFinStr + ":00");
            int cantidadParticipantes = Integer.parseInt(cantidadParticipantesStr);

            // Validar que la hora de fin sea mayor que la hora de inicio
            if (horaFin.before(horaInicio) || horaFin.equals(horaInicio)) {
                request.getSession().setAttribute("error",
                        "La hora de fin debe ser posterior a la hora de inicio");
                response.sendRedirect("usuario.jsp");
                return;
            }

            // Validar que no haya conflicto de horarios
            if (ActividadDAO.existeConflictoReserva(fecha, horaInicio, horaFin)) {
                request.getSession().setAttribute("error",
                        "Ya existe una reserva en ese horario");
                response.sendRedirect("usuario.jsp");
                return;
            }

            // Crear la reserva
            Integer idActividad = ActividadDAO.crearReservaYObtenerId(
                    fecha, horaInicio, horaFin, cantidadParticipantes, cedulaCliente, estado
            );

            // Vincular equipos si la reserva fue creada exitosamente
            if (idActividad != null) {
                // Procesar equipos seleccionados
                String[] equiposIds = request.getParameterValues("equiposIds");
                String[] equiposUsos = request.getParameterValues("equiposUsos");

                // Vincular cada equipo a la actividad
                if (equiposIds != null && equiposUsos != null && equiposIds.length == equiposUsos.length) {
                    for (int i = 0; i < equiposIds.length; i++) {
                        try {

                            // Parsear ID del equipo y obtener uso
                            int equipoId = Integer.parseInt(equiposIds[i]);
                            String uso = equiposUsos[i];
                            actividadDAO.vincularEquipoAActividad(idActividad, equipoId, uso);
                        } catch (NumberFormatException e) {
                            System.err.println("Error al procesar equipo ID: " + equiposIds[i]);
                        }
                    }
                }

                // Reserva creada exitosamente
                request.getSession().setAttribute("exito",
                        "Reserva creada exitosamente con ID: " + idActividad);
            } else {
                request.getSession().setAttribute("error",
                        "No se pudo crear la reserva. Intente nuevamente.");
            }

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error",
                    "Error en el formato de los datos numéricos");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error",
                    "Error al crear la reserva: " + e.getMessage());
        }

       response.sendRedirect("usuario.jsp?mostrar=historial");//mostrar = historial para que se active un script que me muestre el historial de reserva
    }


}
