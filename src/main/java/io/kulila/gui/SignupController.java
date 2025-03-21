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
    private ClientFX clientFX;

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

    public void setClientFX(ClientFX clientFX) {
        this.clientFX = clientFX;
    }

    @FXML
    private void initialize() {
        signupButton.setOnAction(event -> handleSignup());
        goToLoginButton.setOnAction(event -> switchToLogin());
    }

    private void handleSignup() {
        if (clientFX == null) {
            logger.error("ClientFX instance is not set.");
            showAlert(Alert.AlertType.ERROR, "Client not initialized. Restart the application.");
            return;
        }

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
            if (responseNode != null && "SUCCESS".equals(responseNode.get("status").asText())) {
                showAlert(Alert.AlertType.INFORMATION, "Account created successfully!");
                switchToLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, "Signup failed.");
            }
        });
    }

    private void switchToLogin() {
        SceneLoader.loadScene((Stage) goToLoginButton.getScene().getWindow(), "/io/kulila/gui/LoginView.fxml", clientFX);
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }
}
