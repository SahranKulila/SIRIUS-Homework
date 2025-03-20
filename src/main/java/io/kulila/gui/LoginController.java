package io.kulila.gui;

import com.fasterxml.jackson.databind.JsonNode;
import io.kulila.client.ClientFX;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final ClientFX clientFX = new ClientFX();

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

        Map<String, String> requestData = new HashMap<>();
        requestData.put("username", username);
        requestData.put("password", password);

        clientFX.sendJsonRequestFX("LOGIN", requestData, responseNode -> {
            if ("SUCCESS".equals(responseNode.get("status").asText())) {
                showAlert(Alert.AlertType.INFORMATION, "Login successful!");
                switchToMainApp();
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid username or password.");
            }
        });
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
