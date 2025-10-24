package com.example.appweb;

import DAO.EquipoDAO;
import Models.Equipo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ListarEquiposServlet", value = "/ListarEquiposServlet")
public class ListarEquiposServlet extends HttpServlet {
    
    private EquipoDAO equipoDAO = new EquipoDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Obtener todos los equipos
            List<Equipo> equipos = equipoDAO.obtenerEquipos();

            // Agregar datos al request
            request.setAttribute("equipos", equipos);

            // Redirigir al JSP de listar equipos
            request.getRequestDispatcher("listarEquipos.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error en ListarEquiposServlet: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().println("<h3>Error al cargar los equipos: " + e.getMessage() + "</h3>");
        }
    }
}
