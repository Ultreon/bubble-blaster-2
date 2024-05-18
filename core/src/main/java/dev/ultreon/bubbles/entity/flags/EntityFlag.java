package dev.ultreon.bubbles.entity.flags;

import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.libs.commons.v0.Identifier;
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
