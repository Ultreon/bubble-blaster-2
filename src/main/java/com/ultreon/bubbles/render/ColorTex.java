package com.ultreon.bubbles.render;

import java.awt.*;

/**
 * @author Qboi
 * @since 1.0.141
 */
public class ColorTex extends PaintTex {
    public ColorTex(Color color) {
        super(color);
    }

    public ColorTex(int r, int g, int b) {
        super(new Color(r, g, b));
    }

    public ColorTex(float r, float g, float b) {
        super(new Color(r, g, b));
    }

    public ColorTex(int r, int g, int b, int a) {
        super(new Color(r, g, b, a));
    }

    public ColorTex(float r, float g, float b, float a) {
        super(new Color(r, g, b, a));
    }

    public ColorTex(int rgb) {
        super(new Color(rgb));
    }

    public ColorTex(int rgba, boolean includeAlpha) {
        super(new Color(rgba, includeAlpha));
    }

    /**
     * Get a color texture based off a HSB value.
     *
     * @param h the hue
     * @param s the saturation
     * @param b the brightness.
     * @return the color texture created from the HSB value.
     */
    public static ColorTex fromHSB(float h, float s, float b) {
        return new ColorTex(Color.getHSBColor(h, s, b));
    }
}
