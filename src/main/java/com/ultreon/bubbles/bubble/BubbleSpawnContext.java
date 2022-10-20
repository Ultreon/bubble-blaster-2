package com.ultreon.bubbles.bubble;

import java.util.Objects;
import java.util.function.Supplier;

public record BubbleSpawnContext(long spawnIndex, int retry) {
    private static BubbleSpawnContext value = null;

    public static BubbleSpawnContext get() {
        return Objects.requireNonNull(value, () -> "Spawn context not set.");
    }

    public static void inContext(long spawnIndex, int retry, Runnable run) {
        value = new BubbleSpawnContext(spawnIndex, retry);
        run.run();
        value = null;
    }

    public static <T> T inContext(long spawnIndex, int retry, Supplier<T> run) {
        value = new BubbleSpawnContext(spawnIndex, retry);
        T t = run.get();
        value = null;
        return t;
    }
}
