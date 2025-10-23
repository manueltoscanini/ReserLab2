package com.example.appweb;

import DAO.EquipoDAO;
import Models.Equipo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "EquiposServlet", value = "/equipos")
public class EquiposServlet extends HttpServlet {
    
    private EquipoDAO equipoDAO = new EquipoDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Obtener parámetros de paginación
            String pageParam = request.getParameter("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            int pageSize = 4; // 4 equipos por página
            int offset = (page - 1) * pageSize;

            // Obtener todos los equipos
            List<Equipo> todosLosEquipos = equipoDAO.obtenerEquipos();

            // Calcular información de paginación
            int totalEquipos = todosLosEquipos.size();
            int totalPages = (int) Math.ceil((double) totalEquipos / pageSize);

            // Obtener equipos para la página actual
            List<Equipo> equiposPagina = todosLosEquipos.subList(
                    Math.min(offset, totalEquipos),
                    Math.min(offset + pageSize, totalEquipos)
            );

            // Agregar datos al request
            request.setAttribute("equipos", equiposPagina);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalEquipos", totalEquipos);
            request.setAttribute("hasNextPage", page < totalPages);
            request.setAttribute("hasPrevPage", page > 1);

            // Redirigir a admin.jsp con los datos
            request.getRequestDispatcher("/admin.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error en EquiposServlet: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().println("<h3>Error al cargar los equipos: " + e.getMessage() + "</h3>");
        }
    }
}