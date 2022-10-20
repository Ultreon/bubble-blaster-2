package com.ultreon.bubbles.command;

import com.ultreon.bubbles.entity.player.Player;

public class HealCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        player.setHealth(player.getMaxHealth());
        return true;
    }
}
