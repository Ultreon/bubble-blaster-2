package com.ultreon.bubbles.command;

import com.ultreon.bubbles.entity.player.Player;

public class TeleportCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 2) {
            float x, y;

            try {
                x = Float.parseFloat(args[0]);
                y = Float.parseFloat(args[1]);
            } catch (NumberFormatException exception) {
                try {
                    x = Long.parseLong(args[0]);
                } catch (NumberFormatException exception1) {
                    player.sendMessage("Invalid number for x!");
                    return false;
                }
                try {
                    y = Long.parseLong(args[1]);
                } catch (NumberFormatException exception1) {
                    player.sendMessage("Invalid number for y!");
                    return false;
                }
            }

            player.teleport(x, y);
            return true;
        }
        return false;
    }
}
