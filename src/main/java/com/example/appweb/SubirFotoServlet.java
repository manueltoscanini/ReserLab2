package com.example.appweb;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import DAO.UsuarioDAO;
import Models.Usuario;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@WebServlet(name = "SubirFotoServlet", value = "/SubirFotoServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class SubirFotoServlet extends HttpServlet {

    private Cloudinary cloudinary;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        // Configurar Cloudinary con tus credenciales
        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dsqanvus6",
            "api_key", "824789719428873",
            "api_secret", "vVUx94cBCeEGGr6WRuZAIe7ZqpU"
        ));
        
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"No hay sesión activa\"}");
            return;
        }

        try {
            // Obtener el archivo subido
            Part filePart = request.getPart("foto");
            
            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"No se seleccionó ninguna foto\"}");
                return;
            }

            // Validar tipo de archivo
            String contentType = filePart.getContentType();
            if (!contentType.startsWith("image/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"El archivo debe ser una imagen\"}");
                return;
            }

            // Subir a Cloudinary
            InputStream fileContent = filePart.getInputStream();
            Map uploadResult = cloudinary.uploader().upload(fileContent.readAllBytes(), 
                ObjectUtils.asMap(
                    "folder", "reserlab/usuarios",
                    "transformation", new com.cloudinary.Transformation()
                        .width(300).height(300).crop("fill").gravity("face")
                ));

            String fotoUrl = (String) uploadResult.get("secure_url");
            
            // Actualizar en la base de datos
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            boolean actualizado = usuarioDAO.actualizarFotoUsuario(usuario.getEmail(), fotoUrl);
            
            if (actualizado) {
                // Actualizar el objeto usuario en la sesión
                usuario.setFotoUsuario(fotoUrl);
                session.setAttribute("usuario", usuario);
                session.setAttribute("fotoUsuario", fotoUrl); // ✅ Actualizar también el atributo separado
                
                response.getWriter().write("{\"success\": true, \"fotoUrl\": \"" + fotoUrl + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"success\": false, \"message\": \"Error al actualizar la base de datos\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Error al subir la foto: " + e.getMessage() + "\"}");
        }
    }
}
