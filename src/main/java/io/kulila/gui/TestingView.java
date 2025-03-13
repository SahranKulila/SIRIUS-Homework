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
    private static final double CUBE_DISTANCE = 300.0;

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Group root = new Group();
    private final Group cubes = new Group();

    private double cameraX = 0;
    private double cameraY = 0;
    private double cameraZ = -500;

    private double angleX = 0; // Pitch (up/down)
    private double angleY = 0; // Yaw (left/right)

    @Override
    public void start(Stage primaryStage) {
        logger.info("Initializing application...");
        addCube(0, 0, 0);

        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTBLUE);

        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        updateCamera();

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
                moveForward();
                break;
            case S:
                moveBackward();
                break;
            case A:
                moveLeft();
                break;
            case D:
                moveRight();
                break;
            case Q:
                cameraY += CAMERA_SPEED;
                break;
            case E:
                cameraY -= CAMERA_SPEED;
                break;
            case LEFT:
                angleY -= ROTATION_SPEED;
                break;
            case RIGHT:
                angleY += ROTATION_SPEED;
                break;
            case UP:
                angleX = Math.max(-90, angleX - ROTATION_SPEED); // Clamp pitch to [-90, 90]
                break;
            case DOWN:
                angleX = Math.min(90, angleX + ROTATION_SPEED); // Clamp pitch to [-90, 90]
                break;
        }
        updateCamera();
    }

    private void moveForward() {
        double radianY = Math.toRadians(angleY);
        double radianX = Math.toRadians(angleX);
        cameraZ += CAMERA_SPEED * Math.cos(radianY) * Math.cos(radianX);
        cameraX += CAMERA_SPEED * Math.sin(radianY) * Math.cos(radianX);
        cameraY += CAMERA_SPEED * Math.sin(radianX);
    }

    private void moveBackward() {
        double radianY = Math.toRadians(angleY);
        double radianX = Math.toRadians(angleX);
        cameraZ -= CAMERA_SPEED * Math.cos(radianY) * Math.cos(radianX);
        cameraX -= CAMERA_SPEED * Math.sin(radianY) * Math.cos(radianX);
        cameraY -= CAMERA_SPEED * Math.sin(radianX);
    }

    private void moveLeft() {
        double radianY = Math.toRadians(angleY);
        cameraX -= CAMERA_SPEED * Math.cos(radianY);
        cameraZ += CAMERA_SPEED * Math.sin(radianY);
    }

    private void moveRight() {
        double radianY = Math.toRadians(angleY);
        cameraX += CAMERA_SPEED * Math.cos(radianY);
        cameraZ -= CAMERA_SPEED * Math.sin(radianY);
    }

    private void handleMouseClick(Scene scene, javafx.scene.input.MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            Point3D point = getWorldPositionFromMouse(scene, event);
            addCube(point.getX(), point.getY(), point.getZ());
        }
    }

    private Point3D getWorldPositionFromMouse(Scene scene, javafx.scene.input.MouseEvent event) {
        double radianY = Math.toRadians(angleY);
        double radianX = Math.toRadians(angleX);

        double forwardX = Math.sin(radianY) * Math.cos(radianX);
        double forwardY = Math.sin(radianX);
        double forwardZ = Math.cos(radianY) * Math.cos(radianX);

        double x = cameraX + forwardX * CUBE_DISTANCE;
        double y = cameraY + forwardY * CUBE_DISTANCE;
        double z = cameraZ + forwardZ * CUBE_DISTANCE;

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
                new Rotate(-angleX, Rotate.X_AXIS),
                new Rotate(-angleY, Rotate.Y_AXIS)
        );
        logger.info("Updated camera position: cameraX={}, cameraY={}, cameraZ={}", cameraX, cameraY, cameraZ);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
