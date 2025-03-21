package io.kulila.gui;

import io.kulila.client.ClientFX;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SceneLoader {
    private static final Logger logger = LoggerFactory.getLogger(SceneLoader.class);

    public static void loadScene(Stage stage, String fxmlFile, ClientFX clientFX) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlFile));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof LoginController loginController) {
                loginController.setClientFX(clientFX);
            } else if (controller instanceof MainViewController mainViewController) {
                mainViewController.setClientFX(clientFX);
            } else if (controller instanceof SignupController signupController) {
                signupController.setClientFX(clientFX);
            }

            stage.setScene(new Scene(root));
            stage.show();
            logger.info("Loaded scene: {}", fxmlFile);
        } catch (IOException e) {
            logger.error("Failed to load scene: {}", fxmlFile, e);
        }
    }
}
