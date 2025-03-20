package io.kulila.gui.utils;

public class MathUtils {

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double lerp(double start, double end, double t) {
        return start + t * (end - start);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static double distance3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }
}
