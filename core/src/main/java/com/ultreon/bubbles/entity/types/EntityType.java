package com.ultreon.bubbles.entity.types;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.world.World;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Identifier;

public class EntityType<T extends Entity> {
    private final EntityFactory<T> entityFactory;

    public EntityType(EntityFactory<T> entityFactory) {
        this.entityFactory = entityFactory;
    }

    public T create(World world) {
        return this.entityFactory.create(world);
    }

    public T create(World world, MapType document) {
        T t = this.entityFactory.create(world);
        t.load(document);
        return t;
    }

    public Identifier getKey() {
        return Registries.ENTITIES.getKey(this);
    }
}
