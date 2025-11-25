package com.example.appweb;

import DAO.ClienteDAO;
import DAO.ClienteDAO.ClienteBusqueda;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(name = "BuscarClientesServlet", value = "/buscar-clientes")
public class BuscarClientesServlet extends HttpServlet {

    private ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");
        response.setContentType("application/json;charset=UTF-8");

        if (q == null || q.trim().length() < 2) {
            // si no hay texto o es muy corto, devolvemos lista vacía
            try (PrintWriter out = response.getWriter()) {
                out.print("[]");
            }
            return;
        }

        q = q.trim();

        List<ClienteBusqueda> resultados = clienteDAO.buscarClientesPorNombre(q);

        try (PrintWriter out = response.getWriter()) {
            StringBuilder json = new StringBuilder();
            json.append("[");

            for (int i = 0; i < resultados.size(); i++) {
                ClienteBusqueda c = resultados.get(i);
                if (i > 0) json.append(",");

                json.append("{");
                json.append("\"cedula\":\"").append(escapeJson(c.getCedula())).append("\",");
                json.append("\"nombre\":\"").append(escapeJson(c.getNombre())).append("\",");
                json.append("\"email\":\"").append(escapeJson(c.getEmail())).append("\",");
                json.append("\"tipo\":\"").append(escapeJson(c.getTipo())).append("\",");

                String carrera = c.getCarrera();
                if (carrera == null) carrera = "";
                json.append("\"carrera\":\"").append(escapeJson(carrera)).append("\"");
                json.append("}");
            }

            json.append("]");
            out.print(json.toString());
        }
    }

    // helper simple para escapar comillas en JSON
    private String escapeJson(String s) {
        if (s == null) return "";
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}