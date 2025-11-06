// registroServlet.java:
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
            redirectToRegistroWithMessage(response, "error", "Todos los campos marcados son obligatorios.", nombre, email, cedula, tipoCliente, carreraNombre);
            return;
        }

        if (!validarCedulaUruguaya(cedula)) {
            redirectToRegistroWithMessage(response, "error", "La cédula ingresada no es válida.", nombre, email, cedula, tipoCliente, carreraNombre);
            return;
        }

        if (!emailValido(email)) {
            redirectToRegistroWithMessage(response, "error", "El formato del email no es válido.", nombre, email, cedula, tipoCliente, carreraNombre);
            return;
        }

        if (!password.equals(password2)) {
            redirectToRegistroWithMessage(response, "error", "Las contraseñas no coinciden.", nombre, email, cedula, tipoCliente, carreraNombre);
            return;
        }

        // Duplicados
        if (usuarioDAO.existeUsuario(email, cedula)) {
            redirectToRegistroWithMessage(response, "error", "Ya existe un usuario con estos datos (email o cédula).",nombre, email, cedula, tipoCliente, carreraNombre);
            return;
        }

        // Reglas básicas de seguridad
        if (password.length() < 8 ||
                !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") ||
                !password.matches(".*\\d.*")) {

            redirectToRegistroWithMessage(response, "error", "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.", nombre, email, cedula, tipoCliente, carreraNombre);
            return;
        }

        try {
            // Crear usuario (no admin)
            usuarioDAO.crearUsuario(nombre, email, cedula, Hashed.encriptarContra(password));

            // Cliente: mapear tipo y carrera (solo estudiantes requieren carrera)
            Integer idCarrera = null;
            if ("estudiante".equalsIgnoreCase(tipoCliente)) {
                if (isEmpty(carreraNombre)) {
                    redirectToRegistroWithMessage(response, "error", "Seleccioná una carrera para estudiantes.", nombre, email, cedula, tipoCliente, carreraNombre);
                    return;
                }
                idCarrera = clienteDAO.obtenerIdCarreraPorNombre(carreraNombre);
                if (idCarrera == null) {
                    redirectToRegistroWithMessage(response, "error", "La carrera seleccionada no existe.", nombre, email, cedula, tipoCliente, carreraNombre);
                    return;
                }
            }

            boolean ok = clienteDAO.insertarCliente(cedula, tipoCliente, idCarrera);
            if (!ok) {
                redirectToRegistroWithMessage(response, "error", "No se pudo registrar el cliente.", nombre, email, cedula, tipoCliente, carreraNombre);
                return;
            }

            // Éxito: redirigir a login con mensaje de éxito
            redirectToLoginWithSuccessMessage(response, "Registro exitoso. Ahora podés iniciar sesión.");
        } catch (Exception e) {
            redirectToRegistroWithMessage(response, "error", "Error al registrar: " + e.getMessage(), nombre, email, cedula, tipoCliente, carreraNombre);
        }
    }

    // Método auxiliar para redirigir al formulario de registro con un mensaje de error
    private void redirectToRegistroWithMessage(HttpServletResponse response, String messageType, String message, 
            String nombre, String email, String cedula, String tipoCliente, String carreraNombre) throws IOException {
        // Primero obtener la lista de carreras
        List<String> carreras = null;
        try {
            carreras = clienteDAO.listarCarreras();
        } catch (Exception ignored) {}
        
        // Construir URL con parámetros
        StringBuilder url = new StringBuilder("registro.jsp?");
        url.append(messageType).append("=").append(URLEncoder.encode(message, StandardCharsets.UTF_8.toString()));
        
        if (nombre != null) {
            url.append("&nombre=").append(URLEncoder.encode(nombre, StandardCharsets.UTF_8.toString()));
        }
        if (email != null) {
            url.append("&email=").append(URLEncoder.encode(email, StandardCharsets.UTF_8.toString()));
        }
        if (cedula != null) {
            url.append("&cedula=").append(URLEncoder.encode(cedula, StandardCharsets.UTF_8.toString()));
        }
        if (tipoCliente != null) {
            url.append("&tipoCliente=").append(URLEncoder.encode(tipoCliente, StandardCharsets.UTF_8.toString()));
        }
        if (carreraNombre != null) {
            url.append("&carrera=").append(URLEncoder.encode(carreraNombre, StandardCharsets.UTF_8.toString()));
        }
        
        // Agregar las carreras como parámetros codificados
        if (carreras != null && !carreras.isEmpty()) {
            // Codificar la lista de carreras como un parámetro
            StringBuilder carrerasParam = new StringBuilder();
            for (int i = 0; i < carreras.size(); i++) {
                if (i > 0) carrerasParam.append(",");
                carrerasParam.append(URLEncoder.encode(carreras.get(i), StandardCharsets.UTF_8.toString()));
            }
            url.append("&carreras=").append(carrerasParam.toString());
        }
        
        response.sendRedirect(url.toString());
    }

    // Método auxiliar para redirigir al login con un mensaje de éxito
    private void redirectToLoginWithSuccessMessage(HttpServletResponse response, String message) throws IOException {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
        response.sendRedirect("login.jsp?exito=" + encodedMessage);
    }

    private static boolean isEmpty(String s) { return s == null || s.isBlank(); }
    private static String trim(String s) { return s == null ? null : s.trim(); }
    private static boolean emailValido(String email) {
        // Validación simple similar a AdministradorMenu.validarEmail
        return email != null && email.contains("@") && email.contains(".");
    }

    private static boolean validarCedulaUruguaya(String ci) {
        if (ci == null) return false;

        // 1) sacar puntos y guiones
        String limpia = ci.replace(".", "").replace("-", "").trim();

        // debe tener 7 u 8 dígitos (7 base + 1 verificador)
        if (limpia.length() < 7 || limpia.length() > 8) {
            return false;
        }

        // si tiene 7, le agregamos 0 adelante (casos viejos)
        if (limpia.length() == 7) {
            limpia = "0" + limpia;
        }

        // ahora sí son 8
        // primeros 7 = cuerpo, último = dígito verificador
        String cuerpo = limpia.substring(0, 7);
        int digitoVerificador;
        try {
            digitoVerificador = Integer.parseInt(limpia.substring(7));
        } catch (NumberFormatException e) {
            return false;
        }

        int[] pesos = {2, 9, 8, 7, 6, 3, 4};
        int suma = 0;

        for (int i = 0; i < 7; i++) {
            int dig = Character.getNumericValue(cuerpo.charAt(i));
            suma += dig * pesos[i];
        }

        int resto = suma % 10;
        int dvCalculado = (10 - resto) % 10;

        return dvCalculado == digitoVerificador;
    }
}