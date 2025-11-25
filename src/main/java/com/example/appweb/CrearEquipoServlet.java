package com.example.appweb;

import DAO.EquipoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

// Servlet para crear un nuevo equipo
@WebServlet(name = "CrearEquipoServlet", value = "/crear-equipo")
public class CrearEquipoServlet extends HttpServlet {

    private final EquipoDAO equipoDAO = new EquipoDAO();

    // Maneja las solicitudes POST para crear un nuevo equipo
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar la respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Verificar si el usuario está autenticado
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"No hay sesión activa\"}");
            return;
        }

        try {
            // Obtener parámetros del formulario
            String nombre = request.getParameter("nombre");
            String tipo = request.getParameter("tipo");
            String precauciones = request.getParameter("precauciones");

            // Validar parámetros obligatorios
            if (nombre == null || nombre.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"El nombre es requerido\"}");
                return;
            }

            if (tipo == null || tipo.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"El tipo es requerido\"}");
                return;
            }

            // Precauciones puede estar vacío, usar cadena vacía si es null
            String precaucionesFinal = (precauciones != null) ? precauciones.trim() : "";

            // Llamar al método del DAO para agregar el equipo
            boolean exito = equipoDAO.agregarEquipo(nombre.trim(), tipo.trim(), precaucionesFinal);

            if (exito) {
                response.getWriter().write("{\"success\": true, \"message\": \"Equipo creado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"success\": false, \"message\": \"No se pudo crear el equipo\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String safeMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error al crear equipo";
            response.getWriter().write("{\"success\": false, \"message\": \"" + safeMsg + "\"}");
        }
    }
}


