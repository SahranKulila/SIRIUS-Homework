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

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button createAccountButton;

    private final ClientFX client = new ClientFX();
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private void initialize() {
        client.start();
        loginButton.setOnAction(event -> handleLogin());
        createAccountButton.setOnAction(event -> switchToSignup());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        JsonNode response = client.login(username, password);
        String status = response.get("status").asText();

        if ("SUCCESS".equals(status)) {
            SceneLoader.loadScene((Stage) loginButton.getScene().getWindow(), "/io/kulila/gui/MainView.fxml");
        } else {
            logger.error("Login failed for user: {} - {}", username, response.get("message").asText());
            showAlert("Login Failed", response.get("message").asText());
        }
    }

    private void switchToSignup() {
        SceneLoader.loadScene((Stage) createAccountButton.getScene().getWindow(), "/io/kulila/gui/SignupController.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
