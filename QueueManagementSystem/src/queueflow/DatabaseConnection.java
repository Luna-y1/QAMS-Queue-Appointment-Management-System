package queueflow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Singleton instance
    private static DatabaseConnection instance;
    private Connection connection;

    // Database credentials
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "qams_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin123";
    private static final String URL      =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
      + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    /**
     * Private constructor - prevents direct instantiation.
     */
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("[DB] Connected to " + DATABASE + " \u2713");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
        }
    }

    /**
     * Returns the single instance of DatabaseConnection.
     * Creates it if it does not yet exist.
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns the active SQL Connection.
     * Reconnects automatically if the connection was lost.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                instance = new DatabaseConnection();
                return instance.connection;
            }
        } catch (SQLException e) {
            System.err.println("[DB] Connection check failed: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Closes the connection safely.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}