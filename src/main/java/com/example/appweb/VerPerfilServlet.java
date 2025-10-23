package com.example.appweb;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import DAO.ClienteDAO;
import DAO.CarreraDAO;
import Models.Usuario;

@WebServlet(name = "VerPerfilServlet", value = "/VerPerfilServlet")
public class VerPerfilServlet extends HttpServlet {

    private ClienteDAO clienteDAO = new ClienteDAO();
    private CarreraDAO carreraDAO = new CarreraDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String cedula = usuario.getCedula();

        try {
            // Datos del cliente
            String[] datos = clienteDAO.obtenerDatosClienteEstructurado(cedula);
            if (datos != null) {
                request.setAttribute("nombre", datos[0]);
                request.setAttribute("email", datos[1]);
                request.setAttribute("cedula", cedula);
                request.setAttribute("tipo", datos[2]);
                request.setAttribute("carrera", datos[3]);
            } else {
                request.setAttribute("error", "No se encontraron los datos del usuario.");
            }

            // Lista de carreras disponibles
            List<String> carreras = carreraDAO.obtenerCarrerasConSede() != null ?
                    carreraDAO.obtenerCarrerasConSede() : List.of();
            request.setAttribute("listaCarreras", carreras);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al obtener los datos del perfil: " + e.getMessage());
        }

        request.getRequestDispatcher("verPerfil.jsp").forward(request, response);
    }
}