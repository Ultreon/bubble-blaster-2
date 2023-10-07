package com.ultreon.bubbles.command;

import com.ultreon.bubbles.entity.player.Player;

public class EchoCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        var message = String.join(" ", args);
        player.sendSystemMessage(message);
        return true;
    }
}
