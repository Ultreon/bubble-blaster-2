package com.ultreon.bubbles.entity.types;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.world.World;

public interface EntityFactory<T extends Entity> {
    T create(World world);
}
