package io.kulila.engine;

public abstract class Scene {
    public abstract void init();

    public abstract void update();

    public abstract void render();

    public abstract void cleanup();
}
