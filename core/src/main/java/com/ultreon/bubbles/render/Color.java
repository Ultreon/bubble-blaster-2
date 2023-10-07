package com.ultreon.bubbles.render;

import com.ultreon.libs.commons.v0.exceptions.InvalidValueException;
import org.jetbrains.annotations.ApiStatus;

import java.util.regex.Pattern;

public class Color {
    public static final Color BLACK = Color.grayscale(0x00);
    public static final Color GRAY_0 = Color.BLACK;
    public static final Color GRAY_1 = Color.grayscale(0x10);
    public static final Color GRAY_2 = Color.grayscale(0x20);
    public static final Color GRAY_3 = Color.grayscale(0x30);
    public static final Color DARK_GRAY = Color.grayscale(0x40);
    public static final Color GRAY_4 = Color.DARK_GRAY;
    public static final Color GRAY_5 = Color.grayscale(0x50);
    public static final Color GRAY_6 = Color.grayscale(0x60);
    public static final Color GRAY_7 = Color.grayscale(0x70);
    public static final Color GRAY = Color.grayscale(0x80);
    public static final Color GRAY_8 = Color.GRAY;
    public static final Color GRAY_9 = Color.grayscale(0x90);
    public static final Color GRAY_A = Color.grayscale(0xa0);
    public static final Color GRAY_B = Color.grayscale(0xb0);
    public static final Color LIGHT_GRAY = Color.grayscale(0xc0);
    public static final Color GRAY_C = Color.LIGHT_GRAY;
    public static final Color GRAY_D = Color.grayscale(0xd0);
    public static final Color GRAY_E = Color.grayscale(0xe0);
    public static final Color GRAY_F = Color.grayscale(0xf0);
    public static final Color WHITE = Color.grayscale(0xff);
    public static final Color RED = Color.rgb(0xff0000);
    public static final Color ORANGE = Color.rgb(0xff8000);
    public static final Color GOLD = Color.rgb(0xffb000);
    public static final Color YELLOW = Color.rgb(0xffff00);
    public static final Color YELLOW_GREEN = Color.rgb(0x80ff00);
    public static final Color GREEN = Color.rgb(0x00ff00);
    public static final Color MINT = Color.rgb(0x00ff80);
    public static final Color CYAN = Color.rgb(0x00ffff);
    public static final Color AZURE = Color.rgb(0x0080ff);
    public static final Color BLUE = Color.rgb(0x0000ff);
    public static final Color PURPLE = Color.rgb(0x8000ff);
    public static final Color MAGENTA = Color.rgb(0xff00ff);
    public static final Color ROSE = Color.rgb(0xff0080);
    public static final Color TRANSPARENT = Color.rgba(0x00000000);
    public static final Color CRIMSON = Color.rgb(0xdc143c);
    private final java.awt.Color awtColor;

    private Color(long red, long green, long blue, long alpha) {
        this.awtColor = new java.awt.Color((int) red, (int) green, (int) blue, (int) alpha);
    }

    private Color(int red, int green, int blue, int alpha) {
        this.awtColor = new java.awt.Color(red, green, blue, alpha);
    }

    private Color(java.awt.Color color) {
        this.awtColor = color;
    }

    public static Color hsb(float h, float s, float b) {
        return new Color(java.awt.Color.getHSBColor(h, s, b));
    }

    public static Color rgb(int red, int green, int blue) {
        return new Color(red, green, blue, 255);
    }

    public static Color rgb(float red, float green, float blue) {
        return new Color((int) (red * 255), (int) (green * 255), (int) (blue * 255), 255);
    }

    public static Color rgba(int red, int green, int blue, int alpha) {
        return new Color(red, green, blue, alpha);
    }

    public static Color rgba(float red, float green, float blue, float alpha) {
        return new Color((int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
    }

    public static Color rgb(int color) {
        var rgb = (long) color % 0x100000000L;
        return new Color((rgb & 0xff0000L) >> 16, (rgb & 0x00ff00L) >> 8, rgb & 0x0000ffL, 255);
    }

    public static Color rgba(int color) {
        var rgba = (long) color % 0x100000000L;
        return new Color((rgba & 0xff000000L) >> 24, (rgba & 0x00ff0000L) >> 16, (rgba & 0x0000ff00L) >> 8, rgba & 0x000000ffL);
    }

    public static Color argb(int color) {
        var argb = (long) color % 0x100000000L;
        return new Color((argb & 0x00ff0000L) >> 16, (argb & 0x0000ff00L) >> 8, argb & 0x000000ffL, (argb & 0xff000000L) >> 24);
    }

    public static Color bgr(int color) {
        var bgr = (long) color % 0x100000000L;
        return new Color(bgr & 0x0000ffL, (bgr & 0x00ff00L) >> 8, (bgr & 0xff0000L) >> 16, 255);
    }

    public static Color bgra(int color) {
        var bgra = (long) color % 0x100000000L;
        return new Color((bgra & 0x0000ff00L) >> 8, (bgra & 0x00ff0000L) >> 16, (bgra & 0xff000000L) >> 24, bgra & 0x000000ffL);
    }

    public static Color abgr(int color) {
        var abgr = (long) color % 0x100000000L;
        return new Color(abgr & 0x000000ffL, (abgr & 0x0000ff00L) >> 8, (abgr & 0x00ff0000L) >> 16, (abgr & 0xff000000L) >> 24);
    }

    public static Color grayscale(int brightness) {
        return new Color(brightness, brightness, brightness, 0xff);
    }

    public static Color grayscale(int brightness, int alpha) {
        return new Color(brightness, brightness, brightness, alpha);
    }

    public static Color hex(String hex) {
        if (Pattern.matches("#[0-9a-fA-F]{6}", hex)) {
            int rgb = Integer.valueOf(hex.substring(1), 16);
            return Color.rgb(rgb);
        } else if (Pattern.matches("#[0-9a-fA-F]{8}", hex)) {
            int rgb = Integer.valueOf(hex.substring(1), 16);
            return Color.rgba(rgb);
        } else if (Pattern.matches("#[0-9a-fA-F]{3}", hex)) {
            int rgb = Integer.valueOf(new String(new char[]{
                    hex.charAt(1), hex.charAt(1),
                    hex.charAt(2), hex.charAt(2),
                    hex.charAt(3), hex.charAt(3)}), 16);
            return Color.rgb(rgb);
        } else if (Pattern.matches("#[0-9a-fA-F]{4}", hex)) {
            int rgb = Integer.valueOf(new String(new char[]{
                    hex.charAt(1), hex.charAt(1),
                    hex.charAt(2), hex.charAt(2),
                    hex.charAt(3), hex.charAt(3),
                    hex.charAt(4), hex.charAt(4)}), 16);
            return Color.rgba(rgb);
        } else {
            if (!hex.isEmpty()) {
                if (hex.charAt(0) != '#') {
                    throw new InvalidValueException("First character create color code isn't '#'.");
                } else if (hex.length() != 3 && hex.length() != 4 && hex.length() != 6 && hex.length() != 8) {
                    throw new InvalidValueException("Invalid hex length, should be 3, 4, 6 or 8 in length.");
                } else {
                    throw new InvalidValueException("Invalid hex value. Hex values may only contain numbers and letters a to f.");
                }
            } else {
                throw new InvalidValueException("The color hex is empty, it should start with a hex, and then 3, 4, 6 or 8 hexadecimal digits.");
            }
        }
    }

    @ApiStatus.Internal
    public static Color awt(java.awt.Color awt) {
        return new Color(awt);
    }

    public static Color gdx(com.badlogic.gdx.graphics.Color color) {
        return new Color((int) (color.r * 255), (int) (color.g * 255), (int) (color.b * 255), (int) (color.a * 255));
    }

    public java.awt.Color toAwt() {
        return this.awtColor;
    }

    public Color brighter() {
        return new Color(this.awtColor.brighter());
    }

    public Color darker() {
        return new Color(this.awtColor.darker());
    }

    public int getRed() {
        return this.awtColor.getRed();
    }

    public int getGreen() {
        return this.awtColor.getGreen();
    }

    public int getBlue() {
        return this.awtColor.getBlue();
    }

    public int getAlpha() {
        return this.awtColor.getAlpha();
    }

    public int getTransparency() {
        return this.awtColor.getTransparency();
    }

    public int getRgb() {
        return this.awtColor.getRGB();
    }

    public Color withRed(int red) {
        return new Color(red, this.getGreen(), this.getBlue(), this.getAlpha());
    }

    public Color withGreen(int green) {
        return new Color(this.getRed(), green, this.getBlue(), this.getAlpha());
    }

    public Color withBlue(int blue) {
        return new Color(this.getRed(), this.getGreen(), blue, this.getAlpha());
    }

    public Color withAlpha(int alpha) {
        return new Color(this.getRed(), this.getGreen(), this.getBlue(), alpha);
    }

    @Override
    public String toString() {
        return String.format("#%02x%02x%02x%02x", this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
    }

    public com.badlogic.gdx.graphics.Color toGdx() {
        return new com.badlogic.gdx.graphics.Color(this.getRed() / 255f, this.getGreen() / 255f, this.getBlue() / 255f, this.getAlpha() / 255f);
    }
}
