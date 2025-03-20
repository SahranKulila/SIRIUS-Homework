package io.kulila.gui.rendering;

import javafx.geometry.Bounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class RayPicker {

    private static final Logger logger = LoggerFactory.getLogger(RayPicker.class);

    private final SubScene scene;
    private final Camera camera;
    private final Node groundPlane;

    public RayPicker(SubScene scene, Camera camera, Node groundPlane) {
        this.scene = scene;
        this.camera = camera;
        this.groundPlane = groundPlane;
        logger.info("RayPicker initialized.");
    }

    public Point3D pick(MouseEvent event) {
        logger.info("Mouse clicked at screen position X: {}, Y: {}", event.getSceneX(), event.getSceneY());

        // Compute pick direction in world space
        Point3D pickRay = unProjectDirection(event.getSceneX(), event.getSceneY());

        // Get camera world position
        Point3D cameraPosition = camera.localToScene(Point3D.ZERO);

        // Normalize ray direction after transformation
        pickRay = pickRay.normalize();

        // Get actual ground plane position
        Bounds groundBounds = groundPlane.localToScene(groundPlane.getBoundsInLocal());
        double groundY = groundBounds.getMinY();

        // Ensure ray is pointing downward (prevents objects appearing above)
        if (pickRay.getY() >= 0) {
            logger.warn("Pick ray is pointing up; ignoring.");
            return null;
        }

        // Compute actual intersection with ground plane
        double t = (groundY - cameraPosition.getY()) / pickRay.getY();
        if (t <= 0) {
            logger.warn("Ray does not intersect with the ground.");
            return null;
        }

        // Compute world-space intersection point
        Point3D pickedPoint = cameraPosition.add(pickRay.multiply(t));

        // Convert picked point to local scene coordinates
        Point3D finalPlacement = groundPlane.sceneToLocal(pickedPoint);

        logger.info("Final placement point: {}", finalPlacement);
        return finalPlacement;
    }

    private Point3D unProjectDirection(double sceneX, double sceneY) {
        // Convert screen coordinates to normalized device coordinates (-1 to 1)
        double normalizedX = (sceneX / scene.getWidth()) * 2 - 1;
        double normalizedY = 1 - (sceneY / scene.getHeight()) * 2;

        Affine affine = new Affine(camera.getLocalToSceneTransform());

        try {
            affine.invert();
        } catch (NonInvertibleTransformException e) {
            logger.error("Non-invertible transform encountered in unProjectDirection.", e);
            return Point3D.ZERO;
        }

        double tanHFov;
        if (camera instanceof PerspectiveCamera) {
            tanHFov = Math.tan(Math.toRadians(((PerspectiveCamera) camera).getFieldOfView() / 2));
        } else {
            logger.warn("Camera is not a PerspectiveCamera. Using default FOV.");
            tanHFov = Math.tan(Math.toRadians(60 / 2));
        }

        double aspectRatio = scene.getWidth() / scene.getHeight();

        // Convert screen coordinates to a world-space ray
        double dx = normalizedX * tanHFov * aspectRatio;
        double dy = normalizedY * tanHFov;

        // Apply camera transformations before normalizing
        Point3D direction = affine.deltaTransform(dx, dy, 1).normalize();

        logger.info("Computed pick direction: {}", direction);
        return direction;
    }
}
