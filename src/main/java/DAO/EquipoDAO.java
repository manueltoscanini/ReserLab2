package DAO;

import ConectionDB.ConnectionDB;
import Models.Equipo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public boolean agregarEquipo(String nombre, String tipo, String precauciones) {
        String sql = "INSERT INTO EquipoLaboratorio(nombre, tipo, precauciones, foto_equipo, activo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, tipo);
            ps.setString(3, precauciones);
            ps.setString(4, "https://res.cloudinary.com/dsqanvus6/image/upload/v1761750756/images_vheoul.png); // Initially null, can be updated later");
            ps.setInt(5, 1); // activo = 1 por defecto
            int filas = ps.executeUpdate();
            System.out.println("Equipo agregado correctamente.");
            return filas > 0;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al agregar equipo.");
            return false;
        }
    }


    public void eliminarEquipo(int id) {
        String sql = "DELETE FROM EquipoLaboratorio WHERE id_equipo = ?";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Equipo eliminado.");
            } else {
                System.out.println("No se encontró un equipo con ese ID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al eliminar equipo.");
        }
    }

    public List<Equipo> obtenerEquipos() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT * FROM EquipoLaboratorio WHERE activo = 1";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo equipo = new Equipo(
                        rs.getInt("id_equipo"),
                        rs.getString("nombre"),
                        rs.getString("tipo"),
                        rs.getString("precauciones"),
                        rs.getString("foto_equipo")
                );
                lista.add(equipo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean editarEquipo(int id, String nuevoNombre, String nuevoTipo, String nuevasPrecauciones) {
        String sql = "UPDATE EquipoLaboratorio SET nombre = ?, tipo = ?, precauciones = ? WHERE id_equipo = ?";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevoTipo);
            ps.setString(3, nuevasPrecauciones);
            ps.setInt(4, id);
            int filas = ps.executeUpdate();
            System.out.println("[EquipoDAO] UPDATE filas=" + filas + " para id=" + id);
            if (filas > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizarFotoEquipo(int idEquipo, String fotoUrl) {
        String sql = "UPDATE EquipoLaboratorio SET foto_equipo = ? WHERE id_equipo = ?";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setString(1, fotoUrl);
            ps.setInt(2, idEquipo);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("✓ Foto de equipo actualizada correctamente.");
                return true;
            } else {
                System.out.println("✗ No se encontró un equipo con ese ID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al actualizar foto de equipo.");
        }
        return false;
    }

    public Equipo obtenerEquipoPorId(int id) {
        String sql = "SELECT * FROM EquipoLaboratorio WHERE id_equipo = ?";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Equipo obtenida correctamente.");
                    return new Equipo(
                            rs.getInt("id_equipo"),
                            rs.getString("nombre"),
                            rs.getString("tipo"),
                            rs.getString("precauciones"),
                            rs.getString("foto_equipo")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}