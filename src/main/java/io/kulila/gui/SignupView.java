package io.kulila.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class SignupView extends Application {
    private static final Logger logger = LoggerFactory.getLogger(SignupView.class);
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/io/kulila/gui/SignupController.fxml")));
            Scene loginScene = new Scene(loginRoot);
            primaryStage.setTitle("Signup");
            primaryStage.setScene(loginScene);
            primaryStage.setResizable(false);
            primaryStage.show();
            logger.info("Signup window launched.");


        } catch (Exception e) {
            logger.error("Failed to start application with both windows.", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
