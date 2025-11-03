package com.example.appweb;

import DAO.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "EditarUsuarioServlet", value = "/editar-usuario")
public class EditarUsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String cedula = request.getParameter("cedula");
            String nombre = request.getParameter("nombre");
            String email = request.getParameter("email");

            if (cedula == null || cedula.isBlank()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Cédula es requerida\"}");
                return;
            }
            if ((nombre == null || nombre.isBlank()) && (email == null || email.isBlank())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Nombre o email requerido\"}");
                return;
            }

            // Leer actuales si falta alguno, para no setear null
            String nombreFinal = nombre != null && !nombre.isBlank() ? nombre.trim() : null;
            String emailFinal = email != null && !email.isBlank() ? email.trim() : null;

            // Si alguno viene null, obtenemos el usuario para completar valores
            if (nombreFinal == null || emailFinal == null) {
                var usuarioActual = usuarioDAO.buscarUsuarioPorEmail(emailFinal != null ? emailFinal : "");
                // Si no pudimos por email, no arriesgamos; exigimos ambos campos desde UI.
                if (usuarioActual == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"success\": false, \"message\": \"Datos insuficientes\"}");
                    return;
                }
                if (nombreFinal == null) nombreFinal = usuarioActual.getNombre();
                if (emailFinal == null) emailFinal = usuarioActual.getEmail();
            }

            boolean ok = usuarioDAO.actualizarUsuario(cedula, nombreFinal, emailFinal);
            if (ok) {
                response.getWriter().write("{\"success\": true}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"success\": false, \"message\": \"No se pudo actualizar\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String safeMsg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Error";
            response.getWriter().write("{\"success\": false, \"message\": \"" + safeMsg + "\"}");
        }
    }
}


