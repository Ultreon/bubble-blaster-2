package com.ultreon.bubbles.util;

public class Numbers {
    public static Integer tryParseInt(String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public static Long tryParseLong(String s) {
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public static Float tryParseFloat(String s) {
        Integer v = Numbers.tryParseInt(s);
        if (v != null) return v.floatValue();

        try {
            return Float.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    public static Double tryParseDouble(String s) {
        Long v = Numbers.tryParseLong(s);
        if (v != null) return v.doubleValue();
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
