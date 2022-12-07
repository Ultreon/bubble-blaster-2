package com.ultreon.bubbles.entity.types;

import com.ultreon.bubbles.common.Registrable;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.environment.Environment;
import net.querz.nbt.tag.CompoundTag;

import java.util.Objects;

public class EntityType<T extends Entity> extends Registrable {
    private final EntityFactory<T> entityFactory;

    public EntityType(EntityFactory<T> entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityType<?> that = (EntityType<?>) o;
        return Objects.equals(id(), that.id());
    }

    public T create(Environment environment) {
        return entityFactory.create(environment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }

    public T create(Environment environment, CompoundTag document) {
        T t = entityFactory.create(environment);
        t.load(document);
        return t;
    }

    @Override
    public String toString() {
        return "EntityType[" + id().toString() + "]";
    }
}
