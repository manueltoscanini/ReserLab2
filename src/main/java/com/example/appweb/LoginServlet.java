package com.example.appweb;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Import de tu módulo 1 ya dentro del mismo proyecto
import DAO.UsuarioDAO;
import Models.Usuario;

import java.io.IOException;


@WebServlet(name = "LoginServlet", value = "/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO(); // Tu DAO real

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validar parámetros de entrada
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            response.getWriter().println("<h3>Error: Email y contraseña son requeridos</h3>");
            return;
        }

        try {
            System.out.println("Intentando autenticar usuario: " + email);
            Usuario usuario = usuarioDAO.autenticarUsuario(email, password);
            
            if (usuario != null) {
                System.out.println("Usuario autenticado exitosamente: " + usuario.getNombre());
                // Guardar el usuario en sesión
                request.getSession().setAttribute("usuario", usuario);

                // Redirigir según su rol
                if (usuario.getEsAdmin()) {
                    System.out.println("Redirigiendo a admin.jsp");
                    response.sendRedirect("admin.jsp");
                } else {
                    System.out.println("Redirigiendo a usuario.jsp");
                    response.sendRedirect("usuario.jsp");
                }
            } else {
                System.out.println("Autenticación fallida para: " + email);
                // Login fallido
                response.getWriter().println("<h3>Email o contraseña incorrectos</h3>");
            }
        } catch (Exception e) {
            System.err.println("Error en LoginServlet: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().println("<h3>Error al iniciar sesión: " + e.getMessage() + "</h3>");
            response.getWriter().println("<p>Detalles del error: " + e.getClass().getSimpleName() + "</p>");
        }
    }
}
