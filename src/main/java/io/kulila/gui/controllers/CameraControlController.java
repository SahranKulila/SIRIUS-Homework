package io.kulila.gui.controllers;

import io.kulila.gui.rendering.CameraController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;

public class CameraControlController {

    private static final Logger logger = LoggerFactory.getLogger(CameraControlController.class);

    @FXML private Slider sliderRotateX;
    @FXML private Slider sliderRotateY;
    @FXML private Slider sliderZoom;
    @FXML private Button btnResetCamera;

    private CameraController cameraController;

    public void setCameraController(CameraController cameraController) {
        this.cameraController = cameraController;
        logger.info("CameraController linked to CameraControlController.");
        bindControls();
    }

    @FXML
    private void initialize() {
        logger.info("Initializing CameraControlController...");
        setupResetButton();
    }

    private void bindControls() {
        if (cameraController == null) {
            logger.warn("CameraController is null, skipping control binding.");
            return;
        }

        sliderRotateX.valueProperty().addListener((obs, oldVal, newVal) -> {
            cameraController.setRotationX(newVal.doubleValue());
            logger.info("Slider Rotate X changed: {}", newVal);
        });

        sliderRotateY.valueProperty().addListener((obs, oldVal, newVal) -> {
            cameraController.setRotationY(newVal.doubleValue());
            logger.info("Slider Rotate Y changed: {}", newVal);
        });

        sliderZoom.valueProperty().addListener((obs, oldVal, newVal) -> {
            cameraController.setZoom(newVal.doubleValue());
            logger.info("Slider Zoom changed: {}", newVal);
        });
    }

    private void setupResetButton() {
        btnResetCamera.setOnAction(event -> {
            resetCamera();
            logger.info("Reset Camera button clicked.");
        });
    }

    private void resetCamera() {
        sliderRotateX.setValue(0);
        sliderRotateY.setValue(0);
        sliderZoom.setValue(-1000);
        logger.info("Camera reset sliders to default values.");

        if (cameraController != null) {
            cameraController.resetCamera();
            logger.info("CameraController reset to default position.");
        } else {
            logger.warn("CameraController is null, cannot reset camera.");
        }
    }
}
