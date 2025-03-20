package io.kulila.gui.managers;

import io.kulila.gui.controllers.CameraControlController;
import io.kulila.gui.rendering.SceneRenderer;
import io.kulila.gui.controllers.ShapeSelectionController;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InteractionManager {

    private static final Logger logger = LoggerFactory.getLogger(InteractionManager.class);

    private final SceneRenderer sceneRenderer;
    private final Label statusLabel;
    private final ShapePlacementManager shapePlacementManager;

    public InteractionManager(SceneRenderer sceneRenderer, Label statusLabel) {
        this.sceneRenderer = sceneRenderer;
        this.statusLabel = statusLabel;
        this.shapePlacementManager = new ShapePlacementManager(
                sceneRenderer.getSceneObjectManager(),
                sceneRenderer.getRayPicker()
        );

        logger.info("InteractionManager initialized.");
    }

    public void openShapeSelectionDialog() {
        try {
            logger.info("Opening Shape Selection Dialog...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/ShapeSelectionView.fxml"));
            Parent root = loader.load();

            ShapeSelectionController controller = loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Select Shape");
            stage.showAndWait();

            String selectedShape = controller.getSelectedShape();
            if (selectedShape != null) {
                shapePlacementManager.setSelectedShape(selectedShape);
                statusLabel.setText("Selected Shape: " + selectedShape);
                logger.info("Selected Shape: {}", selectedShape);
            } else {
                logger.warn("No shape was selected.");
            }

        } catch (IOException e) {
            logger.error("Failed to load ShapeSelectionView.fxml", e);
        }
    }

    public void openCameraControlDialog() {
        try {
            logger.info("Opening Camera Control Dialog...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/fxml/CameraControlView.fxml"));
            Parent root = loader.load();

            CameraControlController controller = loader.getController();
            controller.setCameraController(sceneRenderer.getCameraController());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Camera Controls");
            stage.show();
            logger.info("Camera Control Dialog opened successfully.");

        } catch (IOException e) {
            logger.error("Failed to load CameraControlView.fxml", e);
        }
    }

    public void handleSceneClick(MouseEvent event) {
        logger.info("Mouse clicked at X: {}, Y: {}", event.getSceneX(), event.getSceneY());
        shapePlacementManager.handlePlacement(event);
    }
}
