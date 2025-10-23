//CambiarContraseniaServlet.java:
package com.example.appweb;

import DAO.UsuarioDAO;
import Models.Usuario;
import Models.Hashed;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "CambiarContraseniaServlet", value = "/CambiarContraseniaServlet")
public class CambiarContraseniaServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Enviar el JSP como modal
        request.getRequestDispatcher("cambiarContrasenia.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String email = usuario.getEmail();

        String actual = request.getParameter("actual");
        String nueva = request.getParameter("nueva");
        String confirmar = request.getParameter("confirmar");

        // Validaciones
        if (actual == null || nueva == null || confirmar == null ||
                actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            response.getWriter().write("error:Todos los campos son obligatorios.");
            return;
        }

        if (!nueva.equals(confirmar)) {
            response.getWriter().write("error:Las contraseñas nuevas no coinciden.");
            return;
        }

        // Reglas básicas de seguridad
        if (nueva.length() < 8 || !nueva.matches(".*[A-Z].*") ||
                !nueva.matches(".*[a-z].*") || !nueva.matches(".*\\d.*")) {
            response.getWriter().write("error:La nueva contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.");
            return;
        }

        try {
            // Verificar contraseña actual
            Usuario usuarioBD = usuarioDAO.buscarUsuarioPorEmail(email);
            if (usuarioBD == null) {
                response.getWriter().write("error:Usuario no encontrado.");
                return;
            }

            if (!Hashed.verificarContra(actual, usuarioBD.getContrasenia())) {
                response.getWriter().write("error:La contraseña actual es incorrecta.");
                return;
            }

            // Actualizar contraseña
            boolean actualizado = usuarioDAO.actualizarContrasenia(email, nueva);

            if (actualizado) {
                // Actualizar también en sesión
                usuario.setContrasenia(Hashed.encriptarContra(nueva));
                session.setAttribute("usuario", usuario);
                response.getWriter().write("exito:Contraseña actualizada correctamente.");
            } else {
                response.getWriter().write("error:No se pudo actualizar la contraseña.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("error:Error al cambiar la contraseña.");
        }
    }
}