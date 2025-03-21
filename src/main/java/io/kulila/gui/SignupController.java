package io.kulila.gui;

import io.kulila.client.InlineClient;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignupController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button signupButton;
    @FXML
    private Button goToLoginButton;

    private final InlineClient client = new InlineClient();
    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    @FXML
    private void initialize() {
        client.connect();
        signupButton.setOnAction(event -> handleSignup());
        goToLoginButton.setOnAction(event -> switchToLogin());
    }

    private void handleSignup() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        if (!password.equals(confirm)) {
            logger.error("Signup failed: passwords do not match for user: {}", username);
            SceneLoader.loadScene((Stage) signupButton.getScene().getWindow(), "/io/kulila/gui/LoginController.fxml");
            return;
        }
        String checkQuery = "SELECT * FROM users WHERE username = '" + username + "'";
        String exists = client.sendMessage(checkQuery);
        if (exists != null && !exists.equals("null")) {
            logger.error("Signup failed: username already exists: {}", username);
        } else {
            String insertQuery = "INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "')";
            client.sendMessage(insertQuery);
        }
        SceneLoader.loadScene((Stage) signupButton.getScene().getWindow(), "/io/kulila/gui/LoginController.fxml");
    }

    private void switchToLogin() {
        SceneLoader.loadScene((Stage) signupButton.getScene().getWindow(), "/io/kulila/gui/LoginController.fxml");
    }
}
