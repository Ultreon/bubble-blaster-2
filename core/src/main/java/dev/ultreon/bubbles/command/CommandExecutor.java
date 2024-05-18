package dev.ultreon.bubbles.command;

import dev.ultreon.bubbles.entity.player.Player;

public interface CommandExecutor {
    @SuppressWarnings("UnusedReturnValue")
    boolean execute(Player player, String[] args);
}
