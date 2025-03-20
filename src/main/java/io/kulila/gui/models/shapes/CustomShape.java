package io.kulila.gui.models.shapes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class CustomShape extends AbstractShape {

    private static final Logger logger = LoggerFactory.getLogger(CustomShape.class);

    public CustomShape(String id, float[] points, int[] faces, Point3D initialPosition) {
        super(id, createMeshView(points, faces), initialPosition);
        setColor(Color.LIGHTGRAY);
        logger.info("CustomShape created: ID={}, Position={}", id, initialPosition);
    }

    private static MeshView createMeshView(float[] points, int[] faces) {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getFaces().setAll(faces);
        return new MeshView(mesh);
    }

    public void setMesh(float[] points, int[] faces) {
        MeshView meshView = (MeshView) getShape();
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getFaces().setAll(faces);
        meshView.setMesh(mesh);
        logger.info("CustomShape updated: ID={}, New Mesh Applied", getId());
    }
}
