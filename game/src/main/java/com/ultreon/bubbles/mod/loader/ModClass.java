package com.ultreon.bubbles.mod.loader;

import java.lang.reflect.InvocationTargetException;

@AntiMod
record ModClass(String modId, Class<?> modClass) {
    public Object init() throws NoSuchMethodException {
        try {
            return modClass.getConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
