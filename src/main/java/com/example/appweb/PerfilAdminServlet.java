package com.example.appweb;

import DAO.UsuarioDAO;
import Models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

// Servlet para manejar el perfil del administrador
@WebServlet(name = "PerfilAdminServlet", value = "/perfil-admin")
public class PerfilAdminServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Maneja las solicitudes GET para mostrar el perfil del administrador
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar si el usuario es administrador
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Si no es administrador, redirigir al login
        if (usuario == null || !usuario.getEsAdmin()) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Redirigir a la página de perfil
        request.getRequestDispatcher("perfil-admin.jsp").forward(request, response);
    }

    // Maneja las solicitudes POST para actualizar el perfil del administrador
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar si el usuario es administrador
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Si no es administrador, redirigir al login
        if (usuario == null || !usuario.getEsAdmin()) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String email = request.getParameter("email");
            String passwordActual = request.getParameter("passwordActual");
            String passwordNueva = request.getParameter("passwordNueva");

            // Validar que los campos obligatorios estén presentes
            if (nombre == null || nombre.trim().isEmpty() || 
                email == null || email.trim().isEmpty()) {
                session.setAttribute("error", "El nombre y el email son obligatorios");
                response.sendRedirect("perfil-admin");
                return;
            }

            String cedula = usuario.getCedula();

            // Si se proporciona contraseña nueva, validar la actual
            if (passwordNueva != null && !passwordNueva.trim().isEmpty()) {
                if (passwordActual == null || passwordActual.trim().isEmpty()) {
                    session.setAttribute("error", "Debe ingresar la contraseña actual para cambiarla");
                    response.sendRedirect("perfil-admin");
                    return;
                }

                // Verificar que la contraseña actual sea correcta
                Usuario usuarioVerificado = usuarioDAO.autenticarUsuario(usuario.getEmail(), passwordActual);
                if (usuarioVerificado == null) {
                    session.setAttribute("error", "La contraseña actual es incorrecta");
                    response.sendRedirect("perfil-admin");
                    return;
                }

                // Actualizar con la nueva contraseña
                boolean actualizado = usuarioDAO.actualizarUsuarioConPassword(cedula, nombre, email, passwordNueva);
                if (actualizado) {
                    // Actualizar el usuario en la sesión
                    usuario.setNombre(nombre);
                    usuario.setEmail(email);
                    session.setAttribute("usuario", usuario);
                    session.setAttribute("nombreUsuario", nombre);
                    session.setAttribute("exito", "Perfil actualizado exitosamente (incluyendo contraseña)");
                } else {
                    session.setAttribute("error", "No se pudo actualizar el perfil");
                }
            } else {
                // Actualizar solo nombre y email
                boolean actualizado = usuarioDAO.actualizarUsuario(cedula, nombre, email);
                if (actualizado) {
                    // Actualizar el usuario en la sesión
                    usuario.setNombre(nombre);
                    usuario.setEmail(email);
                    session.setAttribute("usuario", usuario);
                    session.setAttribute("nombreUsuario", nombre);
                    session.setAttribute("exito", "Perfil actualizado exitosamente");
                } else {
                    session.setAttribute("error", "No se pudo actualizar el perfil");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }

        response.sendRedirect("perfil-admin");
    }
}
