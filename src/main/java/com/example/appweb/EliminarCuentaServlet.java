//EliminarCuentaServlet.java:
package com.example.appweb;

import ConectionDB.ConnectionDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(name = "EliminarCuentaServlet", value = "/EliminarCuentaServlet")
public class EliminarCuentaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("ciUsuario") == null) {
            response.getWriter().write("error:sesionInvalida");
            return;
        }

        String ciUsuario = (String) session.getAttribute("ciUsuario");

        try (Connection conn = ConnectionDB.getInstancia().getConnection()) {
            String sql = "UPDATE Cliente SET activo = 0 WHERE ci_Usuario = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ciUsuario);
            int filas = ps.executeUpdate();

            if (filas > 0) {
                // Cerrar sesión
                session.invalidate();
                response.getWriter().write("exito:cuentaDesactivada");
            } else {
                response.getWriter().write("error:noSeActualizo");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("error:" + e.getMessage());
        }
    }
}