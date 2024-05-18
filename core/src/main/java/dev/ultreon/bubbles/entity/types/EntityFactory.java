package dev.ultreon.bubbles.entity.types;

import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.world.World;

public interface EntityFactory<T extends Entity> {
    T create(World world);
}
