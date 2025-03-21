package io.kulila.gui;

import io.kulila.client.ClientFX;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);
    private final ClientFX clientFX = new ClientFX();

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting MainApplication...");
        SceneLoader.loadScene(primaryStage, "/io/kulila/gui/LoginView.fxml", clientFX);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
