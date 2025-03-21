package io.kulila.gui;

import io.kulila.gui.config.Constants;
import io.kulila.gui.utils.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main3DApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main3DApplication.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting MainApplication...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/MainView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, Constants.APP_WIDTH, Constants.APP_HEIGHT);
            FXUtils.applyDarkTheme(scene);

            primaryStage.setTitle(Constants.APP_TITLE);
            FXUtils.setStageIcon(primaryStage, Constants.ICON_PATH);

            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

            logger.info("Application started successfully.");
        } catch (Exception e) {
            logger.error("Failed to start application.", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
