package dev.ultreon.bubbles.entity.spawning;

import dev.ultreon.bubbles.util.Randomizer;

public final class SpawnUsage implements Hashable {
    private static int currentIdx;

    public static final SpawnUsage DEFAULT = new SpawnUsage();
    public static final SpawnUsage TRIGGERED = new SpawnUsage();
    public static final SpawnUsage INIT_SPAWN = new SpawnUsage();
    public static final SpawnUsage SPAWN = new SpawnUsage();
    private final int index;

    public SpawnUsage() {
        this.index = SpawnUsage.currentIdx++;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public long hash() {
        return Randomizer.hash(this.index);
    }
}
