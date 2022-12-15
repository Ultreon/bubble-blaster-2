package com.ultreon.bubbles.command;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.effect.AppliedEffect;
import com.ultreon.bubbles.effect.StatusEffect;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.commons.exceptions.InvalidValueException;

public class EffectCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 1) {
            if (args[0].equals("clear")) {
                if (player.getActiveEffects().isEmpty()) {
                    player.sendMessage("No effect are active");
                    return true;
                }

                int amount = player.getActiveEffects().size();

                for (AppliedEffect appliedEffect : player.getActiveEffects()) {
                    player.removeEffect(appliedEffect);
                }

                player.sendMessage("Removed " + amount + " effects.");

                return true;
            }
        } else if (args.length == 2) {
            if (args[0].equals("give")) {
                AppliedEffect appliedEffect;
                try {
                    StatusEffect statusEffect = Registry.EFFECTS.getValue(Identifier.parse(args[1]));
                    if (statusEffect == null) {
                        appliedEffect = null;
                    } else {
                        appliedEffect = new AppliedEffect(statusEffect, 15, (byte) 1);
                    }
                } catch (InvalidValueException exception) {
                    player.sendMessage("Invalid key: " + args[1]);
                    return false;
                }

                if (appliedEffect == null) {
                    player.sendMessage("EffectInstance with key ‘" + args[1] + "’ was not found.");
                    return true;
                }
                player.addEffect(appliedEffect);
                return true;
            }
        } else if (args.length == 3) {
            if (args[0].equals("give")) {
                int duration;
                try {
                    duration = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    player.sendMessage("Invalid number for duration: ‘" + args[2] + "’");
                    return false;
                }

                AppliedEffect appliedEffect;
                try {
                    StatusEffect statusEffect = Registry.EFFECTS.getValue(Identifier.parse(args[1]));
                    if (statusEffect == null) {
                        appliedEffect = null;
                    } else {
                        appliedEffect = new AppliedEffect(statusEffect, duration, 1);
                    }
                } catch (InvalidValueException exception) {
                    player.sendMessage("Invalid key: " + args[1]);
                    return false;
                }

                if (appliedEffect == null) {
                    player.sendMessage("EffectInstance with key ‘" + args[1] + "’ was not found.");
                    return true;
                }
                player.addEffect(appliedEffect);
                return true;
            }
        } else if (args.length == 4) {
            if (args[0].equals("give")) {
                int duration;
                try {
                    duration = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    player.sendMessage("Invalid number for duration: ‘" + args[2] + "’");
                    return false;
                }

                int strength;
                try {
                    strength = Integer.parseInt(args[3]);
                } catch (NumberFormatException exception) {
                    player.sendMessage("Invalid number for strength: ‘" + args[3] + "’");
                    return false;
                }

                if (strength < 1) {
                    player.sendMessage("Strength is less than 1; range is 1 to 255!");
                    return false;
                }

                if (strength > 255) {
                    player.sendMessage("Strength is more than 255; range is 1 to 255!");
                    return false;
                }

                AppliedEffect appliedEffect;
                try {
                    StatusEffect statusEffect = Registry.EFFECTS.getValue(Identifier.parse(args[1]));
                    if (statusEffect == null) {
                        appliedEffect = null;
                    } else {
                        appliedEffect = new AppliedEffect(statusEffect, duration, strength);
                    }
                } catch (InvalidValueException exception) {
                    player.sendMessage("Invalid key: " + args[1]);
                    return false;
                }

                if (appliedEffect == null) {
                    player.sendMessage("EffectInstance with key ‘" + args[1] + "’ was not found.");
                    return true;
                }
                player.addEffect(appliedEffect);
                return true;
            }
        }

        player.sendMessage("Usage: /effect clear");
        player.sendMessage("Usage: /effect give <effect:key> [duration:int] [strength:byte]");

        return false;
    }
}
