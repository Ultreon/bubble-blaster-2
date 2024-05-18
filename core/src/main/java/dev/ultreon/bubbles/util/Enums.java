package dev.ultreon.bubbles.util;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class Enums {
    private Enums() {
        throw new IllegalAccessError("Utility class");
    }

    public static <E extends Enum<E>> E byIndex(int value, E defaultValue, Function<E, Integer> getter) {
        for (var e : defaultValue.getDeclaringClass().getEnumConstants()) {
            if (getter.apply(e) == value) {
                return e;
            }
        }
        return defaultValue;
    }

    public static <E extends Enum<E>> E byName(String name, E defaultValue) {
        for (var e : defaultValue.getDeclaringClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return defaultValue;
    }

    public static <E extends Enum<E>> E byOrdinal(int ordinal, E defaultValue) {
        var enumConstants = defaultValue.getDeclaringClass().getEnumConstants();
        if (ordinal >= 0 && ordinal < enumConstants.length) {
            return enumConstants[ordinal];
        }
        return defaultValue;
    }

    public static <E extends Enum<E>> boolean validate(@Nullable Object obj, Class<E> enumClass) {
        if (obj != null) {
            for (var e : enumClass.getEnumConstants()) {
                if (e.name().equalsIgnoreCase(obj.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T extends Enum<T>> int size(Class<T> componentType) {
        return componentType.getEnumConstants().length;
    }
}
