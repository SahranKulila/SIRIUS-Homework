package io.kulila.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.shape.CullFace;
import javafx.scene.input.PickResult;

public class TestingView extends Application {

    private PerspectiveCamera camera;
    private final double MOVE_SPEED = 2;
    private final double ROTATE_SPEED = 0.2;
    private double mouseX, mouseY;
    private final Group root = new Group();
    private boolean canPlaceCube = false;

    private double yaw = 0;
    private double pitch = 0;

    @Override
    public void start(Stage primaryStage) {
        // Create a large plane (ground)
        MeshView ground = createGround();
        root.getChildren().add(ground);

        // Create and configure camera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        camera.setTranslateZ(-50);

        // Scene setup
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTBLUE);
        scene.setCamera(camera);

        // Event handlers
        scene.setOnKeyPressed(this::handleKeyPress);
        scene.setOnKeyReleased(this::handleKeyRelease);
        scene.setOnMousePressed(this::handleMousePress);
        scene.setOnMouseDragged(this::handleMouseDrag);

        primaryStage.setTitle("JavaFX 3D Scene");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MeshView createGround() {
        float size = 500;
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(-size, 0, -size, size, 0, -size, size, 0, size, -size, 0, size);
        mesh.getTexCoords().addAll(0, 0);
        mesh.getFaces().addAll(0, 0, 1, 0, 2, 0, 0, 0, 2, 0, 3, 0);

        MeshView ground = new MeshView(mesh);
        ground.setMaterial(new PhongMaterial(Color.DARKGREEN));
        ground.setCullFace(CullFace.BACK);
        return ground;
    }

    private void handleKeyPress(KeyEvent event) {
        double change = MOVE_SPEED;
        if (event.isShiftDown()) change *= 2;

        Point3D forward = getForwardDirection();
        Point3D right = getRightDirection();

        switch (event.getCode()) {
            case W -> moveCamera(forward.multiply(change));
            case S -> moveCamera(forward.multiply(-change));
            case A -> moveCamera(right.multiply(-change));
            case D -> moveCamera(right.multiply(change));
            case Q -> moveCamera(new Point3D(0, -change, 0));
            case E -> moveCamera(new Point3D(0, change, 0));
            case SPACE -> canPlaceCube = true;
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            canPlaceCube = false;
        }
    }

    private void moveCamera(Point3D direction) {
        camera.setTranslateX(camera.getTranslateX() + direction.getX());
        camera.setTranslateY(camera.getTranslateY() + direction.getY());
        camera.setTranslateZ(camera.getTranslateZ() + direction.getZ());
    }

    private Point3D getForwardDirection() {
        return new Point3D(
                -Math.sin(Math.toRadians(yaw)),
                0,
                Math.cos(Math.toRadians(yaw))
        ).normalize();
    }

    private Point3D getRightDirection() {
        return new Point3D(
                Math.cos(Math.toRadians(yaw)),
                0,
                Math.sin(Math.toRadians(yaw))
        ).normalize();
    }

    private void handleMousePress(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && canPlaceCube) {
            PickResult pickResult = event.getPickResult();
            if (pickResult != null && pickResult.getIntersectedNode() != null) {
                addCube(pickResult.getIntersectedPoint().getX(), pickResult.getIntersectedPoint().getZ());
            }
        }
    }

    private void handleMouseDrag(MouseEvent event) {
        double deltaX = event.getSceneX() - mouseX;
        double deltaY = event.getSceneY() - mouseY;

        yaw -= deltaX * ROTATE_SPEED;
        pitch -= deltaY * ROTATE_SPEED;

        pitch = Math.max(-90, Math.min(90, pitch));

        camera.getTransforms().clear();
        camera.getTransforms().add(new Rotate(yaw, Rotate.Y_AXIS));
        camera.getTransforms().add(new Rotate(pitch, Rotate.X_AXIS));
        camera.getTransforms().add(new Translate(camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ()));

        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }

    private void addCube(double x, double z) {
        Box cube = new Box(10, 10, 10);
        cube.setMaterial(new PhongMaterial(Color.RED));

        cube.setTranslateX(x);
        cube.setTranslateY(5);
        cube.setTranslateZ(z);

        root.getChildren().add(cube);
    }

    public static void main(String[]args) {
        launch(args);
    }
}
