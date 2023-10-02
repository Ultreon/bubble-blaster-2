package com.ultreon.bubbles.entity.spawning;

import com.ultreon.bubbles.entity.player.Player;

public class SpawnReason {
    public static final SpawnReason LOAD = new SpawnReason();
    public static final SpawnReason COMMAND = new SpawnReason();
    public static final SpawnReason PLAYER = new SpawnReason();

    public static SpawnReason natural(SpawnUsage random, int retry) {
        return new NaturalSpawnReason(random, retry);
    }

    public static SpawnReason trigger(Player player) {
        return new TriggerSpawnReason(player);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{}";
    }
}
