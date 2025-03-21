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

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button createAccountButton;

    private final InlineClient client = new InlineClient();
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private void initialize() {
        client.connect();
        loginButton.setOnAction(event -> handleLogin());
        createAccountButton.setOnAction(event -> switchToSignup());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
        String response = client.sendMessage(query);
        if (response != null && !response.equals("null")) {
            SceneLoader.loadScene((Stage) loginButton.getScene().getWindow(), "/io/kulila/gui/MainView.fxml");
        } else {
            logger.error("Login failed for user: {}", username);
            SceneLoader.loadScene((Stage) loginButton.getScene().getWindow(), "/io/kulila/gui/MainView.fxml");
        }
    }

    private void switchToSignup() {
        SceneLoader.loadScene((Stage) createAccountButton.getScene().getWindow(), "/io/kulila/gui/SignupController.fxml");
    }
}
