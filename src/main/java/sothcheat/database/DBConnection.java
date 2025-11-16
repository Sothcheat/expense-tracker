package sothcheat.database;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker_db";
    private static final String USER = "root";
    private static final String PASS = "";
    private static final int CONNECTION_TIMEOUT = 5;
    private static final int SOCKET_TIMEOUT = 10;

    public static Connection getConnection() {
        try {
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASS);
            props.setProperty("connectTimeout", String.valueOf(CONNECTION_TIMEOUT * 1000)); // milliseconds
            props.setProperty("socketTimeout", String.valueOf(SOCKET_TIMEOUT * 1000)); // milliseconds
            props.setProperty("autoReconnect", "true");

            DriverManager.setLoginTimeout(CONNECTION_TIMEOUT);

            return DriverManager.getConnection(URL, props);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "Cannot connect to database. Please ensure:\n" +
                                "1. MySQL server is running\n" +
                                "2. Database 'expense_tracker_db' exists\n" +
                                "3. Connection settings are correct\n\n" +
                                "Error: " + e.getMessage(),
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            });

            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Test if database connection is working
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}