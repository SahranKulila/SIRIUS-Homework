package io.kulila.gui.models.shapes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

public class BoxShape extends AbstractShape {

    private static final Logger logger = LoggerFactory.getLogger(BoxShape.class);

    public BoxShape(String id, double width, double height, double depth, Point3D initialPosition) {
        super(id, new Box(width, height, depth), initialPosition);
        setColor(Color.LIGHTGRAY);
        logger.info("BoxShape created: ID={}, Dimensions={}x{}x{}, Position={}", id, width, height, depth, initialPosition);
    }

    public void setSize(double width, double height, double depth) {
        Box box = (Box) getShape();
        box.setWidth(width);
        box.setHeight(height);
        box.setDepth(depth);
        logger.info("BoxShape resized: ID={}, New Dimensions={}x{}x{}", getId(), width, height, depth);
    }
}
