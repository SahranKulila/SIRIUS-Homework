package io.kulila.gui;

import io.kulila.database.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserDAO userDAO = new UserDAO();

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
        createAccountButton.setOnAction(event -> switchToSignup());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Username and password are required.");
            return;
        }

        boolean isValid = userDAO.validateUser(username, password);
        if (isValid) {
            showAlert(Alert.AlertType.INFORMATION, "Login successful!");
            switchToMainApp();
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid username or password.");
        }
    }

    private void switchToSignup() {
        try {
            Stage stage = (Stage) createAccountButton.getScene().getWindow();
            SceneLoader.loadScene(stage, "SignupController.fxml");
        } catch (Exception e) {
            logger.error("Error switching to signup screen: {}", e.getMessage());
        }
    }

    private void switchToMainApp() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            SceneLoader.loadScene(stage, "MainView.fxml"); // Main app UI
        } catch (Exception e) {
            logger.error("Error switching to main application: {}", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }
}
