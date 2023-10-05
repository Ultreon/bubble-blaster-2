package com.ultreon.bubbles.util;

import com.badlogic.gdx.math.Vector3;
import com.ultreon.libs.commons.v0.vector.Vec3d;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Utils {
    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T make(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }

    @Deprecated(forRemoval = true)
    public static Vec3d toCoreLibs(Vector3 vector) {
        return new Vec3d(vector.x, vector.y, vector.z);
    }
}
