package com.ultreon.bubbles.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
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

    public static Vec3d toCoreLibs(Vector3 vector) {
        return new Vec3d(vector.x, vector.y, vector.z);
    }

    public static void hideCursor() {

        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
    }

    public static void showCursor() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }
}
