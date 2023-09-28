package com.ultreon.commons.util;

import com.ultreon.bubbles.entity.Entity;

@SuppressWarnings("unused")
public class CollisionUtil {
    public static boolean isColliding(Entity entityA, Entity entityB) {
        return entityA.distanceTo(entityB) < entityA.size() / 2 + entityB.size() / 2;
    }
}
