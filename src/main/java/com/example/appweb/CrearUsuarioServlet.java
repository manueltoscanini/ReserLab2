package com.example.appweb;

import DAO.ClienteDAO;
import DAO.UsuarioDAO;
import Models.Hashed;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

// Servlet para crear un nuevo usuario (administrador o cliente)
@WebServlet(name = "CrearUsuarioServlet", value = "/crear-usuario")
public class CrearUsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    // Maneja las solicitudes POST para crear un nuevo usuario
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        try {
            // Obtener parámetros del formulario
            String nombre = trim(request.getParameter("nombre"));
            String email = trim(request.getParameter("email"));
            String cedula = trim(request.getParameter("cedula"));
            String password = trim(request.getParameter("password"));
            String password2 = trim(request.getParameter("password2"));
            String esAdminParam = request.getParameter("esAdmin");
            boolean esAdmin = "true".equals(esAdminParam);
            
            // Validaciones básicas
            if (isEmpty(nombre) || isEmpty(email) || isEmpty(cedula) || isEmpty(password) || isEmpty(password2)) {
                session.setAttribute("error", "Todos los campos son obligatorios.");
                response.sendRedirect("usuarios");
                return;
            }

            // Validar formato de email
            if (!emailValido(email)) {
                session.setAttribute("error", "El formato del email no es válido.");
                response.sendRedirect("usuarios");
                return;
            }

            // Validar que las contraseñas coincidan
            if (!password.equals(password2)) {
                session.setAttribute("error", "Las contraseñas no coinciden.");
                response.sendRedirect("usuarios");
                return;
            }

            // Verificar duplicados
            if (usuarioDAO.existeUsuario(email, cedula)) {
                session.setAttribute("error", "Ya existe un usuario con estos datos (email o cédula).");
                response.sendRedirect("usuarios");
                return;
            }

            // Si NO es admin, necesita datos de cliente
            if (!esAdmin) {
                String tipoCliente = trim(request.getParameter("tipoCliente"));
                String carreraNombre = trim(request.getParameter("carrera"));
                
                if (isEmpty(tipoCliente)) {
                    session.setAttribute("error", "Debe seleccionar el tipo de cliente.");
                    response.sendRedirect("usuarios");
                    return;
                }
                
                // Crear usuario
                usuarioDAO.crearUsuario(nombre, email, cedula, Hashed.encriptarContra(password));
                
                // Cliente: mapear tipo y carrera (solo estudiantes requieren carrera)
                Integer idCarrera = null;
                if ("estudiante".equalsIgnoreCase(tipoCliente)) {
                    if (isEmpty(carreraNombre)) {
                        session.setAttribute("error", "Debe seleccionar una carrera para estudiantes.");
                        response.sendRedirect("usuarios");
                        return;
                    }
                    // Obtener idCarrera desde el nombre
                    idCarrera = clienteDAO.obtenerIdCarreraPorNombre(carreraNombre);
                    if (idCarrera == null) {
                        session.setAttribute("error", "La carrera seleccionada no existe.");
                        response.sendRedirect("usuarios");
                        return;
                    }
                }

                // Insertar datos de cliente
                boolean ok = clienteDAO.insertarCliente(cedula, tipoCliente, idCarrera);
                if (!ok) {
                    session.setAttribute("error", "No se pudo registrar el cliente.");
                    response.sendRedirect("usuarios");
                    return;
                }
                
                session.setAttribute("exito", "Usuario creado exitosamente como " + tipoCliente + ".");
            } else {
                // Es admin - solo crear usuario sin datos de cliente
                // Primero crear el usuario normal
                usuarioDAO.crearUsuario(nombre, email, cedula, Hashed.encriptarContra(password));
                
                // Actualizar el campo es_admin
                String updateSql = "UPDATE usuario SET es_admin = true WHERE cedula = ?";
                try (var ps = ConectionDB.ConnectionDB.getInstancia().getConnection().prepareStatement(updateSql)) {
                    ps.setString(1, cedula);
                    ps.executeUpdate();
                } catch (Exception e) {
                    session.setAttribute("error", "Error al crear administrador: " + e.getMessage());
                    response.sendRedirect("usuarios");
                    return;
                }
                
                session.setAttribute("exito", "Administrador creado exitosamente.");
            }

            // Éxito: redirigir a usuarios
            response.sendRedirect("usuarios");
            
        } catch (Exception e) {
            System.err.println("Error en CrearUsuarioServlet: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error al crear usuario: " + e.getMessage());
            response.sendRedirect("usuarios");
        }
    }

    private static boolean isEmpty(String s) { 
        return s == null || s.isBlank(); 
    }
    
    private static String trim(String s) { 
        return s == null ? null : s.trim(); 
    }
    
    private static boolean emailValido(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}
