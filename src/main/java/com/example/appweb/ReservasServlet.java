package com.example.appweb;

import DAO.ActividadDAO;
import Models.Actividad;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ReservasServlet", value = "/reservas")
public class ReservasServlet extends HttpServlet {

    private ActividadDAO actividadDAO = new ActividadDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Obtener parámetros de paginación
            String pageParam = request.getParameter("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            int pageSize = 3; // 3 reservas por página
            int offset = (page - 1) * pageSize;

            // Obtener todas las reservas
            List<Actividad> todasLasReservas = actividadDAO.getTodas();

            // Calcular información de paginación
            int totalReservas = todasLasReservas.size();
            int totalPages = (int) Math.ceil((double) totalReservas / pageSize);

            // Obtener reservas para la página actual
            List<Actividad> reservasPagina = todasLasReservas.subList(
                    Math.min(offset, totalReservas),
                    Math.min(offset + pageSize, totalReservas)
            );

            // Agregar datos al request
            request.setAttribute("reservas", reservasPagina);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalReservas", totalReservas);
            request.setAttribute("hasNextPage", page < totalPages);
            request.setAttribute("hasPrevPage", page > 1);

            // Redirigir a admin.jsp con los datos
            request.getRequestDispatcher("admin.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error en ReservasServlet: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().println("<h3>Error al cargar las reservas: " + e.getMessage() + "</h3>");
        }
    }
}

