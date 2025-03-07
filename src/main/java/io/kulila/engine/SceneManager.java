package io.kulila.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static final Logger logger = LoggerFactory.getLogger(SceneManager.class);

    private Map<String, Scene> scenes;
    private Scene currentScene;

    public void init() {
        logger.info("Initializing scene manager...");

        scenes = new HashMap<>();
        currentScene = null;

        logger.info("Scene manager initialized successfully.");
    }

    public void addScene(String name, Scene scene) {
        if (scenes.containsKey(name)) {
            logger.warn("Scene with name '{}' already exists. Overwriting...", name);
        }

        scenes.put(name, scene);
        logger.info("Added scene: {}", name);

        if (currentScene == null) {
            setCurrentScene(name);
        }
    }

    public void setCurrentScene(String name) {
        if (!scenes.containsKey(name)) {
            logger.error("Scene with name '{}' does not exist.", name);
            throw new IllegalArgumentException("Scene not found: " + name);
        }

        if (currentScene != null) {
            currentScene.cleanup();
        }

        currentScene = scenes.get(name);
        currentScene.init();
        logger.info("Set current scene: {}", name);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void update() {
        if (currentScene != null) {
            currentScene.update();
        }
    }

    public void cleanup() {
        logger.info("Cleaning up scene manager...");

        for (Scene scene : scenes.values()) {
            scene.cleanup();
        }
        scenes.clear();
        currentScene = null;

        logger.info("Scene manager cleaned up successfully.");
    }
}
