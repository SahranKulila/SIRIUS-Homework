package io.kulila.gui;

import com.fasterxml.jackson.databind.JsonNode;
import io.kulila.client.ClientFX;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SignupController {
    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);
    private final ClientFX clientFX = new ClientFX();

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

    @FXML
    private void initialize() {
        signupButton.setOnAction(event -> handleSignup());
        goToLoginButton.setOnAction(event -> switchToLogin());
    }

    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match.");
            return;
        }

        Map<String, String> requestData = new HashMap<>();
        requestData.put("username", username);
        requestData.put("password", password);

        clientFX.sendJsonRequestFX("SIGNUP", requestData, responseNode -> {
            if ("SUCCESS".equals(responseNode.get("status").asText())) {
                showAlert(Alert.AlertType.INFORMATION, "Account created successfully! You can now log in.");
                switchToLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, responseNode.get("message").asText());
            }
        });
    }

    private void switchToLogin() {
        try {
            Stage stage = (Stage) signupButton.getScene().getWindow();
            SceneLoader.loadScene(stage, "LoginController.fxml");
        } catch (Exception e) {
            logger.error("Error switching to login screen: {}", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }
}
