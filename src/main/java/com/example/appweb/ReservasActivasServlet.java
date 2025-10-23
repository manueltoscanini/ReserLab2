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

@WebServlet(name = "ReservasActivasServlet", value = "/ReservasActivasServlet")
public class ReservasActivasServlet extends HttpServlet {
    
    private ActividadDAO actividadDAO = new ActividadDAO();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate date, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
                }
            })
            .create();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
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
            
            // Obtener las reservas activas (estado "aceptada")
            System.out.println("DEBUG: Consultando reservas activas para cédula: " + cedulaUsuario);
            List<Actividad> reservasActivas = actividadDAO.reservasActivasPorCi(cedulaUsuario, "aceptada");
            System.out.println("DEBUG: Número de reservas encontradas: " + (reservasActivas != null ? reservasActivas.size() : "null"));
            
            // Validar que la lista no sea nula
            if (reservasActivas == null) {
                System.out.println("WARNING: reservasActivas es null, enviando array vacío");
                reservasActivas = new java.util.ArrayList<>();
            }
            
            // Convertir a JSON y enviar respuesta
            String jsonResponse = gson.toJson(reservasActivas);
            System.out.println("DEBUG: Respuesta JSON: " + jsonResponse);
            
            PrintWriter out = response.getWriter();
            out.print(jsonResponse);
            out.flush();
            
        } catch (Exception e) {
            System.err.println("ERROR en ReservasActivasServlet: " + e.getMessage());
            System.err.println("Stack trace completo:");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error interno del servidor\", \"message\": \"" + e.getMessage().replace("\"", "\\\"" ) + "\"}");
        }
    }
}
