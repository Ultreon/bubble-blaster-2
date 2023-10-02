package com.ultreon.bubbles.entity.spawning;

import com.ultreon.bubbles.entity.player.Player;

public class TriggerSpawnReason extends SpawnReason {
    private final Player player;

    public TriggerSpawnReason(Player player) {
        super();
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
