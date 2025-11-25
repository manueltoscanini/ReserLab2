package com.example.appweb;

import DAO.EquipoDAO;
import Models.Equipo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Servlet para editar un equipo existente
@MultipartConfig
@WebServlet(name = "EditarEquipoServlet", value = "/EditarEquipoServlet")
public class EditarEquipoServlet extends HttpServlet {

    private final EquipoDAO equipoDAO = new EquipoDAO();

    // Maneja las solicitudes GET para mostrar el formulario de edición de equipo
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("==== EditarEquipoServlet.doGet ====");

        // Obtener el ID del equipo desde los parámetros de la solicitud
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Falta el id");
            return;
        }

        try {
            // Parsear el ID y obtener el equipo del DAO
            int id = Integer.parseInt(idStr);
            Equipo eq = equipoDAO.obtenerEquipoPorId(id);
            if (eq == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("No se encontró el equipo");
                return;
            }

            // Enviar el equipo al JSP para edición
            request.setAttribute("equipo", eq);
            request.getRequestDispatcher("/admin/editarEquipo.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("ID inválido");
        }
    }

    // Maneja las solicitudes POST para procesar la edición del equipo
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar la codificación y el tipo de contenido de la respuesta
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // Obtener parámetros del formulario
        String idStr = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String tipo = request.getParameter("tipo");
        String precauciones = request.getParameter("precauciones");


        System.out.println("==== EditarEquipoServlet.doPost ====");
        System.out.println("id        = " + idStr);
        System.out.println("nombre    = " + nombre);
        System.out.println("tipo      = " + tipo);
        System.out.println("precauc.  = " + precauciones);
        System.out.println("====================================");

        // si vino algún null es porque el servlet NO está recibiendo bien el form
        if (idStr == null || idStr.isEmpty()
                || nombre == null || nombre.isEmpty()
                || tipo == null || tipo.isEmpty()) {
            response.getWriter().write("{\"success\":false,\"message\":\"Parámetros incompletos en servlet\"}");
            return;
        }

        try {
            // parsea el id
            int id = Integer.parseInt(idStr);

            // llama al DAO normal
            boolean ok = equipoDAO.editarEquipo(id, nombre, tipo, precauciones);

            if (ok) {
                response.getWriter().write("{\"success\":true}");
            } else {
                // si llega acá es porque el UPDATE dio 0 filas
                response.getWriter().write("{\"success\":false,\"message\":\"No se actualizó ninguna fila (id no existe o igual a lo que había)\"}");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"ID no numérico\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"Error interno en el servidor\"}");
        }
    }
}
