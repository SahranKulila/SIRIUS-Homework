package io.kulila.database;

import io.kulila.utils.YamlConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private static String JDBC_URL;

    private static final DatabaseConfig config = new DatabaseConfig();

    public static class DatabaseConfig {
        public String host;
        public int port;
        public String user;
        public String password;
        public String dbName;

        @Override
        public String toString() {
            return "DatabaseConfig{" +
                    "HOST='" + host + '\'' +
                    ", PORT=" + port +
                    ", USER='" + user + '\'' +
                    ", PASSWORD='" + password + '\'' +
                    ", DB_NAME='" + dbName + '\'' +
                    '}';
        }
    }

    static {
        loadConfiguration("database-config.yaml");
        logger.info("Loaded config {}", config);
    }

    private static void loadConfiguration(String yamlFile) {
        try {
            YamlConfigurator.configure(config, yamlFile);
            logger.info("Loaded database configuration from {}", yamlFile);
        } catch (Exception e) {
            logger.error("Failed to load database configuration. Using defaults.", e);
        }
        updateJdbcUrl();
    }

    private static void updateJdbcUrl() {
        JDBC_URL = "jdbc:mysql://" + config.host +
                ":" + config.port +
                "/" + config.dbName +
                "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    }

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, config.user, config.password);
            logger.info("Connected to database: {}", config.dbName);
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
