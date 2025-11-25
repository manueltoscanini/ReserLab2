package com.example.appweb;

import com.example.appweb.util.MailService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import DAO.AdministradorDAO;
import Models.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Servlet para manejar consultas y reclamos de usuarios
@WebServlet(name = "ConsultaReclamoServlet", value = "/ConsultaReclamoServlet")
public class ConsultaReclamoServlet extends HttpServlet {

    // Instancia del DAO de Administrador
    private AdministradorDAO administradorDAO = new AdministradorDAO();
    private Gson gson = new Gson();

    // Maneja las solicitudes GET para obtener la lista de administradores
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar la respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Obtener lista de administradores
            List<Usuario> usuarios = administradorDAO.listarUsuarios();
            
            // Filtrar solo administradores
            List<Usuario> administradores = usuarios.stream()
                    .filter(Usuario::getEsAdmin)
                    .collect(Collectors.toList());
            
            // Crear lista simplificada para el frontend
            List<Map<String, String>> adminList = administradores.stream()
                    .map(admin -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("nombre", admin.getNombre());
                        map.put("email", admin.getEmail());
                        return map;
                    })
                    .collect(Collectors.toList());
            
            // Enviar respuesta JSON
            String json = gson.toJson(adminList);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Error al cargar administradores\"}");
            out.flush();
        }
    }

    // Maneja las solicitudes POST para enviar una consulta o reclamo
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar la respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Verificar si el usuario está autenticado
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("emailUsuario") == null) {
                enviarRespuesta(response, false, "No hay sesión activa");
                return;
            }
            
            // Obtener datos del usuario desde la sesión
            String nombreUsuario = (String) session.getAttribute("nombreUsuario");
            String emailUsuario = (String) session.getAttribute("emailUsuario");
            
            // Obtener parámetros del formulario
            String tipo = request.getParameter("tipo");
            String adminEmail = request.getParameter("adminEmail");
            String adminNombre = request.getParameter("adminNombre");
            String descripcion = request.getParameter("descripcion");
            
            // Validar parámetros
            if (tipo == null || adminEmail == null || descripcion == null || 
                tipo.isEmpty() || adminEmail.isEmpty() || descripcion.isEmpty()) {
                enviarRespuesta(response, false, "Faltan datos requeridos");
                return;
            }
            
            // Crear asunto del email
            String asunto = tipo.equals("consulta") ? 
                "Nueva Consulta de " + nombreUsuario : 
                "Nueva Queja/Reclamo de " + nombreUsuario;
            
            // Crear cuerpo del email
            String html = String.format("""
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f5f5f5;">
                    <div style="background: linear-gradient(135deg, #06295a 0%%, #027b8e 100%%); padding: 30px; border-radius: 10px 10px 0 0;">
                        <h2 style="color: white; margin: 0; font-size: 24px;">%s</h2>
                    </div>
                    <div style="background: white; padding: 30px; border-radius: 0 0 10px 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                        <div style="margin-bottom: 20px;">
                            <p style="color: #666; margin: 5px 0;"><strong style="color: #027b8e;">De:</strong> %s</p>
                            <p style="color: #666; margin: 5px 0;"><strong style="color: #027b8e;">Email:</strong> %s</p>
                            <p style="color: #666; margin: 5px 0;"><strong style="color: #027b8e;">Tipo:</strong> %s</p>
                        </div>
                        <div style="background-color: #f8f9fa; padding: 20px; border-left: 4px solid #027b8e; border-radius: 5px;">
                            <h3 style="color: #06295a; margin-top: 0;">Mensaje:</h3>
                            <p style="color: #333; line-height: 1.6; white-space: pre-wrap;">%s</p>
                        </div>
                        <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #e0e0e0; text-align: center;">
                            <p style="color: #999; font-size: 12px; margin: 0;">ReserLab - Sistema de Gestión de Reservas</p>
                        </div>
                    </div>
                </div>
                """,
                asunto,
                nombreUsuario,
                emailUsuario,
                tipo.equals("consulta") ? "Consulta" : "Queja/Reclamo",
                descripcion
            );
            
            // Enviar email de forma asíncrona
            MailService.sendHtmlAsync(adminEmail, asunto, html);
            
            // Responder éxito
            enviarRespuesta(response, true, "Mensaje enviado exitosamente");
            
        } catch (Exception e) {
            e.printStackTrace();
            enviarRespuesta(response, false, "Error al procesar la solicitud: " + e.getMessage());
        }
    }

    // Método auxiliar para enviar respuestas JSON
    private void enviarRespuesta(HttpServletResponse response, boolean success, String mensaje) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("mensaje", mensaje);
        
        String json = gson.toJson(result);
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}
