package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.random.RandomSource;

import java.util.Objects;
import java.util.function.Supplier;

public record BubbleSpawnContext(RandomSource randomSource, int retry) {
    private static BubbleSpawnContext value = null;

    public static BubbleSpawnContext get() {
        return Objects.requireNonNull(value, "Spawn context not set.");
    }

    public static boolean exists() {
        return value != null;
    }

    public static void inContext(RandomSource randomSource, int retry, Runnable run) {
        value = new BubbleSpawnContext(randomSource, retry);
        run.run();
        value = null;
    }

    public static <T> T inContext(RandomSource randomSource, int retry, Supplier<T> run) {
        value = new BubbleSpawnContext(randomSource, retry);
        T t = run.get();
        value = null;
        return t;
    }
}
