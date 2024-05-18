package dev.ultreon.bubbles.entity.player.ability;

import dev.ultreon.bubbles.entity.Entity;

public abstract class AbilityTrigger {
    private final AbilityTriggerType type;
    private final Entity entity;

    @SuppressWarnings("SameParameterValue")
    protected AbilityTrigger(AbilityTriggerType type, Entity entity) {
        this.type = type;
        this.entity = entity;
    }

    public AbilityTriggerType getType() {
        return this.type;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
