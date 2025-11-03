package com.example.appweb;

import DAO.UsuarioDAO;
import Models.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "UsuarioServlet", value = "/usuarios")
public class UsuarioServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String pageParam = request.getParameter("page");
            String query = request.getParameter("q");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            int pageSize = 6; // 6 usuarios por página
            int offset = (page - 1) * pageSize;

            // Obtener usuarios (con o sin búsqueda)
            List<Usuario> todosLosUsuarios = (query != null && !query.trim().isEmpty())
                    ? usuarioDAO.buscarPorNombre(query.trim())
                    : usuarioDAO.getTodos();
            System.out.println("DEBUG: Total de usuarios encontrados: " + todosLosUsuarios.size());

            // Calcular información de paginación
            int totalUsuarios = todosLosUsuarios.size();
            int totalPages = (int) Math.ceil((double) totalUsuarios / pageSize);

            // Obtener usuarios para la página actual
            List<Usuario> usuariosPagina = todosLosUsuarios.subList(
                    Math.min(offset, totalUsuarios),
                    Math.min(offset + pageSize, totalUsuarios)
            );

            // Agregar datos al request
            request.setAttribute("usuarios", usuariosPagina);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalUsuarios", totalUsuarios);
            request.setAttribute("hasNextPage", page < totalPages);
            request.setAttribute("hasPrevPage", page > 1);
            if (query != null && !query.trim().isEmpty()) {
                request.setAttribute("q", query.trim());
            }

            System.out.println("DEBUG: Página " + page + " de " + totalPages + " con " + usuariosPagina.size() + " usuarios");

            // Redirigir a admin.jsp con los datos y la sección usuarios
            request.getRequestDispatcher("admin.jsp?section=usuarios").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error en UsuarioServlet: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().println("<h3>Error al cargar los usuarios: " + e.getMessage() + "</h3>");
        }
    }
}


