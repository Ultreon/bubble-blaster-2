package com.ultreon.bubbles.bubble;

import com.ultreon.bubbles.random.RandomSource;

import java.util.Objects;
import java.util.function.Supplier;

public final class BubbleSpawnContext {
    private static BubbleSpawnContext value = null;
    private final RandomSource randomSource;
    private final int retry;

    public BubbleSpawnContext(RandomSource randomSource, int retry) {
        this.randomSource = randomSource;
        this.retry = retry;
    }

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

    public RandomSource randomSource() {
        return randomSource;
    }

    public int retry() {
        return retry;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BubbleSpawnContext) obj;
        return Objects.equals(this.randomSource, that.randomSource) &&
                this.retry == that.retry;
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomSource, retry);
    }

    @Override
    public String toString() {
        return "BubbleSpawnContext[" +
                "randomSource=" + randomSource + ", " +
                "retry=" + retry + ']';
    }

}
