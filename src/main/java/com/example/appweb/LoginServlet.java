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

        try {
            Usuario usuario = usuarioDAO.autenticarUsuario(email, password);
            if (usuario != null) {
                // Guardás el usuario en sesión
                request.getSession().setAttribute("usuario", usuario);

                // Redirigís según su rol
                if (usuario.getEsAdmin()) {
                    response.sendRedirect("admin.jsp");
                } else {
                    response.sendRedirect("usuario.jsp");
                }
            } else {
                // Login fallido
                response.getWriter().println("<h3>Email o contraseña incorrectos</h3>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h3>Error al iniciar sesión: " + e.getMessage() + "</h3>");
        }
    }
}
