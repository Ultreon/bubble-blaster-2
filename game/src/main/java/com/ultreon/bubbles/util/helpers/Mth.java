package com.ultreon.bubbles.util.helpers;

import com.ultreon.bubbles.render.Color;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Math helper, for all your math needs.
 */
public class Mth {

    public static byte clamp(byte value, int min, int max) {
        if (value < min) return (byte) min;
        else return (byte) Math.min(value, max);
    }

    public static short clamp(short value, int min, int max) {
        if (value < min) return (short) min;
        else return (short) Math.min(value, max);
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        else return Math.min(value, max);
    }

    public static long clamp(long value, long min, long max) {
        if (value < min) return min;
        else return Math.min(value, max);
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        else return Math.min(value, max);
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        else return Math.min(value, max);
    }

    public static BigInteger clamp(BigInteger value, BigInteger min, BigInteger max) {
        return value.max(min).min(max);
    }

    public static BigDecimal clamp(BigDecimal value, BigDecimal min, BigDecimal max) {
        return value.max(min).min(max);
    }

    public static double root(int value, int root) {
        return Math.pow(value, 1.0d / root);
    }

    public static double round(double value, int places) {
        if (((Double) value).isNaN() || ((Float) (float) value).isNaN()) {
            return value;
        }
        if (((Double) value).isInfinite() || ((Float) (float) value).isInfinite()) {
            return value;
        }

        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double lerp(double min, double max, double percentage) {
        return min + percentage * (max - min);
    }

    public static Color mixColors(Color color1, Color color2, double percent) {
        double inverse_percent = 1.0 - percent;
        int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        int alphaPart = (int) (color1.getAlpha() * percent + color2.getAlpha() * inverse_percent);
        return Color.rgba(redPart, greenPart, bluePart, alphaPart);
    }
}
