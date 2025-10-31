package com.example.appweb;

import DAO.ActividadDAO;
import Models.Actividad;
import Models.Usuario;
import com.google.gson.GsonBuilder;
import com.google.gson.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "HistorialReservasServlet", value = "/historial-reservas")
public class HistorialReservasServlet extends HttpServlet {

    private ActividadDAO actividadDAO = new ActividadDAO();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("\n========================================");
        System.out.println("DEBUG: HistorialReservasServlet INVOCADO");
        System.out.println("========================================\n");

        try {
            // Obtener la cédula del usuario desde la sesión
            HttpSession session = request.getSession();
            // Debug: mostrar todos los atributos de sesión
            System.out.println("DEBUG: Atributos de sesión disponibles:");
            java.util.Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
                Object attributeValue = session.getAttribute(attributeName);
                System.out.println("  " + attributeName + " = " + attributeValue);
            }

            // Obtener el usuario completo de la sesión
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            System.out.println("DEBUG: Usuario en sesión: " + (usuario != null ? usuario.getNombre() : "null"));

            if (usuario == null) {
                System.out.println("DEBUG: Usuario no autenticado - usuario es null");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Usuario no autenticado\"}");
                return;
            }

            String cedulaUsuario = usuario.getCedula();
            System.out.println("DEBUG: Cédula del usuario: " + cedulaUsuario);

            // Obtener el historial de reservas (todos los estados excepto "aceptada")
            System.out.println("DEBUG: Consultando historial de reservas para cédula: " + cedulaUsuario);
            List<Actividad> todasLasReservas = actividadDAO.historialReservasPorCi(cedulaUsuario);
            System.out.println("DEBUG: Número de reservas en historial: " + (todasLasReservas != null ? todasLasReservas.size() : "null"));

            // Validar que la lista no sea nula
            if (todasLasReservas == null) {
                System.out.println("WARNING: historial de reservas es null, enviando array vacío");
                todasLasReservas = new java.util.ArrayList<>();
            }

            // Set the reservas data and forward to the history JSP
            request.setAttribute("historialReservas", todasLasReservas);
            System.out.println("DEBUG: Redirigiendo a /HistorialDeReservas.jsp");
            System.out.println("========================================\n");
            request.getRequestDispatcher("/HistorialDeReservas.jsp").forward(request, response);


        } catch (Exception e) {
            System.err.println("ERROR en HistorialReservasServlet: " + e.getMessage());
            System.err.println("Stack trace completo:");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error interno del servidor\", \"message\": \"" + e.getMessage().replace("\"", "\\\"" ) + "\"}");
        }
    }
}
