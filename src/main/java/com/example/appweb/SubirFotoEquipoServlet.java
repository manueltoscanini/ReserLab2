package com.example.appweb;

import DAO.EquipoDAO;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@WebServlet(name = "SubirFotoEquipoServlet", value = "/SubirFotoEquipoServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class SubirFotoEquipoServlet extends HttpServlet {

    private Cloudinary cloudinary;
    private EquipoDAO equipoDAO;

    @Override
    public void init() throws ServletException {
        // Configurar Cloudinary con tus credenciales
        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dsqanvus6",
            "api_key", "824789719428873",
            "api_secret", "vVUx94cBCeEGGr6WRuZAIe7ZqpU"
        ));
        
        equipoDAO = new EquipoDAO();
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
            // Obtener el ID del equipo
            String idEquipoStr = request.getParameter("idEquipo");
            if (idEquipoStr == null || idEquipoStr.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"ID de equipo no especificado\"}");
                return;
            }
            
            int idEquipo = Integer.parseInt(idEquipoStr);
            
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
                    "folder", "reserlab/equipos",
                    "transformation", new com.cloudinary.Transformation()
                        .width(600).height(400).crop("fill")
                ));

            String fotoUrl = (String) uploadResult.get("secure_url");
            
            // Actualizar en la base de datos
            boolean actualizado = equipoDAO.actualizarFotoEquipo(idEquipo, fotoUrl);
            
            if (actualizado) {
                response.getWriter().write("{\"success\": true, \"fotoUrl\": \"" + fotoUrl + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"success\": false, \"message\": \"Error al actualizar la base de datos\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"message\": \"ID de equipo inválido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Error al subir la foto: " + e.getMessage() + "\"}");
        }
    }
}
