package com.ultreon.bubbles.command;

import com.ultreon.bubbles.entity.player.Player;

public interface CommandExecutor {
    @SuppressWarnings("UnusedReturnValue")
    boolean execute(Player player, String[] args);
}
