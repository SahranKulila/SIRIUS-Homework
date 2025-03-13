package io.kulila.gui;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingView extends Application {

    private static final Logger logger = LoggerFactory.getLogger(TestingView.class);

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final double CAMERA_SPEED = 5.0;
    private static final double ROTATION_SPEED = 2.0;

    private PerspectiveCamera camera;
    private final Translate cameraPosition = new Translate(0, 0, -50);
    private final Rotate cameraRotationX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate cameraRotationY = new Rotate(0, Rotate.Y_AXIS);

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT, true);
        scene.setFill(Color.LIGHTGRAY);

        camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(cameraPosition, cameraRotationY, cameraRotationX);
        scene.setCamera(camera);

        Box defaultCube = createCube();
        defaultCube.setTranslateX(0);
        defaultCube.setTranslateY(0);
        defaultCube.setTranslateZ(0);
        root.getChildren().add(defaultCube);
        logger.info("Default cube added to the scene.");

        scene.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double mouseX = event.getX();
                double mouseY = event.getY();

                PickResult pickResult = event.getPickResult();
                if (pickResult != null && pickResult.getIntersectedNode() != null) {
                    Box cube = createCube();
                    cube.setTranslateX(pickResult.getIntersectedPoint().getX());
                    cube.setTranslateY(pickResult.getIntersectedPoint().getY());
                    cube.setTranslateZ(pickResult.getIntersectedPoint().getZ());
                    root.getChildren().add(cube);
                    logger.info("Cube created at: X={}, Y={}, Z={}",
                            pickResult.getIntersectedPoint().getX(),
                            pickResult.getIntersectedPoint().getY(),
                            pickResult.getIntersectedPoint().getZ());
                }
            }
        });

        scene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            switch (keyCode) {
                case W: // Move forward
                    cameraPosition.setZ(cameraPosition.getZ() + CAMERA_SPEED);
                    logger.info("Camera moved forward. New Z position: {}", cameraPosition.getZ());
                    break;
                case S: // Move backward
                    cameraPosition.setZ(cameraPosition.getZ() - CAMERA_SPEED);
                    logger.info("Camera moved backward. New Z position: {}", cameraPosition.getZ());
                    break;
                case A: // Move left
                    cameraPosition.setX(cameraPosition.getX() - CAMERA_SPEED);
                    logger.info("Camera moved left. New X position: {}", cameraPosition.getX());
                    break;
                case D: // Move right
                    cameraPosition.setX(cameraPosition.getX() + CAMERA_SPEED);
                    logger.info("Camera moved right. New X position: {}", cameraPosition.getX());
                    break;
                case UP: // Rotate up
                    cameraRotationX.setAngle(cameraRotationX.getAngle() - ROTATION_SPEED);
                    logger.info("Camera rotated up. New X rotation: {}", cameraRotationX.getAngle());
                    break;
                case DOWN: // Rotate down
                    cameraRotationX.setAngle(cameraRotationX.getAngle() + ROTATION_SPEED);
                    logger.info("Camera rotated down. New X rotation: {}", cameraRotationX.getAngle());
                    break;
                case LEFT: // Rotate left
                    cameraRotationY.setAngle(cameraRotationY.getAngle() - ROTATION_SPEED);
                    logger.info("Camera rotated left. New Y rotation: {}", cameraRotationY.getAngle());
                    break;
                case RIGHT: // Rotate right
                    cameraRotationY.setAngle(cameraRotationY.getAngle() + ROTATION_SPEED);
                    logger.info("Camera rotated right. New Y rotation: {}", cameraRotationY.getAngle());
                    break;
            }
        });

        primaryStage.setTitle("3D Camera and Cube Creation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Box createCube() {
        Box cube = new Box(10, 10, 10);
        cube.setMaterial(new javafx.scene.paint.PhongMaterial(Color.BLUE));
        return cube;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
