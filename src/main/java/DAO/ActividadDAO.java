package DAO;

import ConectionDB.ConnectionDB;
import Models.Actividad;
import Models.EquipoUso;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActividadDAO {

    public boolean existeClientePorCedula(String cedula) {
        String sql = "SELECT 1 FROM cliente WHERE ci_usuario = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar cliente", e);
        }
    }

    public String buscarEmail(int idActividad) {
        String sql = """
        SELECT u.email
        FROM actividad a
        JOIN cliente c ON c.ci_usuario = a.ci_cliente
        JOIN usuario u ON u.cedula = c.ci_usuario
        WHERE a.id_actividad = ?
    """;

        try (PreparedStatement ps = ConnectionDB.getInstancia()
                .getConnection().prepareStatement(sql)) {
            ps.setInt(1, idActividad);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar email por idActividad", e);
        }

        return null; // si no se encuentra el email
    }

    public boolean esUsuarioAdminPorEmail(String email) {
        String sql = "SELECT es_admin FROM usuario WHERE email = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("es_admin");
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar si el usuario es admin", e);
        }
    }
    public boolean aprobarActividad(int idActividad) {
        String sql = "UPDATE actividad SET estado = 'aceptada' WHERE id_actividad = ? AND estado = 'en_espera'";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setInt(1, idActividad);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al aprobar actividad", e);
        }
    }
    public static Integer crearReservaYObtenerId(LocalDate fecha,
                                          Time horaInicio,
                                          Time horaFin,
                                          int cantParticipantes,
                                          String cedulaCliente,
                                          String estado) {
        String sql = "INSERT INTO actividad(fecha, hora_inicio, hora_fin, estado, cant_participantes, ci_cliente) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ps.setTime(2, horaInicio);
            ps.setTime(3, horaFin);
            ps.setString(4, estado);
            ps.setInt(5, cantParticipantes);
            ps.setString(6, cedulaCliente);
            int filas = ps.executeUpdate();
            if (filas == 0) return null;
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear la reserva", e);
        }
    }

    public boolean vincularEquipoAActividad(int idActividad, int idEquipo, String uso) {
        String sql = "INSERT INTO actividad_equipo(id_actividad, id_equipo, uso) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setInt(1, idActividad);
            ps.setInt(2, idEquipo);
            ps.setString(3, uso);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al vincular equipo a actividad", e);
        }
    }


    public List<Actividad> getHistorialPorUsuario(String ciCliente) {
        List<Actividad> historial = new ArrayList<>();
        String sql = """
        SELECT a.id_actividad, a.fecha, a.hora_inicio, a.hora_fin, a.estado,
               a.cant_participantes, a.ci_cliente,
               u.nombre AS nombre_usuario, ca.nombre AS carrera_nombre
        FROM Actividad a
        JOIN Cliente c ON a.ci_cliente = c.ci_Usuario
        JOIN Usuario u ON c.ci_Usuario = u.cedula
        LEFT JOIN Carrera ca ON c.id_carrera = ca.id_carrera
        WHERE a.ci_cliente = ?
        ORDER BY a.fecha, a.hora_inicio
        """;
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, ciCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer cantParticipantesObj = (Integer) rs.getObject("cant_participantes");
                int cantParticipantes = (cantParticipantesObj == null) ? 0 : cantParticipantesObj;

                Actividad act = new Actividad(
                        rs.getInt("id_actividad"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora_inicio"),
                        rs.getTime("hora_fin"),
                        rs.getString("estado"),
                        cantParticipantes,
                        rs.getString("ci_cliente"),
                        rs.getString("nombre_usuario"),
                        rs.getString("carrera_nombre")
                );
                historial.add(act);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener historial", e);
        }
        return historial;
    }

    public List<Actividad> getTodas() {
        List<Actividad> lista = new ArrayList<>();
        String sql = "SELECT a.id_actividad, a.fecha, a.hora_inicio, a.hora_fin, a.estado, " +
                "a.cant_participantes, a.ci_cliente, " +
                "u.nombre AS nombre_usuario, ca.nombre AS carrera_nombre " +
                "FROM Actividad a " +
                "JOIN Cliente c ON a.ci_cliente = c.ci_Usuario " +
                "JOIN Usuario u ON c.ci_Usuario = u.cedula " +
                "LEFT JOIN Carrera ca ON c.id_carrera = ca.id_carrera " +
                "ORDER BY a.fecha DESC, a.hora_inicio DESC";

        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer cantParticipantesObj = (Integer) rs.getObject("cant_participantes");
                int cantParticipantes = (cantParticipantesObj == null) ? 0 : cantParticipantesObj;

                Actividad act = new Actividad(
                        rs.getInt("id_actividad"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora_inicio"),
                        rs.getTime("hora_fin"),
                        rs.getString("estado"),
                        cantParticipantes,
                        rs.getString("ci_cliente"),
                        rs.getString("nombre_usuario"),   // nombre del cliente
                        rs.getString("carrera_nombre")
                );
                lista.add(act);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener actividades", e);
        }
        return lista;
    }

    public List<Actividad> getPorFecha(LocalDate fecha) {
        List<Actividad> lista = new ArrayList<>();
        String sql = """
        SELECT a.id_actividad, a.fecha, a.hora_inicio, a.hora_fin, a.estado,
               a.cant_participantes, a.ci_cliente,
               u.nombre AS nombre_usuario, ca.nombre AS carrera_nombre
        FROM Actividad a
        JOIN Cliente c ON a.ci_cliente = c.ci_Usuario
        JOIN Usuario u ON c.ci_Usuario = u.cedula
        LEFT JOIN Carrera ca ON c.id_carrera = ca.id_carrera
        WHERE a.fecha = ?
        ORDER BY a.hora_inicio DESC
        """;
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer cantParticipantesObj = (Integer) rs.getObject("cant_participantes");
                int cantParticipantes = (cantParticipantesObj == null) ? 0 : cantParticipantesObj;

                Actividad act = new Actividad(
                        rs.getInt("id_actividad"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora_inicio"),
                        rs.getTime("hora_fin"),
                        rs.getString("estado"),
                        cantParticipantes,
                        rs.getString("ci_cliente"),
                        rs.getString("nombre_usuario"),
                        rs.getString("carrera_nombre")
                );
                lista.add(act);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener actividades por fecha", e);
        }
        return lista;
    }

    public Actividad obtenerActividadPorId(int id) {
        String sql =  "SELECT a.id_actividad, a.fecha, a.hora_inicio, a.hora_fin, a.estado, " +
                "a.cant_participantes, a.ci_cliente, u.nombre AS nombreCliente, ca.nombre AS carreraCliente " +
                "FROM actividad a " +
                "JOIN cliente c ON a.ci_cliente = c.ci_usuario " +
                "JOIN usuario u ON c.ci_usuario = u.cedula " +
                "LEFT JOIN carrera ca ON c.id_carrera = ca.id_carrera " +
                "WHERE a.id_actividad = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Actividad(
                        rs.getInt("id_actividad"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora_inicio"),
                        rs.getTime("hora_fin"),
                        rs.getString("estado"),
                        rs.getInt("cant_participantes"),
                        rs.getString("ci_cliente"),
                        rs.getString("nombreCliente"),
                        rs.getString("carreraCliente")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener actividad por id", e);
        }
    }

    public java.util.List<EquipoUso> obtenerEquiposDeActividad(int idActividad) {
        String sql = "SELECT e.id_equipo, e.nombre, e.tipo, ae.uso FROM actividad_equipo ae JOIN equipolaboratorio e ON e.id_equipo = ae.id_equipo WHERE ae.id_actividad = ?";
        java.util.List<EquipoUso> lista = new java.util.ArrayList<>();
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setInt(1, idActividad);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new EquipoUso(
                        rs.getInt("id_equipo"),
                        rs.getString("nombre"),
                        rs.getString("tipo"),
                        rs.getString("uso")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener equipos de la actividad", e);
        }
        return lista;
    }
    public static boolean existeConflictoReserva(LocalDate fecha, Time horaInicio, Time horaFin) {
        String sql = "SELECT 1 FROM actividad " +
                "WHERE fecha = ? " +
                "AND hora_inicio < ? " +
                "AND hora_fin > ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ps.setTime(2, horaFin);
            ps.setTime(3, horaInicio);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar conflicto de reserva", e);
        }
    }

    public List<Actividad> reservasActivasPorCi(String cedula, String estado) {
        List<Actividad> lista = new ArrayList<>();
        String sql = "SELECT a.id_actividad, a.fecha, a.hora_inicio, a.hora_fin, a.estado, " +
                "a.cant_participantes, a.ci_cliente, " +
                "u.nombre AS nombre_usuario, s.nombre AS sede_nombre " +
                "FROM Actividad a " +
                "JOIN Cliente c ON a.ci_cliente = c.ci_Usuario " +
                "JOIN Usuario u ON c.ci_Usuario = u.cedula " +
                "LEFT JOIN Carrera ca ON c.id_carrera = ca.id_carrera " +
                "LEFT JOIN Sede s ON ca.id_sede = s.id_sede " +
                "WHERE a.ci_cliente = ? AND a.estado = ? " +
                "ORDER BY a.fecha, a.hora_inicio";

        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, cedula);
            ps.setString(2, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer cantParticipantesObj = (Integer) rs.getObject("cant_participantes");
                int cantParticipantes = (cantParticipantesObj == null) ? 0 : cantParticipantesObj;

                Actividad act = new Actividad(
                        rs.getInt("id_actividad"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora_inicio"),
                        rs.getTime("hora_fin"),
                        rs.getString("estado"),
                        cantParticipantes,
                        rs.getString("ci_cliente"),
                        rs.getString("nombre_usuario"),   // nombre del cliente
                        rs.getString("sede_nombre")
                );
                lista.add(act);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener actividades", e);
        }
        return lista;
    }

    public List<Actividad> historialReservasPorCi(String cedula) {
        List<Actividad> lista = new ArrayList<>();
        String sql = "SELECT a.id_actividad, a.fecha, a.hora_inicio, a.hora_fin, a.estado, " +
                "a.cant_participantes, a.ci_cliente, " +
                "u.nombre AS nombre_usuario, s.nombre AS sede_nombre " +
                "FROM Actividad a " +
                "JOIN Cliente c ON a.ci_cliente = c.ci_Usuario " +
                "JOIN Usuario u ON c.ci_Usuario = u.cedula " +
                "LEFT JOIN Carrera ca ON c.id_carrera = ca.id_carrera " +
                "LEFT JOIN Sede s ON ca.id_sede = s.id_sede " +
                "WHERE a.ci_cliente = ? " +
                "ORDER BY a.fecha ASC, a.hora_inicio ASC";

        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer cantParticipantesObj = (Integer) rs.getObject("cant_participantes");
                int cantParticipantes = (cantParticipantesObj == null) ? 0 : cantParticipantesObj;

                Actividad act = new Actividad(
                        rs.getInt("id_actividad"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora_inicio"),
                        rs.getTime("hora_fin"),
                        rs.getString("estado"),
                        cantParticipantes,
                        rs.getString("ci_cliente"),
                        rs.getString("nombre_usuario"),   // nombre del cliente
                        rs.getString("sede_nombre")
                );
                lista.add(act);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener actividades", e);
        }
        return lista;
    }

    public boolean actualizarReserva(int idActividad, LocalDate fecha, Time horaInicio, Time horaFin, int cantParticipantes) {
        String sql = "UPDATE actividad SET fecha = ?, hora_inicio = ?, hora_fin = ?, cant_participantes = ? WHERE id_actividad = ? AND estado IN ('en_espera','aceptada')";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ps.setTime(2, horaInicio);
            ps.setTime(3, horaFin);
            ps.setInt(4, cantParticipantes);
            ps.setInt(5, idActividad);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la reserva", e);
        }
    }

    public boolean eliminarEquiposDeActividad(int idActividad) {
        String sql = "DELETE FROM actividad_equipo WHERE id_actividad = ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setInt(1, idActividad);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar equipos de la actividad", e);
        }
    }

    public boolean existeConflictoReservaExcluyendo(LocalDate fecha, Time horaInicio, Time horaFin, int idActividadExcluir) {
        String sql = "SELECT 1 FROM actividad " +
                "WHERE fecha = ? " +
                "AND hora_inicio < ? " +
                "AND hora_fin > ? " +
                "AND id_actividad != ?";
        try {
            PreparedStatement ps = ConnectionDB.getInstancia().getConnection().prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ps.setTime(2, horaFin);
            ps.setTime(3, horaInicio);
            ps.setInt(4, idActividadExcluir);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar conflicto de reserva", e);
        }
    }

    public void refrescarEstados() {
        // 1) aceptada -> en_curso si es hoy y estamos entre inicio y fin
        String sqlEnCurso = """
        UPDATE actividad
        SET estado = 'en_curso'
        WHERE estado = 'aceptada'
          AND fecha = CURRENT_DATE
          AND CURRENT_TIME BETWEEN hora_inicio AND hora_fin
    """;

        // 2) aceptada/en_curso -> finalizada si ya terminó (ayer o antes, o hoy y pasó la hora_fin)
        String sqlFinalizada = """
        UPDATE actividad
        SET estado = 'finalizada'
        WHERE estado IN ('aceptada','en_curso')
          AND (
               fecha < CURRENT_DATE
            OR (fecha = CURRENT_DATE AND CURRENT_TIME > hora_fin)
          )
    """;

        try (var conn = ConnectionDB.getInstancia().getConnection()) {
            try (var ps1 = conn.prepareStatement(sqlEnCurso)) { ps1.executeUpdate(); }
            try (var ps2 = conn.prepareStatement(sqlFinalizada)) { ps2.executeUpdate(); }
        } catch (SQLException e) {
            throw new RuntimeException("Error al refrescar estados", e);
        }
    }
}


