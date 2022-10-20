package com.ultreon.bubbles.util;

public final class TextUtils {
    private TextUtils() {
        throw new UnsupportedOperationException("Tried to initialize utility class");
    }

    public static String getRepresentationString(String s) {
        return s.replaceAll("\\\\", "\\\\")
                .replaceAll("\"", "\\\"")
                .replaceAll("\b", "\\b")
                .replaceAll("\f", "\\f")
                .replaceAll("\n", "\\n")
                .replaceAll("\r", "\\r")
                .replaceAll("\t", "\\t")
                .replaceAll("\u0000", "\\u0000")
                .replaceAll("\u007f", "\\u007f");
    }
}
