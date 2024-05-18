package dev.ultreon.bubbles.command;

import dev.ultreon.bubbles.entity.attribute.Attribute;
import dev.ultreon.bubbles.entity.player.Player;

import java.util.Objects;

public class MaxHealthCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 2) {
            float value;
            switch (args[0]) {
                case "add":
                    try {
                        value = Float.parseFloat(args[1]);
                    } catch (NumberFormatException exception) {
                        player.sendSystemMessage("Invalid number: ‘" + args[1] + "’");
                        return false;
                    }
                    player.getAttributes().setBase(Attribute.MAX_HEALTH, player.getAttributes().getBase(Attribute.MAX_HEALTH) + value);
                    return true;
                case "set":
                    try {
                        value = Float.parseFloat(args[1]);
                    } catch (NumberFormatException exception) {
                        player.sendSystemMessage("Invalid number: ‘" + args[1] + "’");
                        return false;
                    }
                    player.getAttributes().setBase(Attribute.MAX_HEALTH, value);
                    return true;
                case "subtract":
                    try {
                        value = Float.parseFloat(args[1]);
                    } catch (NumberFormatException exception) {
                        player.sendSystemMessage("Invalid number: ‘" + args[1] + "’");
                        return false;
                    }
                    player.getAttributes().setBase(Attribute.MAX_HEALTH, player.getAttributes().getBase(Attribute.MAX_HEALTH) - value);
                    return true;
            }
        } else if (args.length == 1) {
            if (Objects.equals(args[0], "get")) {
                player.sendSystemMessage("Current health is: " + player.getMaxHealth());
            }
        }

        player.sendSystemMessage("Usage: /max-health <add|set|subtract> <value>");

        return false;
    }
}
