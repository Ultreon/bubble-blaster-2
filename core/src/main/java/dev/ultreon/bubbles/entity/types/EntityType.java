package dev.ultreon.bubbles.entity.types;

import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.ubo.types.MapType;
import dev.ultreon.libs.commons.v0.Identifier;

public class EntityType<T extends Entity> {
    private final EntityFactory<T> entityFactory;

    public EntityType(EntityFactory<T> entityFactory) {
        this.entityFactory = entityFactory;
    }

    public T create(World world) {
        return this.entityFactory.create(world);
    }

    public T create(World world, MapType document) {
        var t = this.entityFactory.create(world);
        t.load(document);
        return t;
    }

    public Identifier getKey() {
        return Registries.ENTITIES.getKey(this);
    }
}
