package com.ultreon.bubbles.entity.types;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Identifier;

public class EntityType<T extends Entity> {
    private final EntityFactory<T> entityFactory;

    public EntityType(EntityFactory<T> entityFactory) {
        this.entityFactory = entityFactory;
    }

    public T create(Environment environment) {
        return entityFactory.create(environment);
    }

    public T create(Environment environment, MapType document) {
        T t = entityFactory.create(environment);
        t.load(document);
        return t;
    }

    public Identifier getId() {
        return Registries.ENTITIES.getKey(this);
    }
}