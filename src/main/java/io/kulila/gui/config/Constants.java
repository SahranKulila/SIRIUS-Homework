package io.kulila.gui.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

    private static final Logger logger = LoggerFactory.getLogger(Constants.class);

    public static final String APP_TITLE = "JavaFX 3D Renderer";
    public static final int APP_WIDTH = 1280;
    public static final int APP_HEIGHT = 720;

    public static final double GROUND_SIZE = 2000.0;

    public static final String ICON_PATH = "/icons/app-icon.png";
    public static final String CSS_THEME = "/css/dark-aero-theme.css";

    static {
        logger.info("Constants initialized: {}x{} | Ground size: {} | Icon: {} | CSS: {}",
                APP_WIDTH, APP_HEIGHT, GROUND_SIZE, ICON_PATH, CSS_THEME);
    }

    private Constants() {
    }
}
