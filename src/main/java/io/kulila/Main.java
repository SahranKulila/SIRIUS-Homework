package io.kulila;
import io.kulila.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("Testing");

        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            DatabaseManager.closeConnection(conn);
        }

//        String url = "jdbc:mysql://localhost:3306/sirius_schema";
//        String user = "root";
//        String password = "qazwsx";
//
//        Connection connection = null;
//
//        try {
//            connection = DriverManager.getConnection(url, user, password);
//            logger.info("Connected to the database successfully.");
//
//            Statement statement = connection.createStatement();
//
//            ResultSet res = statement.executeQuery("SELECT * FROM students");
//
//            while (res.next()) {
//                String name = res.getString("name");
//                System.out.println(name);
//            }
//            connection.close();
//
//
//        } catch (SQLException e) {
//            logger.error("Database operation failed: ", e);
//        } finally {
//            try {
//                if (connection != null && !connection.isClosed()) {
//                    connection.close();
//                    logger.info("Database connection closed.");
//                }
//            } catch (SQLException e) {
//                logger.error("Failed to close connection: ", e);
//            }
//        }

    }
}