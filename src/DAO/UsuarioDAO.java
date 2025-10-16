package DAO;

import ConectionDB.ConnectionDB;
import Models.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.Hashed;
public class UsuarioDAO {

    public void mostrarCarreraUsuario(String cedula) {
        String sql = "SELECT carrera.nombre AS nc " +
                "FROM cliente JOIN carrera ON cliente.id_carrera = carrera.id_carrera " +
                "WHERE cliente.ci_usuario = ?";

        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println(rs.getString("nc"));
                } else {
                    System.out.println("No se encontró la carrera.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar carrera", e);
        }
    }
    public void crearUsuario(String nombre, String email, String cedula, String contrasenia) {
        String consulta = "INSERT INTO usuario(nombre, email, cedula, contrasenia) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);

            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, cedula);
            ps.setString(4, contrasenia);


            int filas = ps.executeUpdate();
            System.out.println("Filas insertadas: " + filas);

        } catch (Exception e) {
            e.printStackTrace(); // Ver error real
            throw new RuntimeException("Error al crear usuario", e);
        }
    }

    public Usuario autenticarUsuario(String correo, String contrasenia) {
        String consulta = """
    SELECT u.*, c.activo
    FROM usuario u
    LEFT JOIN cliente c ON u.cedula = c.ci_usuario
    WHERE u.email = ?
""";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta)) {
            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String contraseniaEncriptada = rs.getString("contrasenia");
                    Boolean esAdmin = rs.getBoolean("es_admin");
                    Integer activo = rs.getObject("activo", Integer.class);
                    
                    // Verificar si la contraseña ingresada coincide con la encriptada (salt + SHA-256)
                    if (Hashed.verificarContra(contrasenia, contraseniaEncriptada)) {
                        // Verificar si es admin o si es cliente activo
                        if (esAdmin || (activo != null && activo == 1)) {
                            return new Usuario(
                                rs.getString("nombre"),
                                rs.getString("email"),
                                rs.getString("cedula"),
                                rs.getString("contrasenia"),
                                esAdmin
                            );
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al autenticar usuario: " + e.getMessage(), e);
        }
    }

    // Método para listar todos los usuarios
    public java.util.List<Usuario> listarUsuarios() {
        String consulta = "SELECT * FROM usuario ORDER BY nombre";
        java.util.List<Usuario> usuarios = new java.util.ArrayList<>();
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("cedula"),
                        rs.getString("contrasenia"),
                        rs.getBoolean("es_admin")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al listar usuarios", e);
        }
        return usuarios;
    }

    // Método para buscar usuario por email
    public Usuario buscarUsuarioPorEmail(String email) {
        String consulta = "SELECT * FROM usuario WHERE email = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("cedula"),
                        rs.getString("contrasenia"),
                        rs.getBoolean("es_admin")
                );
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuario", e);
        }
    }

    // Método para actualizar email de usuario
    public boolean actualizarEmail(String emailActual, String nuevoEmail) {
        String consulta = "UPDATE usuario SET email = ? WHERE email = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            ps.setString(1, nuevoEmail);
            ps.setString(2, emailActual);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar email", e);
        }
    }

    // Método para actualizar nombre de usuario
    public boolean actualizarNombre(String email, String nuevoNombre) {
        String consulta = "UPDATE usuario SET nombre = ? WHERE email = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            ps.setString(1, nuevoNombre);
            ps.setString(2, email);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar nombre", e);
        }
    }

    // Método para actualizar cédula de usuario
    public boolean actualizarCedula(String email, String nuevaCedula) {
        String consulta = "UPDATE usuario SET cedula = ? WHERE email = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            ps.setString(1, nuevaCedula);
            ps.setString(2, email);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar cédula", e);
        }
    }

    // Método para actualizar contraseña de usuario
    public boolean actualizarContrasenia(String email, String nuevaContrasenia) {
        String consulta = "UPDATE usuario SET contrasenia = ? WHERE email = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            // Encriptar la nueva contraseña antes de guardarla
            ps.setString(1, Hashed.encriptarContra(nuevaContrasenia));
            ps.setString(2, email);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar contraseña", e);
        }
    }

    // Método para verificar si un email ya existe en la base de datos
    public boolean existeEmail(String email) {
        String consulta = "SELECT COUNT(*) FROM usuario WHERE email = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar email existente", e);
        }
    }

    // Método para verificar si un email ya existe excluyendo un email específico
    public boolean existeEmailExcluyendo(String email, String emailExcluir) {
        String consulta = "SELECT COUNT(*) FROM usuario WHERE email = ? AND email != ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            ps.setString(1, email);
            ps.setString(2, emailExcluir);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar email existente excluyendo actual", e);
        }
    }
    public boolean existeUsuario(String nombre, String email, String cedula) {
        String consulta = "SELECT COUNT(*) FROM usuario WHERE nombre = ? OR email = ? OR cedula = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);
            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, cedula);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al verificar usuario existente", e);
        }
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("cedula"),
                            rs.getString("contrasenia"),
                            rs.getBoolean("es_admin")
                    );
                }
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener usuario", e);
        }
    }

    // Método para probar la conexión a la base de datos
    public boolean probarConexion() {
        try {
            ConnectionDB.getInstancia().getConnection();
            System.out.println("Conexión a la base de datos exitosa");
            return true;
        } catch (Exception e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para verificar si existe un usuario con el email dado
    public boolean existeUsuarioPorEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ?";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al verificar existencia del usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}