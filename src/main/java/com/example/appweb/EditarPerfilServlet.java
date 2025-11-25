//EditarPerfilServlet.java:
package com.example.appweb;

import DAO.ClienteDAO;
import DAO.UsuarioDAO;
import DAO.CarreraDAO;
import ConectionDB.ConnectionDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import Models.Usuario;

// Servlet para editar el perfil del usuario
@WebServlet(name = "EditarPerfilServlet", value = "/EditarPerfilServlet")
public class EditarPerfilServlet extends HttpServlet {

    // Instancias de los DAOs necesarios
    private ClienteDAO clienteDAO = new ClienteDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private CarreraDAO carreraDAO = new CarreraDAO();

    // Maneja las solicitudes GET para mostrar el formulario de edición de perfil
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar si el usuario está autenticado
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Obtener el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String cedula = usuario.getCedula();

        try {
            // Obtener datos del cliente
            String[] datos = clienteDAO.obtenerDatosClienteEstructurado(cedula);
            if (datos != null) {
                request.setAttribute("nombre", datos[0]);
                request.setAttribute("email", datos[1]);
                request.setAttribute("tipo", datos[2]);
                request.setAttribute("carrera", datos[3]);
                // Lista de carreras para el select
                List<String> carreras = clienteDAO.listarCarreras();
                request.setAttribute("listaCarreras", carreras);
            } else {
                request.setAttribute("error", "No se encontraron datos del cliente.");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar datos: " + e.getMessage());
        }

        if (request.getAttribute("desdeEdicion") != null) {
            request.getRequestDispatcher("verPerfil.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("editarPerfil.jsp").forward(request, response);
        }
    }

    // Maneja las solicitudes POST para actualizar el perfil del usuario
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar si el usuario está autenticado
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Obtener el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String cedula = usuario.getCedula();
        String email = usuario.getEmail();

        String nuevoNombre = request.getParameter("nombre");
        String nuevoTipo = request.getParameter("tipo_cliente");
        String nuevaCarrera = request.getParameter("carrera");

        try {
            boolean actualizado = false;

            // Actualizar nombre si se proporcionó
            if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                System.out.println("Actualizando nombre de: " + usuario.getNombre() + " a: " + nuevoNombre);
                boolean nombreActualizado = usuarioDAO.actualizarNombre(email, nuevoNombre);
                System.out.println("Nombre actualizado en BD: " + nombreActualizado);
                if (nombreActualizado) {
                    usuario.setNombre(nuevoNombre);
                    actualizado = true;
                }
            }

            // Actualizar tipo de cliente si se proporcionó
            if (nuevoTipo != null && !nuevoTipo.trim().isEmpty()) {
                System.out.println("Actualizando tipo de cliente a: " + nuevoTipo);
                
                // Actualizar tipo de cliente
                String sql = "UPDATE cliente SET tipo_cliente = ? WHERE ci_usuario = ?";
                var ps = ConnectionDB.getInstancia()
                        .getConnection().prepareStatement(sql);
                ps.setString(1, nuevoTipo.toLowerCase());
                ps.setString(2, cedula);
                int filasAfectadas = ps.executeUpdate();
                System.out.println("Filas afectadas en cliente (tipo): " + filasAfectadas);
                
                // Manejar carrera según el tipo
                if (nuevoTipo.equalsIgnoreCase("estudiante")) {
                    if (nuevaCarrera != null && !nuevaCarrera.trim().isEmpty()) {
                        System.out.println("Actualizando carrera a: " + nuevaCarrera);
                        Integer idCarrera = clienteDAO.obtenerIdCarreraPorNombre(nuevaCarrera);
                        System.out.println("ID de carrera encontrado: " + idCarrera);
                        if (idCarrera != null) {
                            clienteDAO.actualizarCarrera(idCarrera, cedula);
                            System.out.println("Carrera actualizada en BD");
                        }
                    }
                } else {
                    System.out.println("Eliminando carrera (no es estudiante)");
                    clienteDAO.actualizarCarrera(null, cedula);
                }
                
                actualizado = true;
            }

            if (actualizado) {
                // Actualizar la sesión con los datos modificados
                session.setAttribute("usuario", usuario);
                request.setAttribute("mensaje", "Datos actualizados correctamente.");
            } else {
                request.setAttribute("mensaje", "No se realizaron cambios.");
            }
            
            // Obtener los datos actualizados para mostrar en el perfil
            String[] datos = clienteDAO.obtenerDatosClienteEstructurado(cedula);
            if (datos != null) {
                request.setAttribute("nombre", datos[0]);
                request.setAttribute("email", datos[1]);
                request.setAttribute("cedula", cedula);
                request.setAttribute("tipo", datos[2]);
                request.setAttribute("carrera", datos[3]);
            }
            
            // Lista de carreras disponibles
            List<String> carreras = carreraDAO.obtenerCarrerasConSede() != null ?
                    carreraDAO.obtenerCarrerasConSede() : List.of();
            request.setAttribute("listaCarreras", carreras);

            // Redirigir al perfil actualizado
            request.getRequestDispatcher("verPerfil.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al actualizar: " + e.getMessage());
            doGet(request, response);
        }
    }
}