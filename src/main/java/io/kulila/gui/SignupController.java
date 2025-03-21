package io.kulila.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

    @FXML
    private void initialize() {
        signupButton.setOnAction(event -> switchToLogin());
        goToLoginButton.setOnAction(event -> switchToLogin());
    }

    private void switchToLogin() {
        SceneLoader.loadScene((Stage) signupButton.getScene().getWindow(), "/io/kulila/gui/LoginController.fxml");
        SceneLoader.loadScene((Stage) goToLoginButton.getScene().getWindow(), "/io/kulila/gui/LoginController.fxml");
    }
}
