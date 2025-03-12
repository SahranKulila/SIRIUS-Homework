package io.kulila.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TestingController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}