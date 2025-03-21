package io.kulila.gui;

import io.kulila.client.ClientFX;
import javafx.application.Application;
import javafx.stage.Stage;

public class SignupView extends Application {
    private final ClientFX clientFX = new ClientFX();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        SceneLoader.loadScene(primaryStage, "/io/kulila/gui/SignupView.fxml", clientFX);
    }
}
