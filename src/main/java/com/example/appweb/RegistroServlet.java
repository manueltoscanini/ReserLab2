package com.example.appweb;

import DAO.ClienteDAO;
import DAO.UsuarioDAO;
import Models.Hashed;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "RegistroServlet", value = "/RegistroServlet")
public class RegistroServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Pre-cargar lista de carreras para el formulario (si existieran)
        try {
            List<String> carreras = clienteDAO.listarCarreras();
            request.setAttribute("carreras", carreras);
        } catch (Exception ignored) {}

        request.getRequestDispatcher("/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = trim(request.getParameter("nombre"));
        String email = trim(request.getParameter("email"));
        String cedula = trim(request.getParameter("cedula"));
        String password = trim(request.getParameter("password"));
        String password2 = trim(request.getParameter("password2"));
        String tipoCliente = trim(request.getParameter("tipoCliente"));
        String carreraNombre = trim(request.getParameter("carrera"));

        // Validaciones básicas siguiendo el flujo de LoginMenu/AdministradorMenu
        if (isEmpty(nombre) || isEmpty(email) || isEmpty(cedula) || isEmpty(password) || isEmpty(password2) || isEmpty(tipoCliente)) {
            request.setAttribute("error", "Todos los campos marcados son obligatorios.");
            doGet(request, response);
            return;
        }

        if (!emailValido(email)) {
            request.setAttribute("error", "El formato del email no es válido.");
            doGet(request, response);
            return;
        }

        if (!password.equals(password2)) {
            request.setAttribute("error", "Las contraseñas no coinciden.");
            doGet(request, response);
            return;
        }

        // Duplicados
        if (usuarioDAO.existeUsuario(nombre, email, cedula)) {
            request.setAttribute("error", "Ya existe un usuario con estos datos (nombre, email o cédula).");
            doGet(request, response);
            return;
        }

        try {
            // Crear usuario (no admin)
            usuarioDAO.crearUsuario(nombre, email, cedula, Hashed.encriptarContra(password));

            // Cliente: mapear tipo y carrera (solo estudiantes requieren carrera)
            Integer idCarrera = null;
            if ("estudiante".equalsIgnoreCase(tipoCliente)) {
                if (isEmpty(carreraNombre)) {
                    request.setAttribute("error", "Seleccioná una carrera para estudiantes.");
                    doGet(request, response);
                    return;
                }
                idCarrera = clienteDAO.obtenerIdCarreraPorNombre(carreraNombre);
                if (idCarrera == null) {
                    request.setAttribute("error", "La carrera seleccionada no existe.");
                    doGet(request, response);
                    return;
                }
            }

            boolean ok = clienteDAO.insertarCliente(cedula, tipoCliente, idCarrera);
            if (!ok) {
                request.setAttribute("error", "No se pudo registrar el cliente.");
                doGet(request, response);
                return;
            }

            // Éxito: redirigir a login
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } catch (Exception e) {
            request.setAttribute("error", "Error al registrar: " + e.getMessage());
            doGet(request, response);
        }
    }

    private static boolean isEmpty(String s) { return s == null || s.isBlank(); }
    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static boolean emailValido(String email) {
        // Validación simple similar a AdministradorMenu.validarEmail
        return email != null && email.contains("@") && email.contains(".");
    }
}


