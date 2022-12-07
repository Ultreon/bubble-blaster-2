package com.ultreon.bubbles.command;

import com.ultreon.bubbles.entity.player.Player;

public class GameOverCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        player.getEnvironment().triggerGameOver();
        return true;
    }
}
