package com.example.appweb;

import DAO.EquipoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "EliminarEquipoServlet", value = "/eliminar-equipo")
public class EliminarEquipoServlet extends HttpServlet {

    private EquipoDAO equipoDAO = new EquipoDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"No hay sesión activa\"}");
            return;
        }

        try {
            String idEquipoStr = request.getParameter("idEquipo");
            
            if (idEquipoStr == null || idEquipoStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"ID de equipo no especificado\"}");
                return;
            }
            
            int idEquipo = Integer.parseInt(idEquipoStr);
            
            // Eliminar el equipo usando el DAO
            equipoDAO.eliminarEquipo(idEquipo);
            
            response.getWriter().write("{\"success\": true, \"message\": \"Equipo eliminado correctamente\"}");
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"message\": \"ID de equipo inválido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Error interno del servidor\"}");
        }
    }
}