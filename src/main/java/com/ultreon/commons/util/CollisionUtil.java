package com.ultreon.commons.util;

import com.ultreon.bubbles.entity.Entity;

@SuppressWarnings("unused")
public class CollisionUtil {
    public static boolean isColliding(Entity entityA, Entity entityB) {
//        Area areaA = new Area(shapeA);
//        areaA.intersect(new Area(shapeB));
        return entityA.distanceTo(entityB) < (entityA.size() + entityB.size()) / 2;
    }
}
