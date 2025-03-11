package dev.ultreon.bubbles.util.helpers;

import com.badlogic.gdx.graphics.Color;
import dev.ultreon.bubbles.render.Colors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Math helper, for all your math needs.
 */
public class MathHelper {
    @Deprecated(forRemoval = true)
    public static byte clamp(byte value, int min, int max) {
        if (value < min) return (byte) min;
        else return (byte) Math.min(value, max);
    }

    @Deprecated(forRemoval = true)
    public static short clamp(short value, int min, int max) {
        if (value < min) return (short) min;
        else return (short) Math.min(value, max);
    }

    @Deprecated(forRemoval = true)
    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        else return Math.min(value, max);
    }

    @Deprecated(forRemoval = true)
    public static long clamp(long value, long min, long max) {
        if (value < min) return min;
        else return Math.min(value, max);
    }

    @Deprecated(forRemoval = true)
    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        else return Math.min(value, max);
    }

    @Deprecated(forRemoval = true)
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        else return Math.min(value, max);
    }

    @Deprecated(forRemoval = true)
    public static BigInteger clamp(BigInteger value, BigInteger min, BigInteger max) {
        return value.max(min).min(max);
    }

    @Deprecated(forRemoval = true)
    public static BigDecimal clamp(BigDecimal value, BigDecimal min, BigDecimal max) {
        return value.max(min).min(max);
    }

    @Deprecated(forRemoval = true)
    public static double root(int value, int root) {
        return Math.pow(value, 1.0d / root);
    }

    @Deprecated(forRemoval = true)
    public static double round(double value, int places) {
        if (((Double) value).isNaN() || ((Float) (float) value).isNaN()) {
            return value;
        }
        if (((Double) value).isInfinite() || ((Float) (float) value).isInfinite()) {
            return value;
        }

        if (places < 0) throw new IllegalArgumentException();

        var bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Deprecated(forRemoval = true)
    public static double lerp(double min, double max, double percentage) {
        return min + percentage * (max - min);
    }

    public static Color mixColors(Color color1, Color color2, double percent) {
        var inverse_percent = 1.0 - percent;
        var redPart = (float) (color1.r * percent + color2.r * inverse_percent);
        var greenPart = (float) (color1.g * percent + color2.g * inverse_percent);
        var bluePart = (float) (color1.b * percent + color2.b * inverse_percent);
        var alphaPart = (float) (color1.a * percent + color2.a * inverse_percent);
        return Colors.rgba(redPart, greenPart, bluePart, alphaPart);
    }

    @Deprecated(forRemoval = true)
    public static byte diff(byte from, byte to) {
        return (byte) (Math.max(from, to) - Math.min (from, to));
    }

    @Deprecated(forRemoval = true)
    public static short diff(short from, short to) {
        return (short) (Math.max(from, to) - Math.min (from, to));
    }

    @Deprecated(forRemoval = true)
    public static int diff(int from, int to) {
        return Math.max(from, to) - Math.min (from, to);
    }

    @Deprecated(forRemoval = true)
    public static long diff(long from, long to) {
        return Math.max(from, to) - Math.min (from, to);
    }

    @Deprecated(forRemoval = true)
    public static float diff(float from, float to) {
        return Math.max(from, to) - Math.min (from, to);
    }

    @Deprecated(forRemoval = true)
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

        var bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(maxPlaces, RoundingMode.HALF_UP);
        var $1 = bd.toPlainString().replaceAll("([.,][1-9]*)0+$", "$1");
        if ($1.endsWith(".") || $1.endsWith(",")) {
            return $1.substring(0, $1.length() - 1);
        }
        return $1;
    }

    public static String compress(double totalPriority) {
        if (Double.isNaN(totalPriority) || Double.isInfinite(totalPriority)) {
            return Double.toString(totalPriority);
        }
        if (totalPriority < 0d) {
            return "-" + MathHelper.compress(-totalPriority);
        }
        if (totalPriority >= 0d && totalPriority < 1_000d) {
            return Double.toString(totalPriority);
        }
        if (totalPriority >= 1_000d && totalPriority < 1_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000d, 1) + "k";
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
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000d, 1) + "Qa";
        }
        if (totalPriority >= 1_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000d, 1) + "Qi";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000_000d, 1) + "Sx";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000_000_000d, 1) + "Sp";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000_000_000_000d, 1) + "Oc";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000_000_000_000_000d, 1) + "No";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000_000_000_000_000d && totalPriority < 1_000_000_000_000_000_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000_000_000_000_000_000d, 1) + "Dc";
        }
        if (totalPriority >= 1_000_000_000_000_000_000_000_000_000_000_000_000d) {
            return MathHelper.toReadableString(totalPriority / 1_000_000_000_000_000_000_000_000_000_000_000_000d, 1) + "Ud";
        }
        return Double.toString(totalPriority);
    }
}
