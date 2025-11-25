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

// Servlet para manejar el inicio de sesión de usuarios
@WebServlet(name = "LoginServlet", value = "/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Maneja las solicitudes POST para el inicio de sesión
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
            // Intentar autenticar al usuario
            System.out.println("Intentando autenticar usuario: " + email);
            Usuario usuario = usuarioDAO.autenticarUsuario(email, password);

            if (usuario != null) {

                // Verificar si es cliente y está activo
                boolean activo = usuarioDAO.estaActivo(usuario.getCedula());
                if (!activo) {
                    response.sendRedirect("login.jsp?msg=cuentaDesactivada");
                    return;
                }

                System.out.println("Usuario autenticado exitosamente: " + usuario.getNombre());

                // Crear sesión y guardar datos del usuario
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);
                session.setAttribute("nombreUsuario", usuario.getNombre());
                session.setAttribute("emailUsuario", usuario.getEmail());
                session.setAttribute("ciUsuario", usuario.getCedula());
                session.setAttribute("fotoUsuario", usuario.getFotoUsuario());

                // Redirigir según su rol
                if (usuario.getEsAdmin()) {
                    System.out.println("Redirigiendo a admin.jsp");
                    response.sendRedirect("reservas");
                } else {
                    System.out.println("Redirigiendo a usuario.jsp");
                    response.sendRedirect("usuario.jsp");
                }
            } else {
                // Autenticación fallida
                response.sendRedirect("login.jsp?error=credenciales");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=servidor");
        }
    }
}
