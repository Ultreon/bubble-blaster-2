package dev.ultreon.bubbles.util;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import dev.ultreon.bubbles.entity.Entity;

public class CollisionUtil {
    public static boolean isColliding(Entity a, Entity b) {
        var circleA = new Circle(a.getX(), a.getY(), a.radius());
        var circleB = new Circle(b.getX(), b.getY(), b.radius());
        return Intersector.overlaps(circleA, circleB);
    }
}
