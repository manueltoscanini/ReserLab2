package DAO;

import ConectionDB.ConnectionDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void actualizarCarrera(Integer id_carrera, String cedula) {
        String sql = "UPDATE cliente SET id_carrera = ? WHERE ci_usuario = ?";
        try (PreparedStatement ps =
                     ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {

            ps.setInt(1, id_carrera);
            ps.setString(2, cedula);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar carrera", e);
        }
    }
    public List<String> listarCarreras() {
        String sql = "SELECT nombre FROM carrera ORDER BY nombre";
        List<String> carreras = new ArrayList<>();
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                carreras.add(rs.getString("nombre"));
            }
            return carreras;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar carreras", e);
        }
    }

    public Integer obtenerIdCarreraPorNombre(String nombre) {
        String sql = "SELECT id_carrera FROM carrera WHERE nombre = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_carrera");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener id de carrera", e);
        }
    }

    public boolean insertarCliente(String cedulaUsuario, String tipoCliente, Integer idCarrera) {
        String sql = "INSERT INTO cliente(ci_usuario, tipo_cliente, id_carrera,activo) VALUES (?, ?, ?,?)";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, cedulaUsuario);
            ps.setString(2, tipoCliente.toLowerCase());

            // Manejar idCarrera que puede ser null
            if (idCarrera != null) {
                ps.setInt(3, idCarrera);
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4,"1");
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar cliente", e);
        }
    }

    public String obtenerDatosClientePorCedula(String cedula) {
        String sql = "SELECT u.nombre, u.email, c.tipo_cliente, ca.nombre AS carrera FROM cliente c JOIN usuario u ON u.cedula = c.ci_usuario LEFT JOIN carrera ca ON ca.id_carrera = c.id_carrera WHERE c.ci_usuario = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                String tipo = rs.getString("tipo_cliente");
                String carrera = rs.getString("carrera");
                return nombre + " | CI: " + cedula + " | Email: " + email + " | Tipo: " + tipo + (carrera != null ? " | Carrera: " + carrera : "");
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener datos del cliente", e);
        }
    }

    public boolean desactivarCliente(String cedula) {
        String sql = "UPDATE cliente SET activo = 0 WHERE ci_usuario = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, cedula);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al desactivar cliente", e);
        }
    }
}


