package dev.ultreon.bubbles.command;

import dev.ultreon.bubbles.entity.player.Player;

public class LevelCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 2) {
            int value;
            switch (args[0]) {
                case "add":
                    try {
                        value = Integer.parseInt(args[1]);
                    } catch (NumberFormatException exception) {
                        player.sendSystemMessage("Invalid number: ‘" + args[1] + "’");
                        return false;
                    }
                    player.setLevel(player.getLevel() + value);
                    return true;
                case "set":
                    try {
                        value = Integer.parseInt(args[1]);
                    } catch (NumberFormatException exception) {
                        player.sendSystemMessage("Invalid number: ‘" + args[1] + "’");
                        return false;
                    }
                    player.setLevel(value);
                    return true;
                case "subtract":
                    try {
                        value = Integer.parseInt(args[1]);
                    } catch (NumberFormatException exception) {
                        player.sendSystemMessage("Invalid number: ‘" + args[1] + "’");
                        return false;
                    }
                    player.setLevel(player.getLevel() - value);
                    return true;
            }
        } else if (args.length == 1) {
            switch (args[0]) {
                case "up":
                    player.levelUp();
                    return true;
                case "down":
                    player.levelDown();
                    return true;
            }
        }

        player.sendSystemMessage("Usage: /level <up|down>");
        player.sendSystemMessage("Usage: /level <add|set|subtract> <value>");

        return false;
    }
}
