package io.kulila.gui;

import io.kulila.gui.config.Constants;
import io.kulila.gui.utils.FXUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LoginView extends Application {
    private static final Logger logger = LoggerFactory.getLogger(LoginView.class);
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/io/kulila/gui/LoginController.fxml")));
            Scene loginScene = new Scene(loginRoot);
            primaryStage.setTitle("Login");
            primaryStage.setScene(loginScene);
            primaryStage.setResizable(false);
            primaryStage.show();
            logger.info("Login window launched.");


            Stage mainStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/MainView.fxml"));
            Parent mainRoot = loader.load();
            Scene mainScene = new Scene(mainRoot, Constants.APP_WIDTH, Constants.APP_HEIGHT);
            FXUtils.applyDarkTheme(mainScene);
            mainStage.setTitle(Constants.APP_TITLE);
            FXUtils.setStageIcon(mainStage, Constants.ICON_PATH);
            mainStage.setScene(mainScene);
            mainStage.setResizable(true);
            mainStage.show();
            logger.info("Main3DApplication window launched.");

        } catch (Exception e) {
            logger.error("Failed to start application with both windows.", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
