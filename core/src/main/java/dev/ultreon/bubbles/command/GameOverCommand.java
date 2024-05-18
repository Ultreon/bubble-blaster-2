package dev.ultreon.bubbles.command;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.entity.player.Player;

public class GameOverCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        BubbleBlaster.invokeTick(() -> {
            player.getWorld().triggerGameOver();
        });
        return true;
    }
}
