package io.kulila.gui.controllers;

import io.kulila.gui.rendering.SceneRenderer;
import io.kulila.gui.managers.InteractionManager;
import javafx.scene.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML private BorderPane rootPane;
    @FXML private SubScene scene3D;
    @FXML private Label statusLabel;
    @FXML private Button btnShapeSelection;
    @FXML private Button btnCameraControl;

    private SceneRenderer sceneRenderer;
    private InteractionManager interactionManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing MainController...");

        if (scene3D.getRoot() == null) {
            logger.warn("SubScene root is null, setting default root Group.");
            scene3D.setRoot(new Group());
        }

        sceneRenderer = new SceneRenderer(scene3D);
        interactionManager = new InteractionManager(sceneRenderer, statusLabel);

        setupUIInteractions();
        statusLabel.setText("Scene Loaded");
        logger.info("Scene initialized successfully.");
    }

    private void setupUIInteractions() {
        btnShapeSelection.setOnAction(event -> {
            logger.info("Shape selection button clicked.");
            interactionManager.openShapeSelectionDialog();
            statusLabel.setText("Shape Selection Opened");
        });

        btnCameraControl.setOnAction(event -> {
            logger.info("Camera control button clicked.");
            interactionManager.openCameraControlDialog();
            statusLabel.setText("Camera Controls Opened");
        });

        scene3D.setOnMouseClicked(event -> {
            logger.info("Mouse clicked on scene at X: {}, Y: {}", event.getSceneX(), event.getSceneY());
            interactionManager.handleSceneClick(event);
        });
    }
}
