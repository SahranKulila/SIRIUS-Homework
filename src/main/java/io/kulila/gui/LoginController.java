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

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private ClientFX clientFX;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button createAccountButton;

    public void setClientFX(ClientFX clientFX) {
        this.clientFX = clientFX;
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        createAccountButton.setOnAction(event -> switchToSignup());
    }

    private void handleLogin() {
        if (clientFX == null) {
            logger.error("ClientFX instance is not set.");
            showAlert(Alert.AlertType.ERROR, "Client not initialized. Restart the application.");
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Username and password are required.");
            return;
        }

        Map<String, String> requestData = new HashMap<>();
        requestData.put("username", username);
        requestData.put("password", password);

        clientFX.sendJsonRequestFX("LOGIN", requestData, responseNode -> {
            if (responseNode != null && "SUCCESS".equals(responseNode.get("status").asText())) {
                showAlert(Alert.AlertType.INFORMATION, "Login successful!");
                switchToMainApp();
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid username or password.");
            }
        });
    }

    private void switchToSignup() {
        SceneLoader.loadScene((Stage) createAccountButton.getScene().getWindow(), "/io/kulila/gui/SignupView.fxml", clientFX);
    }

    private void switchToMainApp() {
        SceneLoader.loadScene((Stage) loginButton.getScene().getWindow(), "/io/kulila/gui/MainView.fxml", clientFX);
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }
}
