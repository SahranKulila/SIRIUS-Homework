package io.kulila.engine;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainScene extends Scene {
    private static final Logger logger = LoggerFactory.getLogger(MainScene.class);

    private Polygon polygon;
    private boolean rotatePolygon = true;

    @Override
    public void init() {
        logger.info("Initializing MainScene...");

        float[] vertices = {
                -0.5f, -0.5f, // Bottom-left
                0.5f, -0.5f, // Bottom-right
                0.5f,  0.5f, // Top-right
                -0.5f,  0.5f  // Top-left
        };

        polygon = new Polygon(vertices, new Vector3f(0, 0, 0));
    }

    @Override
    public void update() {
        // Update scene logic

        if (rotatePolygon) {
            polygon.rotate(1.0f); // 1 degree per frame
        }
    }

    @Override
    public void render() {
        // Render scene objects

        polygon.render();
    }

    @Override
    public void cleanup() {
        logger.info("Cleaning up MainScene...");
        if (polygon != null) {
            polygon.cleanup();
        }
    }

    public void toggleRotation() {
        rotatePolygon = !rotatePolygon;
    }
}
