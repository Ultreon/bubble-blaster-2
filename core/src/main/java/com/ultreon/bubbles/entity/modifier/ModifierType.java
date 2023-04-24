package com.ultreon.bubbles.entity.modifier;

import java.util.HashMap;

public record ModifierType(String name) {
    static final HashMap<String, ModifierType> types = new HashMap<>();

    public ModifierType {
        if (types.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate modifier detected!");
        }

        ModifierType.types.put(name, this);
    }
}
