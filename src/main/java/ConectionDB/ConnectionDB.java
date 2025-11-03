//ConnectionDB.java:
package ConectionDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    private static ConnectionDB instancia;
    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/reserlab?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
    private final String user = "root";
    private final String password = "";

    private ConnectionDB() throws SQLException {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión a la base de datos establecida correctamente");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de MySQL no encontrado", e);
        } catch (SQLException e) {
            System.err.println("Error al conectarse a la base de datos: " + e.getMessage());
            throw new SQLException("Error al conectarse a la base de datos: " + e.getMessage(), e);
        }
    }

    public static ConnectionDB getInstancia() throws SQLException {
        if (instancia == null) {
            instancia = new ConnectionDB();
        }
        return instancia;
    }

    public Connection getConnection() {
        try {
            // Verificar si la conexión sigue siendo válida
            if (connection == null || connection.isClosed()) {
                instancia = null; // Forzar recreación
                return getInstancia().getConnection();
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión: " + e.getMessage());
            throw new RuntimeException("Error al obtener conexión", e);
        }
    }
}
