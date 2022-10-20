package com.ultreon.bubbles.event.v1.entity;

import com.ultreon.bubbles.entity.Entity;

@Deprecated
public class EntityTickEvent extends EntityEvent {
    public EntityTickEvent(Entity entity) {
        super(entity);
    }
}
