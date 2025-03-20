package io.kulila.gui.managers;

import io.kulila.gui.models.SceneObject;
import io.kulila.gui.models.shapes.*;
import io.kulila.gui.rendering.RayPicker;
import io.kulila.gui.rendering.SceneBoundary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ShapePlacementManager {

    private static final Logger logger = LoggerFactory.getLogger(ShapePlacementManager.class);

    private final SceneObjectManager sceneObjectManager;
    private final RayPicker rayPicker;
    private final SceneBoundary sceneBoundary;
    private String selectedShape = null;

    public ShapePlacementManager(SceneObjectManager sceneObjectManager, RayPicker rayPicker) {
        this.sceneObjectManager = sceneObjectManager;
        this.rayPicker = rayPicker;
        this.sceneBoundary = new SceneBoundary();
        logger.info("ShapePlacementManager initialized.");
    }

    public void setSelectedShape(String shape) {
        this.selectedShape = shape;
        logger.info("Selected shape set to: {}", shape);
    }

    public void handlePlacement(MouseEvent event) {
        Point3D placementPoint = rayPicker.pick(event);

        if (placementPoint == null) {
            logger.warn("Invalid placement: Ray did not hit the ground.");
            return;
        }

        placementPoint = sceneBoundary.clampPosition(placementPoint);
        logger.info("Placing {} at {}", selectedShape, placementPoint);

        SceneObject shapeObject = createShape(selectedShape, placementPoint);
        if (shapeObject != null) {
            sceneObjectManager.addObject(shapeObject);
            logger.info("{} placed successfully with ID: {}", selectedShape, shapeObject.getId());
        } else {
            logger.error("Failed to create shape: {}", selectedShape);
        }
    }

    private SceneObject createShape(String shapeType, Point3D position) {
        switch (shapeType) {
            case "Box":
                return new BoxShape("Box_" + System.currentTimeMillis(), 50, 50, 50, position);
            case "Sphere":
                return new SphereShape("Sphere_" + System.currentTimeMillis(), 30, position);
            case "Cylinder":
                return new CylinderShape("Cylinder_" + System.currentTimeMillis(), 20, 60, position);
            case "Custom":
                return createCustomShape(position);
            default:
                logger.warn("Unknown shape type requested: {}", shapeType);
                return null;
        }
    }

    private SceneObject createCustomShape(Point3D position) {
        float[] points = {
                -25,  25, 0,
                25,  25, 0,
                -25, -25, 0,
                25, -25, 0
        };

        int[] faces = {
                0, 0, 1, 0, 2, 0,
                1, 0, 3, 0, 2, 0
        };

        CustomShape customShape = new CustomShape("Custom_" + System.currentTimeMillis(), points, faces, position);
        customShape.setColor(Color.BLUE);
        logger.info("Custom shape created with ID: {}", customShape.getId());
        return customShape;
    }
}
