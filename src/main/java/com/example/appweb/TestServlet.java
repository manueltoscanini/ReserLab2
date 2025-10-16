package com.example.appweb;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import DAO.UsuarioDAO;
import Models.Usuario;

import java.io.IOException;

@WebServlet(name = "TestServlet", value = "/TestServlet")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<h2>Prueba de Conexión y Autenticación</h2>");
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        
        // Probar conexión
        response.getWriter().println("<h3>1. Probando conexión a la base de datos...</h3>");
        boolean conexionOK = usuarioDAO.probarConexion();
        if (conexionOK) {
            response.getWriter().println("<p style='color: green;'>✓ Conexión exitosa</p>");
        } else {
            response.getWriter().println("<p style='color: red;'>✗ Error en la conexión</p>");
            return;
        }
        
        // Probar si existe un usuario de prueba
        String emailPrueba = "admin@test.com";
        response.getWriter().println("<h3>2. Verificando si existe usuario de prueba...</h3>");
        boolean existeUsuario = usuarioDAO.existeUsuarioPorEmail(emailPrueba);
        if (existeUsuario) {
            response.getWriter().println("<p style='color: green;'>✓ Usuario encontrado: " + emailPrueba + "</p>");
        } else {
            response.getWriter().println("<p style='color: orange;'>⚠ Usuario no encontrado: " + emailPrueba + "</p>");
        }
        
        // Probar autenticación
        response.getWriter().println("<h3>3. Probando autenticación...</h3>");
        try {
            Usuario usuario = usuarioDAO.autenticarUsuario(emailPrueba, "password123");
            if (usuario != null) {
                response.getWriter().println("<p style='color: green;'>✓ Autenticación exitosa</p>");
                response.getWriter().println("<p>Usuario: " + usuario.getNombre() + "</p>");
                response.getWriter().println("<p>Email: " + usuario.getEmail() + "</p>");
                response.getWriter().println("<p>Es Admin: " + usuario.getEsAdmin() + "</p>");
            } else {
                response.getWriter().println("<p style='color: red;'>✗ Autenticación fallida</p>");
            }
        } catch (Exception e) {
            response.getWriter().println("<p style='color: red;'>✗ Error en autenticación: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        
        response.getWriter().println("<br><a href='login.jsp'>Volver al Login</a>");
    }
}

