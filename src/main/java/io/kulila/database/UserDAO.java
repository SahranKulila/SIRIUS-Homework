package io.kulila.database;

import io.kulila.dataclass.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword()); // Hashing later

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            logger.error("Error inserting user: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password); // Hashing later

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            logger.error("Error validating user: {}", e.getMessage());
            return false;
        }
    }
}
