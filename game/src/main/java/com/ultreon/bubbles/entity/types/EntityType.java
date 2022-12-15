package com.ultreon.bubbles.entity.types;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.environment.Environment;
import net.querz.nbt.tag.CompoundTag;

import java.util.Objects;

public class EntityType<T extends Entity> {
    private final EntityFactory<T> entityFactory;

    public EntityType(EntityFactory<T> entityFactory) {
        this.entityFactory = entityFactory;
    }

    public T create(Environment environment) {
        return entityFactory.create(environment);
    }

    public T create(Environment environment, CompoundTag document) {
        T t = entityFactory.create(environment);
        t.load(document);
        return t;
    }
}
