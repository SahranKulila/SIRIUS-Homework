package io.kulila.gui.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ShapeSelectionController {

    private static final Logger logger = LoggerFactory.getLogger(ShapeSelectionController.class);

    @FXML private Button btnBox;
    @FXML private Button btnSphere;
    @FXML private Button btnCylinder;
    @FXML private Button btnCustom;
    @FXML private Button btnConfirm;
    @FXML private Button btnCancel;

    private String selectedShape = null;

    @FXML
    private void initialize() {
        logger.info("Initializing ShapeSelectionController...");
        setupShapeSelection();
        setupControlButtons();
    }

    private void setupShapeSelection() {
        btnBox.setOnAction(event -> {
            selectShape("Box");
            logger.info("Box shape selected.");
        });
        btnSphere.setOnAction(event -> {
            selectShape("Sphere");
            logger.info("Sphere shape selected.");
        });
        btnCylinder.setOnAction(event -> {
            selectShape("Cylinder");
            logger.info("Cylinder shape selected.");
        });
        btnCustom.setOnAction(event -> {
            selectShape("Custom");
            logger.info("Custom shape selected.");
        });
    }

    private void setupControlButtons() {
        btnConfirm.setOnAction(event -> {
            confirmSelection();
            logger.info("Confirm button clicked.");
        });
        btnCancel.setOnAction(event -> {
            closeWindow();
            logger.info("Cancel button clicked.");
        });
    }

    private void selectShape(String shape) {
        selectedShape = shape;
    }

    private void confirmSelection() {
        if (selectedShape != null) {
            logger.info("Shape confirmed: {}", selectedShape);
        } else {
            logger.warn("No shape was selected before confirmation.");
        }
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
        logger.info("Shape selection window closed.");
    }

    public String getSelectedShape() {
        return selectedShape;
    }
}
