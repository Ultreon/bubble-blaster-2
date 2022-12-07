package com.ultreon.bubbles.entity.player.ability;

import com.ultreon.bubbles.entity.Entity;

public abstract class AbilityTrigger {
    private final AbilityTriggerType type;
    private final Entity entity;

    @SuppressWarnings("SameParameterValue")
    protected AbilityTrigger(AbilityTriggerType type, Entity entity) {
        this.type = type;
        this.entity = entity;
    }

    public AbilityTriggerType getType() {
        return type;
    }

    public Entity getEntity() {
        return entity;
    }
}
