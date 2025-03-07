package io.kulila.engine;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Engine {
    private static final Logger logger = LoggerFactory.getLogger(Engine.class);

    private WindowManager windowManager;
    private Renderer renderer;
    private SceneManager sceneManager;
//    private ImGuiLayer imGuiLayer;

    private boolean isRunning = false;

    public void init() {
        logger.info("Initializing engine...");

        windowManager = new WindowManager();
        windowManager.createWindow(1280, 720, "3D Graphics Engine");

        renderer = new Renderer();
        renderer.init();

        sceneManager = new SceneManager();
        sceneManager.init();

        // Add the MainScene to the SceneManager
        MainScene mainScene = new MainScene();
        sceneManager.addScene("MainScene", mainScene);
        sceneManager.setCurrentScene("MainScene");

//        imGuiLayer = new ImGuiLayer();
//        imGuiLayer.startFrame();
//        imGuiLayer.init(windowManager.getWindowHandle());

        isRunning = true;
        logger.info("Engine initialized successfully.");
    }

    public void run() {
        logger.info("Starting engine main loop...");

//        windowManager.pollEvents();
//        logger.info("Events polled.");
//
//        sceneManager.update();
//        logger.info("SceneManager updated.");
//
//        renderer.render(sceneManager.getCurrentScene());
//        logger.info("Scene rendered.");
//
//        imGuiLayer.render();
//        logger.info("ImGuiLayer rendered.");
//
//        windowManager.swapBuffers();
//        logger.info("Buffer Swapped.");
//
//        if (windowManager.shouldClose()) {
//            logger.info("window should close...");
//            stop();
//        }

        while (isRunning) {
            windowManager.pollEvents();

            /// ///
            if (GLFW.glfwGetKey(windowManager.getWindowHandle(), GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS) {
                ((MainScene) sceneManager.getCurrentScene()).toggleRotation();
            }

            sceneManager.update();

            renderer.render(sceneManager.getCurrentScene());

//            imGuiLayer.render();

            windowManager.swapBuffers();

            if (windowManager.shouldClose()) {
                stop();
            }
        }

        logger.info("Engine main loop stopped.");
    }

    public void stop() {
        logger.info("Stopping engine...");

//        imGuiLayer.cleanup();

        renderer.cleanup();

        windowManager.cleanup();

        isRunning = false;
        logger.info("Engine stopped successfully.");
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.init();
        engine.run();
    }
}
