package io.kulila.gui.models.shapes;

import io.kulila.gui.models.SceneObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point3D;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;

public abstract class AbstractShape extends SceneObject {

    private static final Logger logger = LoggerFactory.getLogger(AbstractShape.class);

    protected PhongMaterial material;

    public AbstractShape(String id, Shape3D shape, Point3D initialPosition) {
        super(id, shape, initialPosition);
        this.material = new PhongMaterial();
        shape.setMaterial(material);
        logger.info("AbstractShape created: ID={} at {}", id, initialPosition);
    }

    public void setColor(javafx.scene.paint.Color color) {
        material.setDiffuseColor(color);
        logger.info("Color set for shape {}: {}", getId(), color);
    }

    public javafx.scene.paint.Color getColor() {
        return material.getDiffuseColor();
    }

    public Shape3D getShape() {
        return (Shape3D) getNode();
    }
}
