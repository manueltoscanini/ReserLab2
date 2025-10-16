package DAO;

import ConectionDB.ConnectionDB;
import Models.Sede;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SedesDAO {
    public void crearSede(String nombre,String direccion,String departamento){

        String consulta = "INSERT INTO Sede(nombre,direccion,departamento) VALUES(?,?,?)";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);

            ps.setString(1,nombre);
            ps.setString(2,direccion);
            ps.setString(3,departamento);
            ps.executeUpdate();
            System.out.println("Sede agregada correctamante.");
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear Sede",e);
        }
    }

    // Devuelve id_sede o null si no existe
    public Integer obtenerIdPorNombre(String nombre) {
        String sql = "SELECT id_sede FROM Sede WHERE nombre = ?";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_sede");
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener id de sede", e);
        }
    }

    // Cuenta cuántas carreras están asociadas a la sede
    public int contarCarrerasPorId(int idSede) {
        String sql = "SELECT COUNT(*) FROM Carrera WHERE id_sede = ?";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idSede);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar carreras para sede", e);
        }
    }

    // Elimina la sede por nombre, comprobando dependencias antes
    public boolean eliminarSedePorNombre(String nombre) {
        Integer id = obtenerIdPorNombre(nombre);
        if (id == null) {
            System.out.println("No existe una sede con ese nombre.");
            return false;
        }

        int carreras = contarCarrerasPorId(id);
        if (carreras > 0) {
            System.out.println("No se puede eliminar la sede: hay " + carreras +
                    " carrera(s) asociada(s). Elimine esas carreras primero.");
            return false;
        }

        String sql = "DELETE FROM Sede WHERE id_sede = ?";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Sede eliminada correctamente.");
                return true;
            } else {
                System.out.println("No se eliminó la sede.");
                return false;
            }
        } catch (SQLIntegrityConstraintViolationException fkEx) {
            // excepción específica por FK
            throw new RuntimeException("No se puede eliminar la sede por restricciones de integridad (FK).", fkEx);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar Sede", e);
        }
    }

    public boolean modificarSede(String nombreActual, String nuevoNombre, String nuevaDireccion, String nuevoDepartamento) {
        String consulta = "UPDATE Sede SET nombre = ?, direccion = ?, departamento = ? WHERE nombre = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(consulta);

            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevaDireccion);
            ps.setString(3, nuevoDepartamento);
            ps.setString(4, nombreActual);

            int filasAfectadas = ps.executeUpdate();

            return filasAfectadas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al modificar Sede", e);
        }
    }


    public List<Sede> obtenerSedes() {
        List<Sede> sedes = new ArrayList<>();
        String sql = "SELECT * FROM Sede";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Sede s = new Sede(
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("departamento")
                );
                sedes.add(s);
            }
        } catch (SQLException e){
            throw new RuntimeException("Error al obtener sedes", e);
        }
        return sedes;
    }
}
