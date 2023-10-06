package com.ultreon.bubbles.command;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.entity.player.Player;

public class GameOverCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        BubbleBlaster.invokeTick(() -> {
            player.getWorld().triggerGameOver();
        });
        return true;
    }
}
