package io.kulila.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DatabaseUtils {
//    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
//
//    private static final String DB_HOST = "localhost";
//    private static final int DB_PORT = 3306;
//    private static final String DB_USER = "root";
//    private static final String DB_PASSWORD = "qazwsx";
//    private static final String DB_NAME = "sirius_schema";
//    private static Connection connection = null;
//
//    private static final String JDBC_URL = String.format("jdbc:mysql://%s:%d/%s", DB_HOST, DB_PORT, DB_NAME);
//    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
//
//    public static Connection connect() throws SQLException {
//        if (connection == null || connection.isClosed()) {
//            try {
//                Class.forName(JDBC_DRIVER);
//                connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
//                logger.info("Database connected successfully to {}:{}", DB_HOST, DB_PORT);
//            } catch (ClassNotFoundException e) {
//                logger.error("MySQL JDBC driver not found: {}", e.getMessage());
//                throw new SQLException("MySQL JDBC driver not found", e);
//            }
//        }
//        return connection;
//    }
//
//    public static ResultSet executeQuery(String query) throws SQLException {
//        Connection conn = connect();
//        Statement stmt = conn.createStatement();
//        return stmt.executeQuery(query);
//    }
//
//    public static int executeUpdate(String query) throws SQLException {
//        Connection conn = connect();
//        Statement stmt = conn.createStatement();
//        return stmt.executeUpdate(query);
//    }
//
//    public static void close() {
//        if (connection != null) {
//            try {
//                connection.close();
//                logger.info("Database connection closed.");
//            } catch (SQLException e) {
//                logger.error("Error closing database connection: {}", e.getMessage());
//            }
//        }
//    }
}
