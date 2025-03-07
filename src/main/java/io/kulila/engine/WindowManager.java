package io.kulila.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class WindowManager {
    private static final Logger logger = LoggerFactory.getLogger(WindowManager.class);

    private long windowHandle;
    private int width;
    private int height;
    private String title;

    public void createWindow(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;

        logger.info("Creating GLFW window...");

        // Set up an error callback to print GLFW errors
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // Hide the window initially
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // Allow window resizing
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3); // OpenGL 3.3
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        windowHandle = GLFW.glfwCreateWindow(width, height, title, 0, 0);
        if (windowHandle == 0) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        // Center the window on the screen
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFW.glfwSetWindowPos(windowHandle, (GLFW.glfwGetVideoMode(monitor).width() - width) / 2,
                (GLFW.glfwGetVideoMode(monitor).height() - height) / 2);

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        // Enable v-sync
        GLFW.glfwSwapInterval(1);

        // Show the window
        GLFW.glfwShowWindow(windowHandle);

        logger.info("GLFW window created successfully.");
    }

    public void pollEvents() {
        GLFW.glfwPollEvents();
    }

    // Swaps the front and back buffers to display the rendered frame.
    public void swapBuffers() {
        GLFW.glfwSwapBuffers(windowHandle);
    }

    // True if the window should close, false otherwise.
    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(windowHandle);
    }

    public void cleanup() {

        logger.info("Cleaning up GLFW window...");

        GLFW.glfwDestroyWindow(windowHandle);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();

        logger.info("GLFW window cleaned up successfully.");
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
