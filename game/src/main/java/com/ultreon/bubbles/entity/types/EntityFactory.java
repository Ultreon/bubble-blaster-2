package com.ultreon.bubbles.entity.types;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.environment.Environment;

public interface EntityFactory<T extends Entity> {
    T create(Environment environment);
}
