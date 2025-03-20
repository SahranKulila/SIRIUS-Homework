package io.kulila.gui.rendering;

import io.kulila.gui.config.Constants;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneBoundary {

    private static final Logger logger = LoggerFactory.getLogger(SceneBoundary.class);

    private final double boundarySize;

    public SceneBoundary() {
        this.boundarySize = Constants.GROUND_SIZE / 2;
    }

    public boolean isWithinBoundary(Point3D position) {
        boolean withinBounds = position.getX() >= -boundarySize && position.getX() <= boundarySize &&
                position.getZ() >= -boundarySize && position.getZ() <= boundarySize &&
                position.getY() >= 0;
        logger.debug("Checking boundary for Point3D {}: {}", position, withinBounds);
        return withinBounds;
    }

    public boolean isWithinBoundary(Node node) {
        Bounds bounds = node.localToScene(node.getBoundsInLocal());
        boolean withinBounds = bounds.getMinX() >= -boundarySize && bounds.getMaxX() <= boundarySize &&
                bounds.getMinZ() >= -boundarySize && bounds.getMaxZ() <= boundarySize &&
                bounds.getMinY() >= 0;
        logger.debug("Checking boundary for Node {}: {}", node, withinBounds);
        return withinBounds;
    }

    public Point3D clampPosition(Point3D position) {
        double x = clamp(position.getX(), -boundarySize, boundarySize);
        double y = Math.max(position.getY(), 0);
        double z = clamp(position.getZ(), -boundarySize, boundarySize);

        Point3D clampedPosition = new Point3D(x, y, z);
        logger.info("Clamping position {} to {}", position, clampedPosition);
        return clampedPosition;
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}
