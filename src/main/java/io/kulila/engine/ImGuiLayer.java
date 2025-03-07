package io.kulila.engine;

import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImGuiLayer {
//    private static final Logger logger = LoggerFactory.getLogger(ImGuiLayer.class);
//
//    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
//    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
//
//    private long windowHandle;
//
//    public void init(long windowHandle) {
//        logger.info("Initializing Dear ImGui...");
//
//        this.windowHandle = windowHandle;
//
//        ImGui.createContext();
//
//        ImGuiIO io = ImGui.getIO();
//        io.setIniFilename(null); // Disable saving ImGui settings to disk
//        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
//        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
//
//        // Initialize platform and renderer bindings
//        imGuiGlfw.init(windowHandle, true);
//        imGuiGl3.init();
//
//        logger.info("Dear ImGui initialized successfully.");
//    }
//
//    public void startFrame() {
//        imGuiGlfw.newFrame();
//        ImGui.newFrame();
//    }
//
//    public void render() {
//        //renderDebugWindow();
//
//        ImGui.render();
//        imGuiGl3.renderDrawData(ImGui.getDrawData());
//    }
//
//    public void cleanup() {
//        logger.info("Cleaning up Dear ImGui...");
//
//        imGuiGl3.shutdown();
//        imGuiGlfw.shutdown();
//        ImGui.destroyContext();
//
//        logger.info("Dear ImGui cleaned up successfully.");
//    }
//
//
//    private void renderDebugWindow() {
//        ImGui.begin("Debug Window");
//
//        // Display FPS
//        ImGui.text("FPS: " + ImGui.getIO().getFramerate());
//
//        // Display memory usage
//        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//        ImGui.text("Memory: " + usedMemory / 1024 / 1024 + " MB");
//
//        ImGui.end();
//    }
}
