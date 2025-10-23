//LoginServlet.java:
package com.example.appweb;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

                // ✅ Crear sesión y guardar datos del usuario
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);
                session.setAttribute("nombreUsuario", usuario.getNombre());  // usado en usuario.jsp
                session.setAttribute("emailUsuario", usuario.getEmail());    // usado para consultas/reclamos
                session.setAttribute("ciUsuario", usuario.getCedula());      // opcional, útil para consultas
                session.setAttribute("fotoUsuario", usuario.getFotoUsuario()); // URL de la foto

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
