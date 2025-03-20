package io.kulila.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class SceneLoader {
    private static final Logger logger = LoggerFactory.getLogger(SceneLoader.class);

    public static void loadScene(Stage stage, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneLoader.class.getResource(fxmlFile)));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            logger.info("Loaded scene: {}", fxmlFile);
        } catch (IOException e) {
            logger.error("Failed to load scene: {}", fxmlFile, e);
        }
    }
}
