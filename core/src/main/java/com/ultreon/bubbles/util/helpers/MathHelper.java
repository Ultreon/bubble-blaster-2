package com.ultreon.bubbles.util.helpers;

import com.ultreon.bubbles.render.Color;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Math helper, for all your math needs.
 */
public class MathHelper {

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

    public static byte diff(byte from, byte to) {
        return (byte) (Math.max(from, to) - Math.min (from, to));
    }

    public static short diff(short from, short to) {
        return (short) (Math.max(from, to) - Math.min (from, to));
    }

    public static int diff(int from, int to) {
        return Math.max(from, to) - Math.min (from, to);
    }

    public static long diff(long from, long to) {
        return Math.max(from, to) - Math.min (from, to);
    }

    public static float diff(float from, float to) {
        return Math.max(from, to) - Math.min (from, to);
    }

    public static double diff(double from, double to) {
        return Math.max(from, to) - Math.min (from, to);
    }

    public static String toReadableString(double value) {
        return MathHelper.toReadableString(value, 5);
    }

    public static String toReadableString(double value, int maxPlaces) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return Double.toString(value);
        }

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(maxPlaces, RoundingMode.HALF_UP);
        String $1 = bd.toPlainString().replaceAll("([.,][1-9]*)0+$", "$1");
        if ($1.endsWith(".") || $1.endsWith(",")) {
            return $1.substring(0, $1.length() - 1);
        }
        return $1;
    }

    public static String compress(double totalPriority) {
        if (totalPriority >= 0d && totalPriority < 1_000d) {
            return Double.toString(totalPriority);
        }
        if (totalPriority >= 1_000d && totalPriority < 1_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000d, 1) + "K";
        }
        if (totalPriority >= 1_000_000d && totalPriority < 1_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000d, 1) + "M";
        }
        if (totalPriority >= 1_000_000_000d && totalPriority < 1_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000d, 1) + "B";
        }
        if (totalPriority >= 1_000_000_000_000d && totalPriority < 1_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000d, 1) + "T";
        }
        if (totalPriority >= 1_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000d, 1) + "QD";
        }
        if (totalPriority >= 1_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000d, 1) + "QT";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000_000d, 1) + "S";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000_000_000d, 1) + "SX";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000_000_000_000d, 1) + "C";
        }
        return Double.toString(totalPriority);
    }
}
