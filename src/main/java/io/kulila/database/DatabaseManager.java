package io.kulila.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String USER = "root";
    private static final String PASSWORD = "qazwsx";
    private static final String DB_NAME = "sirius_schema";

    private static final String JDBC_URL = "jdbc:mysql://"
                                            + HOST + ":"
                                            + PORT + "/"
                                            + DB_NAME + "?useSSL=false&serverTimezone=UTC";

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            logger.info("Connected to database: {}", DB_NAME);
            return connection;
        } catch (SQLException e) {
            logger.error("Database connection failed: {}", e.getMessage());
            return null;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed.");
            } catch (SQLException e) {
                logger.error("Error closing database connection: {}", e.getMessage());
            }
        }
    }
}
