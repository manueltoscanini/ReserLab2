package com.example.appweb;

import DAO.ActividadDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RefrescarEstadosServlet", value = "/refrescar-estados")
public class RefrescarEstadosServlet extends HttpServlet {
    private final ActividadDAO actividadDAO = new ActividadDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            actividadDAO.refrescarEstados();
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"No se pudieron refrescar los estados\"}");
        }
    }
}