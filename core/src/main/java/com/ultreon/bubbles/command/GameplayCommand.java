package com.ultreon.bubbles.command;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.commons.exceptions.InvalidValueException;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.registries.v0.exception.RegistryException;

public class GameplayCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length >= 1 && args[0].equals("event")) {
            if (args.length == 3) {
                GameplayEvent event;
                try {
                    event = Registries.GAMEPLAY_EVENTS.getValue(Identifier.parse(args[2]));
                } catch (InvalidValueException exception) {
                    player.sendSystemMessage("Invalid key: " + args[1]);
                    return false;
                } catch (RegistryException exception) {
                    player.sendSystemMessage("Gameplay event with key ‘" + args[1] + "’ was not found.");
                    return true;
                }
                switch (args[1]) {
                    case "begin":
                        BubbleBlaster.invokeTick(() -> {
                            if (player.getWorld().beginEvent(event)) {
                                player.sendSystemMessage("Started " + event.getTranslationText() + " gameplay event.");
                            } else {
                                player.sendSystemMessage("A gameplay event was already active.");
                            }
                        });
                        break;
                    case "end":
                        BubbleBlaster.invokeTick(() -> {
                            var activeEvent = player.getWorld().getActiveEvent();
                            if (activeEvent == null) {
                                player.sendSystemMessage("There isn't any gameplay event active.");
                            }
                            if (player.getWorld().endEvent(event)) {
                                player.sendSystemMessage("Ended " + event.getTranslationText() + " gameplay event.");
                            } else {
                                player.sendSystemMessage("The " + event.getTranslationText() + " gameplay event wasn't active.");
                            }
                        });
                        break;
                }
                player.sendSystemMessage("Usage: /gameplay event (begin|end) <gameplay-event> OR");
                player.sendSystemMessage("    /gameplay event end");
            }
            if (args.length == 2 && args[1].equals("end")) {
                BubbleBlaster.invokeTick(() -> {
                    var activeEvent = player.getWorld().getActiveEvent();
                    if (activeEvent == null) {
                        player.sendSystemMessage("There isn't any gameplay event active.");
                        return;
                    }
                    player.getWorld().endEvent();
                    player.sendSystemMessage("Ended " + activeEvent.getTranslationText() + " gameplay event.");
                });
            }
            player.sendSystemMessage("Usage: /gameplay event (begin|end) <gameplay-event> OR");
            player.sendSystemMessage("    /gameplay event end");
            return true;
        }
        player.sendSystemMessage("Usage: /gameplay event ...");
        return true;
    }
}
