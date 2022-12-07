package com.ultreon.bubbles.event.v1.entity;

import com.ultreon.bubbles.entity.player.Player;

@Deprecated
public class PlayerTickEvent extends EntityTickEvent {
    private final Player player;

    public PlayerTickEvent(Player player) {
        super(player);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
