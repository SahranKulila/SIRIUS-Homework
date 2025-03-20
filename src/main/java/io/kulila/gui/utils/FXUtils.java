package io.kulila.gui.utils;

import io.kulila.gui.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FXUtils {

    private static final Logger logger = LoggerFactory.getLogger(FXUtils.class);

    public static void applyDarkTheme(Scene scene) {
        try {
            String cssPath = FXUtils.class.getResource(Constants.CSS_THEME).toExternalForm();
            scene.getStylesheets().add(cssPath);
            logger.info("Applied dark theme: {}", Constants.CSS_THEME);
        } catch (Exception e) {
            logger.error("Failed to load CSS theme: {}", Constants.CSS_THEME, e);
        }
    }

    public static void setStageIcon(Stage stage, String iconPath) {
        try {
            Image icon = new Image(FXUtils.class.getResourceAsStream(iconPath));
            if (icon.isError()) {
                throw new RuntimeException("Failed to load icon: " + iconPath);
            }
            stage.getIcons().add(icon);
            logger.info("Set stage icon: {}", iconPath);
        } catch (Exception e) {
            logger.error("Failed to set stage icon: {}", iconPath, e);
        }
    }

    public static void setNodeVisibility(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
        logger.info("Set node visibility: {} -> {}", node, visible);
    }
}
