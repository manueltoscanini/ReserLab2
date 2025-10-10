package DAO;

import ConectionDB.ConnectionDB;
import Models.Equipo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public void agregarEquipo(String nombre, String tipo, String precauciones) {
        String sql = "INSERT INTO EquipoLaboratorio(nombre, tipo, precauciones) VALUES (?, ?, ?)";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, tipo);
            ps.setString(3, precauciones);
            ps.executeUpdate();
            System.out.println("Equipo agregado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al agregar equipo.");
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
        String sql = "SELECT * FROM EquipoLaboratorio";
        try (PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipo equipo = new Equipo(
                        rs.getInt("id_equipo"),
                        rs.getString("nombre"),
                        rs.getString("tipo"),
                        rs.getString("precauciones")
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
            if (filas > 0) {
                System.out.println("✓ Equipo actualizado correctamente.");
                return true;
            } else {
                System.out.println("✗ No se encontró un equipo con ese ID.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al actualizar equipo.");
        }
        return false;
    }

























}