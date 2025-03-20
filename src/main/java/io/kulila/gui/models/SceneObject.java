package io.kulila.gui.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;

public class SceneObject {

    private static final Logger logger = LoggerFactory.getLogger(SceneObject.class);

    private final Node node;
    private final String id;

    public SceneObject(String id, Node node, Point3D initialPosition) {
        this.id = id;
        this.node = node;
        setPosition(initialPosition);
        logger.info("SceneObject created: ID={}, Position={}", id, initialPosition);
    }

    public void setPosition(Point3D position) {
        node.setTranslateX(position.getX());
        node.setTranslateY(position.getY());
        node.setTranslateZ(position.getZ());
        logger.info("SceneObject moved: ID={}, New Position={}", id, position);
    }

    public Point3D getPosition() {
        return new Point3D(node.getTranslateX(), node.getTranslateY(), node.getTranslateZ());
    }

    public Node getNode() {
        return node;
    }

    public String getId() {
        return id;
    }

    public void addToGroup(Group group) {
        group.getChildren().add(node);
        logger.info("SceneObject added to group: ID={}", id);
    }

    public void removeFromGroup(Group group) {
        group.getChildren().remove(node);
        logger.info("SceneObject removed from group: ID={}", id);
    }
}
