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

@WebServlet(name = "ResetearContraseniaServlet", value = "/ResetearContraseniaServlet")
public class ResetearContraseniaServlet extends HttpServlet {
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        String nuevaContrasenia = request.getParameter("nuevaContrasenia");
        String confirmarContrasenia = request.getParameter("confirmarContrasenia");
        
        // Validar parámetros
        if (token == null || token.trim().isEmpty() || 
            nuevaContrasenia == null || nuevaContrasenia.trim().isEmpty() ||
            confirmarContrasenia == null || confirmarContrasenia.trim().isEmpty()) {
            response.sendRedirect("restablecerContrasenia.jsp?error=contrasenia");
            return;
        }
        
        // Validar que las contraseñas coincidan
        if (!nuevaContrasenia.equals(confirmarContrasenia)) {
            response.sendRedirect("restablecerContrasenia.jsp?error=contrasenia");
            return;
        }
        
        // Validar que la contraseña no esté vacía
        if (nuevaContrasenia.length() == 0) {
            response.sendRedirect("restablecerContrasenia.jsp?error=contrasenia");
            return;
        }
        
        try {
            // Verificar el token (en una aplicación real, se verificaría contra la base de datos)
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect("restablecerContrasenia.jsp?error=token");
                return;
            }
            
            String sessionToken = (String) session.getAttribute("resetToken");
            String email = (String) session.getAttribute("resetEmail");
            
            if (sessionToken == null || email == null || !sessionToken.equals(token)) {
                response.sendRedirect("restablecerContrasenia.jsp?error=token");
                return;
            }
            
            // Verificar que el usuario aún existe
            Usuario usuario = usuarioDAO.buscarUsuarioPorEmail(email);
            if (usuario == null) {
                response.sendRedirect("restablecerContrasenia.jsp?error=token");
                return;
            }
            
            // Actualizar la contraseña
            boolean actualizado = usuarioDAO.actualizarContrasenia(email, nuevaContrasenia);
            
            if (actualizado) {
                // Limpiar los atributos de sesión
                session.removeAttribute("resetToken");
                session.removeAttribute("resetEmail");
                
                // Redirigir a la página de éxito
                response.sendRedirect("restablecerContrasenia.jsp?msg=exito");
            } else {
                response.sendRedirect("restablecerContrasenia.jsp?error=contrasenia");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("restablecerContrasenia.jsp?error=contrasenia");
        }
    }
}