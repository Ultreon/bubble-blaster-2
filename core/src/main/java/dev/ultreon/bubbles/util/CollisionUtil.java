package dev.ultreon.bubbles.util;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import dev.ultreon.bubbles.entity.Entity;

public class CollisionUtil {
    public static boolean isColliding(Entity a, Entity b) {
        return a.distanceTo(b) < (a.radius() + b.radius());
    }
}
