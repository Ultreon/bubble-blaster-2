package com.ultreon.bubbles.render;

/**
 * @author Qboi
 * @since 1.0.141
 */
public class ColorTex extends PaintTex {
    public ColorTex(Color color) {
        super(color.toAwt());
    }

    public ColorTex(int r, int g, int b) {
        this(Color.rgb(r, g, b));
    }

    public ColorTex(float r, float g, float b) {
        this(Color.rgb(r, g, b));
    }

    public ColorTex(int r, int g, int b, int a) {
        this(Color.rgba(r, g, b, a));
    }

    public ColorTex(float r, float g, float b, float a) {
        this(Color.rgba(r, g, b, a));
    }

    public ColorTex(int argb) {
        this(Color.argb(argb));
    }

    @Deprecated
    public ColorTex(int argb, boolean includeAlpha) {
        this(argb);
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
        return new ColorTex(Color.hsb(h, s, b));
    }
}
