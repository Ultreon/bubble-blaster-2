package dev.ultreon.bubbles.entity.damage;

import dev.ultreon.bubbles.entity.Entity;

public final class EntityDamageSource extends DamageSource {
    private final Entity entity;

    public EntityDamageSource(Entity entity, DamageType type) {
        super(type);
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
