package com.ultreon.bubbles.event.v1.entity;

import com.ultreon.bubbles.entity.damage.DamageSource;
import com.ultreon.bubbles.entity.player.Player;

@Deprecated
public class PlayerDamageEvent extends EntityDamageEvent {
    private final Player player;

    public PlayerDamageEvent(Player player, DamageSource damageSource) {
        super(player, damageSource);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
