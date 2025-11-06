package com.example.appweb;

import DAO.UsuarioDAO;
import Models.Usuario;
import com.example.appweb.util.MailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "RecuperarContraseniaServlet", value = "/RecuperarContraseniaServlet")
public class RecuperarContraseniaServlet extends HttpServlet {
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("=== INICIO RecuperarContraseniaServlet ===");
        
        // Establecer el tipo de contenido y codificación
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        
        System.out.println("Solicitud de recuperación de contraseña recibida");
        System.out.println("Email recibido del parámetro: '" + email + "'");
        
        try {
            // Validar que se haya proporcionado un email
            if (email == null || email.trim().isEmpty()) {
                System.out.println("ERROR: No se proporcionó un email");
                enviarRespuesta(response, false, "Por favor, ingrese su correo electrónico.");
                System.out.println("=== FIN RecuperarContraseniaServlet (ERROR) ===");
                return;
            }
            
            // Limpiar el email
            email = email.trim().toLowerCase();
            System.out.println("Email procesado: '" + email + "'");
            
            // Verificar si el usuario existe
            System.out.println("Buscando usuario en la base de datos...");
            Usuario usuario = usuarioDAO.buscarUsuarioPorEmail(email);
            if (usuario == null) {
                System.out.println("Usuario NO encontrado para el email: " + email);
                // Por seguridad, no revelamos si el email existe o no en la base de datos
                enviarRespuesta(response, true, "Si su correo está registrado, recibirá un enlace para restablecer su contraseña.");
                System.out.println("=== FIN RecuperarContraseniaServlet (USUARIO NO ENCONTRADO) ===");
                return;
            }
            
            System.out.println("Usuario encontrado: " + usuario.getNombre() + " (" + usuario.getEmail() + ")");
            
            // Generar un token único para restablecer la contraseña
            String token = UUID.randomUUID().toString();
            System.out.println("Token generado: " + token);
            
            // Guardar el token en la sesión (en una aplicación real, se guardaría en la base de datos)
            HttpSession session = request.getSession();
            session.setAttribute("resetToken", token);
            session.setAttribute("resetEmail", email);
            
            System.out.println("Token y email guardados en sesión");
            
            // Crear el enlace para restablecer la contraseña
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
            String resetLink = baseUrl + "restablecerContrasenia.jsp?token=" + token;
            
            System.out.println("URL de restablecimiento generada: " + resetLink);
            
            // Crear el contenido del correo usando el mismo enfoque que ConsultaReclamoServlet
            String asunto = "Restablecer Contraseña - ReserLab";
            String htmlContent = String.format("""
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f5f5f5;">
                    <div style="background: linear-gradient(135deg, #06295a 0%%, #027b8e 100%%); padding: 30px; border-radius: 10px 10px 0 0;">
                        <h2 style="color: white; margin: 0; font-size: 24px;">Restablecer Contraseña</h2>
                    </div>
                    <div style="background: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                        <div style="margin-bottom: 20px;">
                            <p style="color: #666; margin: 5px 0;"><strong style="color: #027b8e;">Hola:</strong> %s</p>
                        </div>
                        <div style="margin-bottom: 20px;">
                            <p style="color: #333; line-height: 1.6;">Hemos recibido una solicitud para restablecer la contraseña de su cuenta.</p>
                        </div>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" 
                               style="background: linear-gradient(135deg, #06295a 0%%, #027b8e 100%%); color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;">
                                Restablecer Contraseña
                            </a>
                        </div>
                        <div style="margin-bottom: 20px;">
                            <p style="color: #333; line-height: 1.6;">Si no puede hacer clic en el botón, copie y pegue el siguiente enlace en su navegador:</p>
                            <p style="word-break: break-all; color: #027b8e; background-color: #f8f9fa; padding: 10px; border-left: 4px solid #027b8e;">%s</p>
                        </div>
                        <div style="margin-bottom: 20px;">
                            <p style="color: #333; line-height: 1.6;"><strong>Este enlace expirará en 1 hora.</strong></p>
                            <p style="color: #333; line-height: 1.6;">Si no solicitó restablecer su contraseña, puede ignorar este correo.</p>
                        </div>
                        <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #e0e0e0; text-align: center;">
                            <p style="color: #999; font-size: 12px; margin: 0;">ReserLab - Sistema de Gestión de Reservas</p>
                        </div>
                    </div>
                </div>
                """, usuario.getNombre(), resetLink, resetLink);
            
            // Enviar el correo de forma asíncrona (como hace ConsultaReclamoServlet)
            System.out.println("Intentando enviar correo a: " + email);
            MailService.sendHtmlAsync(email, asunto, htmlContent);
            System.out.println("Correo enviado exitosamente (asíncrono)");
            
            // Responder éxito
            enviarRespuesta(response, true, "Si su correo está registrado, recibirá un enlace para restablecer su contraseña.");
            
        } catch (Exception e) {
            System.err.println("ERROR INTERNO DEL SERVIDOR:");
            e.printStackTrace();
            enviarRespuesta(response, false, "Error interno del servidor. Por favor, inténtelo más tarde.");
        }
        
        System.out.println("=== FIN RecuperarContraseniaServlet ===");
    }
    
    private void enviarRespuesta(HttpServletResponse response, boolean success, String mensaje) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("mensaje", mensaje);
        
        String json = new com.google.gson.Gson().toJson(result);
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}