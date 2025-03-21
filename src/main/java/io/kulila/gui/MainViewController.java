package io.kulila.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainViewController {
    @FXML
    private Button btnLogout;

    @FXML
    private void initialize() {
        btnLogout.setOnAction(event -> handleLogout());
    }

    private void handleLogout() {
        SceneLoader.loadScene((Stage) btnLogout.getScene().getWindow(), "/io/kulila/gui/LoginController.fxml");
    }
}
