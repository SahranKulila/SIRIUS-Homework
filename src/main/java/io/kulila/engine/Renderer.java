package io.kulila.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private static final Logger logger = LoggerFactory.getLogger(Renderer.class);

    public void init() {
        logger.info("Initializing renderer...");

        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        logger.info("Renderer initialized successfully.");
    }

    public void render(Scene scene) {
        //if(scene == null) return;

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        scene.render();

        int error = glGetError();
        if (error != GL_NO_ERROR) {
            logger.error("OpenGL error: {}", error);
        }
    }

    public void cleanup() {
        logger.info("Cleaning up renderer...");

        // No cleanup needed for now, this is for future use

        logger.info("Renderer cleaned up successfully.");
    }
}
