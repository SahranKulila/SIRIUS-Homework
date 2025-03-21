package io.kulila.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
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
        loginButton.setOnAction(event -> switchToMainApp());
        createAccountButton.setOnAction(event -> switchToSignup());
    }

    private void switchToSignup() {
        SceneLoader.loadScene((Stage) createAccountButton.getScene().getWindow(), "/io/kulila/gui/SignupController.fxml");
    }

    private void switchToMainApp() {
        SceneLoader.loadScene((Stage) loginButton.getScene().getWindow(), "/io/kulila/gui/MainView.fxml");
    }
}
