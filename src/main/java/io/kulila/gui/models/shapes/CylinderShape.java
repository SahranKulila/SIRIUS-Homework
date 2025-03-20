package io.kulila.gui.models.shapes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;

public class CylinderShape extends AbstractShape {

    private static final Logger logger = LoggerFactory.getLogger(CylinderShape.class);

    public CylinderShape(String id, double radius, double height, Point3D initialPosition) {
        super(id, new Cylinder(radius, height), initialPosition);
        setColor(Color.LIGHTGRAY);
        logger.info("CylinderShape created: ID={}, Radius={}, Height={}, Position={}", id, radius, height, initialPosition);
    }

    public void setSize(double radius, double height) {
        Cylinder cylinder = (Cylinder) getShape();
        cylinder.setRadius(radius);
        cylinder.setHeight(height);
        logger.info("CylinderShape resized: ID={}, New Radius={}, New Height={}", getId(), radius, height);
    }

    public double getRadius() {
        return ((Cylinder) getShape()).getRadius();
    }

    public double getHeight() {
        return ((Cylinder) getShape()).getHeight();
    }
}
