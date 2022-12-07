package com.ultreon.bubbles.util;

public class CompareUtils {
    public CompareUtils() {
        throw ExceptionUtils.utilityClass();
    }

    public static <T> boolean isGreater(Comparable<T> obj, T than) {
        return obj.compareTo(than) > 0;
    }

    public static <T> boolean isGreaterOrEqual(Comparable<T> obj, T than) {
        return obj.compareTo(than) >= 0;
    }

    public static <T> boolean isEqual(Comparable<T> obj, T than) {
        return obj.compareTo(than) == 0;
    }

    public static <T> boolean isLessEqual(Comparable<T> obj, T than) {
        return obj.compareTo(than) <= 0;
    }

    public static <T> boolean isLess(Comparable<T> obj, T than) {
        return obj.compareTo(than) < 0;
    }
}
