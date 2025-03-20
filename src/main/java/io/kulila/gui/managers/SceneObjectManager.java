package io.kulila.gui.managers;

import io.kulila.gui.models.SceneObject;
import io.kulila.gui.rendering.SceneBoundary;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SceneObjectManager {

    private static final Logger logger = LoggerFactory.getLogger(SceneObjectManager.class);

    private final Group sceneRoot;
    private final SceneBoundary boundary;
    private final Map<String, SceneObject> objects;

    public SceneObjectManager(Group sceneRoot) {
        this.sceneRoot = sceneRoot;
        this.boundary = new SceneBoundary();
        this.objects = new HashMap<>();
        logger.info("SceneObjectManager initialized.");
    }

    public void addObject(SceneObject object) {
        Point3D position = object.getPosition();
        if (!boundary.isWithinBoundary(position)) { // Now correctly checks boundaries for Point3D
            Point3D clampedPosition = boundary.clampPosition(position);
            object.setPosition(clampedPosition);
            logger.warn("Object {} was out of bounds, clamped to {}", object.getId(), clampedPosition);
        }
        objects.put(object.getId(), object);
        object.addToGroup(sceneRoot);
        logger.info("Added object {} at position {}", object.getId(), object.getPosition());
    }

    public void removeObject(String id) {
        SceneObject object = objects.remove(id);
        if (object != null) {
            object.removeFromGroup(sceneRoot);
            logger.info("Removed object {}", id);
        } else {
            logger.warn("Attempted to remove nonexistent object {}", id);
        }
    }

    public SceneObject getObject(String id) {
        return objects.get(id);
    }

    public void moveObject(String id, Point3D newPosition) {
        SceneObject object = objects.get(id);
        if (object != null) {
            Point3D clampedPosition = boundary.clampPosition(newPosition);
            object.setPosition(clampedPosition);
            logger.info("Moved object {} to new position {}", id, clampedPosition);
        } else {
            logger.warn("Attempted to move nonexistent object {}", id);
        }
    }

    public boolean objectExists(String id) {
        boolean exists = objects.containsKey(id);
        logger.debug("Object {} exists: {}", id, exists);
        return exists;
    }
}
