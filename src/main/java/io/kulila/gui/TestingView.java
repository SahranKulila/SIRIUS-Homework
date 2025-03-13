package io.kulila.gui;

import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.PickResult;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingView extends Application {

    private static final Logger logger = LoggerFactory.getLogger(TestingView.class);
    private static final double CAMERA_SPEED = 5.0;
    private static final double ROTATION_SPEED = 2.0;

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Group root = new Group();
    private final Group cubes = new Group();

    private double cameraX = 0;
    private double cameraY = 0;
    private double cameraZ = -500;

    private double angleX = 0;
    private double angleY = 0;

    @Override
    public void start(Stage primaryStage) {
        logger.info("Initializing application...");
        addCube(0, 0, 0);

        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTBLUE);

        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.getTransforms().addAll(
                new Translate(cameraX, cameraY, cameraZ),
                new Rotate(angleX, Rotate.X_AXIS),
                new Rotate(angleY, Rotate.Y_AXIS)
        );

        scene.setCamera(camera);

        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        scene.setOnMouseClicked(event -> handleMouseClick(scene, event));

        root.getChildren().add(cubes);

        primaryStage.setTitle("3D Camera and Cube Interaction");
        primaryStage.setScene(scene);
        primaryStage.show();
        logger.info("Application started successfully.");
    }

    private void handleKeyPress(KeyCode code) {
        switch (code) {
            case W:
                cameraZ += CAMERA_SPEED * Math.cos(Math.toRadians(angleY));
                cameraX += CAMERA_SPEED * Math.sin(Math.toRadians(angleY));
                logger.info("Moving forward: cameraX={}, cameraY={}, cameraZ={}", cameraX, cameraY, cameraZ);
                break;
            case S:
                cameraZ -= CAMERA_SPEED * Math.cos(Math.toRadians(angleY));
                cameraX -= CAMERA_SPEED * Math.sin(Math.toRadians(angleY));
                logger.info("Moving backward: cameraX={}, cameraY={}, cameraZ={}", cameraX, cameraY, cameraZ);
                break;
            case A:
                cameraX -= CAMERA_SPEED * Math.cos(Math.toRadians(angleY));
                cameraZ += CAMERA_SPEED * Math.sin(Math.toRadians(angleY));
                logger.info("Moving left: cameraX={}, cameraY={}, cameraZ={}", cameraX, cameraY, cameraZ);
                break;
            case D:
                cameraX += CAMERA_SPEED * Math.cos(Math.toRadians(angleY));
                cameraZ -= CAMERA_SPEED * Math.sin(Math.toRadians(angleY));
                logger.info("Moving right: cameraX={}, cameraY={}, cameraZ={}", cameraX, cameraY, cameraZ);
                break;
            case Q:
                cameraY += CAMERA_SPEED;
                logger.info("Moving up: cameraX={}, cameraY={}, cameraZ={}", cameraX, cameraY, cameraZ);
                break;
            case E:
                cameraY -= CAMERA_SPEED;
                logger.info("Moving down: cameraX={}, cameraY={}, cameraZ={}", cameraX, cameraY, cameraZ);
                break;
            case LEFT:
                angleY -= ROTATION_SPEED;
                logger.info("Rotating left: angleX={}, angleY={}", angleX, angleY);
                break;
            case RIGHT:
                angleY += ROTATION_SPEED;
                logger.info("Rotating right: angleX={}, angleY={}", angleX, angleY);
                break;
            case UP:
                angleX += ROTATION_SPEED;
                logger.info("Rotating up: angleX={}, angleY={}", angleX, angleY);
                break;
            case DOWN:
                angleX -= ROTATION_SPEED;
                logger.info("Rotating down: angleX={}, angleY={}", angleX, angleY);
                break;
        }

        updateCamera();
    }

    private void handleMouseClick(Scene scene, javafx.scene.input.MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            Point3D point = getMousePositionIn3D(scene, event);
            logger.info("Mouse clicked at 3D position: x={}, y={}, z={}", point.getX(), point.getY(), point.getZ());
            addCube(point.getX(), point.getY(), point.getZ());
        }
    }

    private Point3D getMousePositionIn3D(Scene scene, javafx.scene.input.MouseEvent event) {
        double x = event.getX() - scene.getWidth() / 2;
        double y = event.getY() - scene.getHeight() / 2;
        double z = cameraZ;
        return new Point3D(x, y, z);
    }

    private void addCube(double x, double y, double z) {
        Box cube = new Box(50, 50, 50);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.color(Math.random(), Math.random(), Math.random()));
        cube.setMaterial(material);
        cube.setTranslateX(x);
        cube.setTranslateY(y);
        cube.setTranslateZ(z);
        cubes.getChildren().add(cube);
        logger.info("Added cube at position: x={}, y={}, z={}", x, y, z);
    }

    private void updateCamera() {
        camera.getTransforms().clear();
        camera.getTransforms().addAll(
                new Translate(cameraX, cameraY, cameraZ),
                new Rotate(angleX, Rotate.X_AXIS),
                new Rotate(angleY, Rotate.Y_AXIS)
        );
        logger.info("Updated camera position: cameraX={}, cameraY={}, cameraZ={}", cameraX, cameraY, cameraZ);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
