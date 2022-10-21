package com.ultreon.bubbles.mod.loader;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

@AntiMod
public class ModList {
    private static ModList instance;
    private Map<String, ModObject> objects;

    @ApiStatus.Internal
    static void set(ModList modList) {
        instance = modList;
    }

    public ModList get() {
        return instance;
    }

    @ApiStatus.Internal
    ModList(Map<String, ModObject> objects) {
        this.objects = objects;
    }

    public ModObject getMod(String modId) {
        return objects.get(modId);
    }
}
