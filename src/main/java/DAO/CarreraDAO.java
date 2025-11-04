package DAO;

import ConectionDB.ConnectionDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarreraDAO {

    public boolean crearCarrera(String nombre, int idSede) {
        String checkSql = "SELECT 1 FROM Carrera WHERE nombre = ? AND id_sede = ?";
        try {
            PreparedStatement psCheck = ConnectionDB.getInstancia().getConnection().prepareStatement(checkSql);
            psCheck.setString(1, nombre);
            psCheck.setInt(2, idSede);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) return false; // Ya existe
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar carrera existente", e);
        }

        String sql = "INSERT INTO Carrera(nombre, id_sede) VALUES(?, ?)";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setInt(2, idSede);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear carrera", e);
        }
    }

    public int obtenerIdCarreraPorNombre(String nombre) {
        String sql = "SELECT id_carrera FROM Carrera WHERE nombre = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_carrera");
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener id de carrera", e);
        }
    }

    public List<String> obtenerCarrerasConSede() {
        List<String> resultado = new ArrayList<>();
        String sql = "SELECT c.nombre as carrera, s.nombre as sede, s.departamento FROM Carrera c JOIN Sede s ON c.id_sede = s.id_sede";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String line = "Carrera: " + rs.getString("carrera") + " | Sede: " + rs.getString("sede") + " | Departamento: " + rs.getString("departamento");
                resultado.add(line);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar carreras", e);
        }
        return resultado;
    }

    // Devuelve filas estructuradas: [carrera, sede, departamento]
    public List<String[]> obtenerCarrerasConSedeTabla() {
        List<String[]> filas = new ArrayList<>();
        String sql = "SELECT c.nombre AS carrera, s.nombre AS sede, s.departamento AS departamento FROM Carrera c JOIN Sede s ON c.id_sede = s.id_sede ORDER BY s.nombre, c.nombre";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                filas.add(new String[]{ rs.getString("carrera"), rs.getString("sede"), rs.getString("departamento") });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar carreras con sede", e);
        }
        return filas;
    }
    public boolean eliminarCarrera(int idCarrera) {
        String sql = "DELETE FROM Carrera WHERE id_carrera = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setInt(1, idCarrera);
            int filas = ps.executeUpdate();
            return filas > 0; // true si eliminó al menos una fila
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar carrera", e);
        }
    }



}