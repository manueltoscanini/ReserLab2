package DAO;

import ConectionDB.ConnectionDB;
import Models.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdministradorDAO {

    public static void crearUsuario(String nombre, String email, String cedula, String contrasenia, boolean es_admin) {
        String consulta = "INSERT INTO usuario(nombre, email, cedula, contrasenia,es_admin) VALUES (?, ?, ?, ?,?)";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);

            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, cedula);
            ps.setString(4, contrasenia);
            ps.setBoolean(5,es_admin);

            int filas = ps.executeUpdate();
            System.out.println("Filas insertadas: " + filas);

        } catch (Exception e) {
            e.printStackTrace(); // Ver error real
            throw new RuntimeException("Error al crear usuario", e);
        }
    }
    public boolean existeUsuario(String email){
        String consulta = "SELECT * FROM usuario WHERE email=?";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta)) {

            ps.setString(1,email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar Usuario");
        }
    }

    public void eliminarUsuario(String email) {
        String consulta = "UPDATE cliente SET activo = 0 WHERE ci_Usuario = ( SELECT cedula FROM usuario WHERE email = ?)";

        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta)) {
            ps.setString(1, email);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException("El usuario no es un cliente o no existe.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al desactivar el cliente", e);
        }
    }



    public static List<Usuario> buscarUsuarios(String nombre){

        List<Usuario> usuarios = new ArrayList<>();
        String consulta = "SELECT nombre,email,cedula,es_admin FROM usuario WHERE nombre=?";

        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            ps.setString(1,nombre);
            ResultSet rs = ps.executeQuery();


            while (rs.next()) {

                Usuario u = new Usuario();
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setCedula(rs.getString("cedula"));
                u.setEsadmin(rs.getBoolean("es_admin"));
                usuarios.add(u);
            }


        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar Usuario");
        }

        return usuarios;

    }
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String consulta = "SELECT u.nombre, u.email, u.cedula, u.es_admin FROM usuario u LEFT JOIN cliente c ON u.cedula = c.ci_Usuario WHERE u.es_admin = 1 OR c.activo = 1;";

        try {

            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Usuario u = new Usuario();
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setCedula(rs.getString("cedula"));
                u.setEsadmin(rs.getBoolean("es_admin"));
                usuarios.add(u);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios", e);
        }

        return usuarios;
    }
}