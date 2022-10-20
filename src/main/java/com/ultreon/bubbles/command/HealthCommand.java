package com.ultreon.bubbles.command;

import com.ultreon.bubbles.entity.player.Player;

import java.util.Objects;

public class HealthCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 2) {
            float value;
            switch (args[0]) {
                case "add" -> {
                    try {
                        value = Float.parseFloat(args[1]);
                    } catch (NumberFormatException exception) {
                        player.sendMessage("Invalid number: ‘" + args[1] + "’");
                        return false;
                    }
                    player.setHealth(player.getHealth() + value);
                    return true;
                }
                case "set" -> {
                    try {
                        value = Float.parseFloat(args[1]);
                    } catch (NumberFormatException exception) {
                        player.sendMessage("Invalid number: ‘" + args[1] + "’");
                        return false;
                    }
                    player.setHealth(value);
                    return true;
                }
                case "subtract" -> {
                    try {
                        value = Float.parseFloat(args[1]);
                    } catch (NumberFormatException exception) {
                        player.sendMessage("Invalid number: ‘" + args[1] + "’");
                        return false;
                    }
                    player.setHealth(player.getHealth() - value);
                    return true;
                }
            }
        } else if (args.length == 1) {
            if (Objects.equals(args[0], "get")) {
                player.sendMessage("Current health is: " + player.getHealth());
            }
        }

        player.sendMessage("Usage: /health <add|set|subtract> <value>");

        return false;
    }
}
