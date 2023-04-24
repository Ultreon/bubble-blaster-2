package com.ultreon.bubbles.mod.loader;

@AntiMod
public class ModLoadingContext {
    private static ModLoadingContext instance;
    private final ModObject modObject;

    public ModLoadingContext(ModObject modObject) {
        this.modObject = modObject;
    }

    public static ModLoadingContext get() {
        return instance;
    }

    static void set(ModObject modObject) {
        instance = new ModLoadingContext(modObject);
    }

    public ModObject getModObject() {
        return modObject;
    }
}
