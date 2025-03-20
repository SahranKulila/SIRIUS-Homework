package io.kulila.gui.rendering;

import io.kulila.gui.config.Constants;
import io.kulila.gui.managers.SceneObjectManager;
import io.kulila.gui.rendering.RayPicker;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneRenderer {

    private static final Logger logger = LoggerFactory.getLogger(SceneRenderer.class);

    private final SubScene scene3D;
    private final Group root3D;
    private final PerspectiveCamera camera;
    private final SceneObjectManager sceneObjectManager;
    private final RayPicker rayPicker;
    private final CameraController cameraController;

    public SceneRenderer(SubScene subScene) {
        logger.info("Initializing SceneRenderer...");
        this.scene3D = subScene;
        this.root3D = new Group();
        this.sceneObjectManager = new SceneObjectManager(root3D);
        this.cameraController = new CameraController(subScene);
        this.camera = cameraController.getCamera();
        this.rayPicker = new RayPicker(subScene, camera, createGroundPlane());

        setupScene();
        setupSubScene(subScene);
        subScene.setRoot(root3D);
        logger.info("SceneRenderer initialized successfully.");
    }

    private void setupScene() {
        AmbientLight ambientLight = new AmbientLight(Color.WHITE.deriveColor(0, 1, 0.5, 1));
        root3D.getChildren().addAll(createGroundPlane(), ambientLight);
    }

    private void setupCamera() {
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.getTransforms().addAll(
                new Rotate(-45, Rotate.X_AXIS),
                new Rotate(-45, Rotate.Y_AXIS),
                new Translate(0, -500, -500)
        );
    }

    private void setupSubScene(SubScene subScene) {
        subScene.setFill(Color.web("#404040"));
        subScene.setCamera(camera);
    }

    private Node createGroundPlane() {
        Box groundPlane = new Box(Constants.GROUND_SIZE, 1, Constants.GROUND_SIZE);
        groundPlane.setMaterial(new PhongMaterial(Color.DARKGRAY));
        groundPlane.setTranslateY(0);
        groundPlane.setDrawMode(DrawMode.FILL);
        return groundPlane;
    }

    public CameraController getCameraController() {
        return cameraController;
    }

    public SceneObjectManager getSceneObjectManager() {
        return sceneObjectManager;
    }

    public RayPicker getRayPicker() {
        return rayPicker;
    }

    public SubScene getSubScene() {
        return scene3D;
    }
}
