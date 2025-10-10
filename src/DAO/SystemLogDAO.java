package DAO;

import ConectionDB.ConnectionDB;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SystemLogDAO {

    public boolean registrarAccion(String accion, Integer idReserva, String emailAdmin, String detalle) {
        String sql = "INSERT INTO systemlog(accion, fecha_accion, detalle, id_reserva, em_admin) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, accion);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            if (detalle == null || detalle.isEmpty()) {
                ps.setNull(3, java.sql.Types.LONGVARCHAR);
            } else {
                ps.setString(3, detalle);
            }
            if (idReserva == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, idReserva);
            }
            ps.setString(5, emailAdmin);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar en systemlog", e);
        }
    }
}


