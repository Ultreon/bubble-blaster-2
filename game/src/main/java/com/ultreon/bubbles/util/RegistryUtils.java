package com.ultreon.bubbles.util;

import com.ultreon.bubbles.registry.object.ObjectHolder;
import org.jetbrains.annotations.Nullable;

public class RegistryUtils {
    @Nullable
    public static ObjectHolder getObjectHolder(Class<?> clazz) {
        return clazz.getDeclaredAnnotation(ObjectHolder.class);
    }
}
