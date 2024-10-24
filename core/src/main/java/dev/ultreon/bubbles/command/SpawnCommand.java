package dev.ultreon.bubbles.command;

import dev.ultreon.bubbles.bubble.BubbleType;
import dev.ultreon.bubbles.entity.Bubble;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.entity.spawning.SpawnInformation;
import dev.ultreon.bubbles.entity.spawning.SpawnUsage;
import dev.ultreon.bubbles.random.JavaRandom;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.util.exceptions.InvalidValueException;
import dev.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.libs.registries.v0.exception.RegistryException;

import java.util.Locale;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendSystemMessage("Usage: /spawn bubble [...]");
            return false;
        }

        switch (args.length) {
            case 2:
                if (args[0].equals("bubble")) {
                    BubbleType bubbleType = null;
                    try {
                        bubbleType = Registries.BUBBLES.getValue(Identifier.parse(args[1]));
                    } catch (InvalidValueException exception) {
                        player.sendSystemMessage("Invalid key: " + args[1]);
                        return false;
                    } catch (RegistryException ignored) {

                    }

                    if (bubbleType == null) {
                        player.sendSystemMessage("Bubble with key ‘" + args[1] + "’ was not found.");
                        return true;
                    }

                    var bubble = new Bubble(player.getWorld());
                    player.getWorld().spawn(bubble, SpawnInformation.naturalSpawn(null, new JavaRandom(), SpawnUsage.BUBBLE_SPAWN, player.getWorld()));
                    bubble.setBubbleType(bubbleType);
                    player.sendSystemMessage("Spawned bubble with type " + bubble.getBubbleType().getTranslationText());
                    return true;
                }
                break;
            case 3:
                if (args[0].equals("bubble")) {
                    BubbleType bubbleType = null;
                    try {
                        bubbleType = Registries.BUBBLES.getValue(Identifier.parse(args[1]));
                    } catch (InvalidValueException exception) {
                        player.sendSystemMessage("Invalid key: " + args[1]);
                        return false;
                    } catch (RegistryException ignored) {

                    }

                    if (bubbleType == null) {
                        player.sendSystemMessage("Bubble with key ‘" + args[1] + "’ was not found.");
                        return true;
                    }

                    Bubble.Variant variant;
                    try {
                        variant = Bubble.Variant.valueOf(args[2].toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException e) {
                        player.sendSystemMessage("No bubble variant with name ‘" + args[2] + "’ was found.");
                        return false;
                    }
                    var finalBubbleType = bubbleType;
                    var bubble = new Bubble(player.getWorld(), variant);
                    player.getWorld().spawn(bubble, SpawnInformation.naturalSpawn(null, new JavaRandom(), SpawnUsage.BUBBLE_SPAWN, player.getWorld()));
                    bubble.setBubbleType(finalBubbleType);
                    player.sendSystemMessage("Spawned bubble with type " + bubble.getBubbleType().getTranslationText());
                    return true;
                }
                break;
        }

        player.sendSystemMessage("Usage: /spawn bubble <bubble:key> [radius:int] [speed:double]");
        return false;
    }
}
