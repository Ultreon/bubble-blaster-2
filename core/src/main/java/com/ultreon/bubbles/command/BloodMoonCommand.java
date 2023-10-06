package com.ultreon.bubbles.command;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.init.GameplayEvents;
import com.ultreon.commons.util.TimeUtils;

import java.time.Duration;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

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
                    long nextInMs = (long) (player.getWorld().getNextBloodMoon() / (double) TPS * 1000.0);
                    if (nextInMs < 0) {
                        player.sendSystemMessage("Blood moon is active right now.");
                    } else {
                        player.sendSystemMessage("Next blood moon in " + TimeUtils.formatDuration(Duration.ofMillis(nextInMs)) + ".");
                    }
                    break;
                }
                case "ends": {
                    long nextInMs = (long) (GameplayEvents.BLOOD_MOON_EVENT.getDeactivateTicks() / (double) TPS * 1000.0);
                    if (nextInMs < 0) {
                        player.sendSystemMessage("Blood moon is inactive right now.");
                    } else {
                        player.sendSystemMessage("Blood moon will end in " + TimeUtils.formatDuration(Duration.ofMillis(nextInMs)) + ".");
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
