package dev.ultreon.bubbles.entity.spawning;

public class NaturalSpawnReason extends SpawnReason {
    private final SpawnUsage usage;
    private final int retry;

    public NaturalSpawnReason(SpawnUsage usage, int retry) {
        this.usage = usage;
        this.retry = retry;
    }

    public SpawnUsage getUsage() {
        return this.usage;
    }

    @Override
    public String toString() {
        return "LoadSpawnReason{" +
                "random=" + this.usage +
                '}';
    }

    public int getRetry() {
        return this.retry;
    }
}
