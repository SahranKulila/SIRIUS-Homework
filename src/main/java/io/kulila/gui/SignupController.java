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

    private final ClientFX client = new ClientFX();
    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    @FXML
    private void initialize() {
        client.start();
        signupButton.setOnAction(event -> handleSignup());
        goToLoginButton.setOnAction(event -> switchToLogin());
    }

    private void handleSignup() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (!password.equals(confirm)) {
            showAlert("Signup Failed", "Passwords do not match.");
            return;
        }

        JsonNode response = client.signup(username, password);
        String status = response.get("status").asText();
        if ("SUCCESS".equals(status)) {
            SceneLoader.loadScene((Stage) signupButton.getScene().getWindow(), "/io/kulila/gui/LoginController.fxml");
        } else {
            logger.error("Signup failed for user: {} - {}", username, response.get("message").asText());
            showAlert("Signup Failed", response.get("message").asText());
        }
    }

    private void switchToLogin() {
        SceneLoader.loadScene((Stage) signupButton.getScene().getWindow(), "/io/kulila/gui/LoginController.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
