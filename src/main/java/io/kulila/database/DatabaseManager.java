package io.kulila.database;

import io.kulila.utils.YamlConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private static String HOST = "localhost";
    private static int PORT = 3306;
    private static String USER = "root";
    private static String PASSWORD = "qazwsx";
    private static String DB_NAME = "sirius_schema";
    private static String JDBC_URL;

    // Static block to load YAML config once at class loading
    static {
        loadConfiguration("database-config.yaml");
    }

    private static void loadConfiguration(String yamlFile) {
        try {
            YamlConfigurator.configure(DatabaseManager.class, yamlFile);
            logger.info("Loaded database configuration from {}", yamlFile);
        } catch (Exception e) {
            logger.error("Failed to load database configuration. Using defaults.", e);
        }
        updateJdbcUrl();
    }

    private static void updateJdbcUrl() {
        JDBC_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";
    }

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
