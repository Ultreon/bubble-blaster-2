package dev.ultreon.bubbles.command;

import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.init.GameplayEvents;
import dev.ultreon.libs.datetime.v0.Duration;

import static dev.ultreon.bubbles.BubbleBlaster.TPS;

public class BloodMoonCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "begin":
                    BubbleBlaster.invokeTick(() -> player.getWorld().beginEvent(GameplayEvents.BLOOD_MOON_EVENT));
                    break;
                case "end":
                    BubbleBlaster.invokeTick(() -> player.getWorld().endEvent(GameplayEvents.BLOOD_MOON_EVENT));
                    break;
                case "next": {
                    var nextInMs = (long) (player.getWorld().getNextBloodMoon() / (double) TPS * 1000.0);
                    if (nextInMs < 0) {
                        player.sendSystemMessage("Blood moon is active right now.");
                    } else {
                        player.sendSystemMessage("Next blood moon in " + Duration.ofMilliseconds(nextInMs).toSimpleString() + ".");
                    }
                    break;
                }
                case "ends": {
                    var nextInMs = (long) (GameplayEvents.BLOOD_MOON_EVENT.getDeactivateTicks() / (double) TPS * 1000.0);
                    if (nextInMs < 0) {
                        player.sendSystemMessage("Blood moon is inactive right now.");
                    } else {
                        player.sendSystemMessage("Blood moon will end in " + Duration.ofMilliseconds(nextInMs).toSimpleString() + ".");
                    }
                    break;
                }
            }
        } else {
            player.sendSystemMessage("Usage: /blood-moon (trigger|next)");
        }
        return true;
    }
}
