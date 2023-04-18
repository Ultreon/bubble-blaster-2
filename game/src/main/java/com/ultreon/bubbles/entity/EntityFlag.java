package com.ultreon.bubbles.entity;

import com.ultreon.bubbles.registry.Registries;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.Nullable;

public class EntityFlag {
    public void onEnable() {

    }

    public void onDisable() {

    }

    public @Nullable Identifier getId() {
        return Registries.ENTITY_FLAGS.getKey(this);
    }
}
