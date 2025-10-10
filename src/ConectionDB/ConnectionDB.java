package ConectionDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    private static ConnectionDB instancia;
    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/reserlab";
    private final String user = "root";
    private final String password = "";

    private ConnectionDB() throws SQLException {
        try {
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new SQLException("Error al conectarse a la base de datos", e);
        }
    }

    public static ConnectionDB getInstancia() throws SQLException {
        if (instancia == null) {
            instancia = new ConnectionDB();
        }
        return instancia;
    }

    public Connection getConnection() {
        return connection;
    }
}
