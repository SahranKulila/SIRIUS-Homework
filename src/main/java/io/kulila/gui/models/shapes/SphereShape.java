package io.kulila.gui.models.shapes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;

public class SphereShape extends AbstractShape {

    private static final Logger logger = LoggerFactory.getLogger(SphereShape.class);

    public SphereShape(String id, double radius, Point3D initialPosition) {
        super(id, new Sphere(radius), initialPosition);
        setColor(Color.LIGHTGRAY);
        logger.info("SphereShape created: ID={}, Radius={}, Position={}", id, radius, initialPosition);
    }

    public void setRadius(double radius) {
        Sphere sphere = (Sphere) getShape();
        sphere.setRadius(radius);
        logger.info("SphereShape resized: ID={}, New Radius={}", getId(), radius);
    }

    public double getRadius() {
        return ((Sphere) getShape()).getRadius();
    }
}
