package io.kulila.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class MainView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private double angleX = 0;
    private double angleY = 0;

    private static final double ROTATION_SPEED = 2.0;

    @Override
    public void start(Stage primaryStage) {
        Box cube = new Box(100, 100, 100);
        PhongMaterial material = new PhongMaterial(Color.BLUE);
        cube.setMaterial(material);

        Group wireframe = createWireframe(cube);

        Group root = new Group(cube, wireframe);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(1000.0);
        camera.setTranslateZ(-400);

        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(200);
        light.setTranslateY(-200);
        light.setTranslateZ(-300);

        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);
        scene.setCamera(camera);

        root.getChildren().add(light);

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.UP) {
                angleX -= ROTATION_SPEED;
            } else if (code == KeyCode.DOWN) {
                angleX += ROTATION_SPEED;
            } else if (code == KeyCode.LEFT) {
                angleY -= ROTATION_SPEED;
            } else if (code == KeyCode.RIGHT) {
                angleY += ROTATION_SPEED;
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                cube.getTransforms().clear();
                wireframe.getTransforms().clear();
                cube.getTransforms().addAll(
                        new Rotate(angleX, Rotate.X_AXIS),
                        new Rotate(angleY, Rotate.Y_AXIS)
                );
                wireframe.getTransforms().addAll(
                        new Rotate(angleX, Rotate.X_AXIS),
                        new Rotate(angleY, Rotate.Y_AXIS)
                );
            }
        };

        timer.start();

        primaryStage.setTitle("Rotating Cube with Visible Edges and Lighting");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Group createWireframe(Box cube) {
        double width = cube.getWidth();
        double height = cube.getHeight();
        double depth = cube.getDepth();

        Point3D[] vertices = {
                new Point3D(-width / 2, -height / 2, -depth / 2),
                new Point3D(width / 2, -height / 2, -depth / 2),
                new Point3D(width / 2, height / 2, -depth / 2),
                new Point3D(-width / 2, height / 2, -depth / 2),
                new Point3D(-width / 2, -height / 2, depth / 2),
                new Point3D(width / 2, -height / 2, depth / 2),
                new Point3D(width / 2, height / 2, depth / 2),
                new Point3D(-width / 2, height / 2, depth / 2)
        };

        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        Group wireframe = new Group();
        for (int[] edge : edges) {
            Point3D start = vertices[edge[0]];
            Point3D end = vertices[edge[1]];
            Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(2);
            wireframe.getChildren().add(line);
        }

        return wireframe;
    }
}
