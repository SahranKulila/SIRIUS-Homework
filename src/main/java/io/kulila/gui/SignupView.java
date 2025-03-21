package io.kulila.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class SignupView extends Application {
    @Override
    public void start(Stage primaryStage) {
        SceneLoader.loadScene(primaryStage, "/io/kulila/gui/SignupController.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
