package com.example.appweb;

import DAO.EquipoDAO;
import Models.Equipo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "EquiposServlet", value = "/EquiposServlet")
public class EquiposServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EquipoDAO dao = new EquipoDAO();
        List<Equipo> equipos = dao.obtenerEquipos();

        request.setAttribute("equipos", equipos);
        RequestDispatcher dispatcher = request.getRequestDispatcher("listarEquipos.jsp");
        dispatcher.forward(request, response);
    }
}