package io.kulila.gui;

import io.kulila.database.ConnectionPool;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button createAccountButton;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        createAccountButton.setOnAction(event -> handleCreateAccount());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        logger.info("Username: {}", username);
        logger.info("Password: {}", password);
    }

    private void handleCreateAccount() {
        logger.info("Create Account button clicked");
    }
}
