package com.ultreon.commons.util;

import com.ultreon.bubbles.entity.Entity;

@SuppressWarnings("unused")
public class CollisionUtil {
    public static boolean isColliding(Entity a, Entity b) {
        return a.distanceTo(b) < a.getRadius() + b.getRadius();
    }
}
