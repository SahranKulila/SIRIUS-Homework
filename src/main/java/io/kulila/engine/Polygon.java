package io.kulila.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class Polygon {
    private float[] vertices;
    private Vector3f position;
    private float rotationAngle;

    private int vaoId;
    private int vboId;

    public Polygon(float[] vertices, Vector3f position) {
        this.vertices = vertices;
        this.position = position;
        this.rotationAngle = 0.0f;
        initBuffers();
    }

    private void initBuffers() {
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

        FloatBuffer vertexBuffer = org.lwjgl.BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void rotate(float angle) {
        this.rotationAngle += angle;
    }

    public void render() {
        Matrix4f transform = new Matrix4f()
                .translate(position)
                .rotate((float) Math.toRadians(rotationAngle), new Vector3f(0, 0, 1));

        GL30.glBindVertexArray(vaoId);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, vertices.length / 2);

        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        GL30.glDeleteVertexArrays(vaoId);
        GL15.glDeleteBuffers(vboId);
    }

}
