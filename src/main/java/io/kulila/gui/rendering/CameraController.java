package io.kulila.gui.rendering;

import javafx.scene.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class CameraController {

    private static final Logger logger = LoggerFactory.getLogger(CameraController.class);

    private final PerspectiveCamera camera;
    private final Group cameraGroup;  // Rotation group
    private final Group rootGroup;
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX;
    private final Rotate rotateY;
    private final Translate translate;

    public CameraController(SubScene scene) {
        logger.info("Initializing CameraController...");

        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);

        cameraGroup = new Group();
        rotateX = new Rotate(0, Rotate.X_AXIS);
        rotateY = new Rotate(0, Rotate.Y_AXIS);
        translate = new Translate(-100, -300, -1000);

        cameraGroup.getTransforms().addAll(rotateY, rotateX, translate);

        rootGroup = new Group();
        rootGroup.getChildren().add(cameraGroup);

        cameraGroup.getChildren().add(camera);

        scene.setCamera(camera);
        ((Group) scene.getRoot()).getChildren().add(rootGroup);
        initMouseControl(scene);
    }

    private void initMouseControl(SubScene scene) {
        scene.setOnMousePressed(this::handleMousePressed);
        scene.setOnMouseDragged(this::handleMouseDragged);
        scene.setOnScroll(event -> {
            double delta = event.getDeltaY();
            setZoom(translate.getZ() + delta);
        });
    }

    private void handleMousePressed(MouseEvent event) {
        anchorX = event.getSceneX();
        anchorY = event.getSceneY();
        anchorAngleX = rotateX.getAngle();
        anchorAngleY = rotateY.getAngle();
    }

    private void handleMouseDragged(MouseEvent event) {
        setRotationY(anchorAngleY + (event.getSceneX() - anchorX) * 0.2);
        setRotationX(anchorAngleX - (event.getSceneY() - anchorY) * 0.2);
    }

    public void setRotationX(double angle) {
        angle = clamp(angle, -90, 90);
        rotateX.setAngle(angle);
        logger.info("Camera rotated on X-axis: {}", angle);
    }

    public void setRotationY(double angle) {
        angle = normalizeAngle(angle);
        rotateY.setAngle(angle);
        logger.info("Camera rotated on Y-axis: {}", angle);
    }

    public void setZoom(double zoom) {
        //zoom = clamp(zoom, -3000, -100);
        translate.setZ(zoom);
        logger.info("Camera zoom set to: {}", zoom);
    }

    public void resetCamera() {
        setRotationX(0);
        setRotationY(0);
        setZoom(-1000);
        logger.info("Camera reset to default position.");
    }

    private double normalizeAngle(double angle) {
        angle = angle % 360;
        if (angle > 180) {
            angle -= 360;
        } else if (angle < -180) {
            angle += 360;
        }
        return angle;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }
}
