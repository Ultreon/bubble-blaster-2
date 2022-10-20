package com.ultreon.bubbles.event.v1.entity;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.event.v1.Event;

@Deprecated
public abstract class EntityEvent extends Event {
    private final Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
