package com.ultreon.bubbles.entity.damage;

import com.ultreon.bubbles.entity.Entity;

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
