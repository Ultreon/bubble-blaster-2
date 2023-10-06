package com.ultreon.bubbles.command;

import com.ultreon.bubbles.effect.StatusEffect;
import com.ultreon.bubbles.effect.StatusEffectInstance;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.commons.exceptions.InvalidValueException;
import com.ultreon.libs.commons.v0.Identifier;

public class EffectCommand implements CommandExecutor {
    @Override
    public boolean execute(Player player, String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equals("clear")) {
                    if (player.getActiveEffects().isEmpty()) {
                        player.sendSystemMessage("No effect are active");
                        return true;
                    }

                    int amount = player.getActiveEffects().size();

                    for (StatusEffectInstance appliedEffect : player.getActiveEffects()) {
                        player.removeEffect(appliedEffect);
                    }

                    player.sendSystemMessage("Removed " + amount + " status effects.");

                    return true;
                }
                break;
            case 2:
                if (args[0].equals("give")) {
                    StatusEffectInstance appliedEffect;
                    try {
                        StatusEffect statusEffect = Registries.EFFECTS.getValue(Identifier.parse(args[1]));
                        if (statusEffect == null) {
                            player.sendSystemMessage("Status effect with key ‘" + args[1] + "’ was not found.");
                            return true;
                        } else {
                            appliedEffect = new StatusEffectInstance(statusEffect, 15, (byte) 1);
                        }
                    } catch (InvalidValueException exception) {
                        player.sendSystemMessage("Invalid key: " + args[1]);
                        return false;
                    }

                    player.addEffect(appliedEffect);
                    return true;
                } else if (args[0].equals("clear")) {
                    if (player.getActiveEffects().isEmpty()) {
                        player.sendSystemMessage("No effect are active");
                        return true;
                    }

                    StatusEffect statusEffect;
                    try {
                        statusEffect = Registries.EFFECTS.getValue(Identifier.parse(args[1]));
                    } catch (InvalidValueException exception) {
                        player.sendSystemMessage("Invalid key: " + args[1]);
                        return false;
                    }
                    if (statusEffect == null) {
                        player.sendSystemMessage("Status effect with key ‘" + args[1] + "’ was not found.");
                        return true;
                    }

                    player.removeEffect(player.getActiveEffect(statusEffect));
                    player.sendSystemMessage("Removed the status effect " + statusEffect.getTranslationText() + ".");

                    return true;
                }
                break;
            case 3:
                if (args[0].equals("give")) {
                    int duration;
                    try {
                        duration = Integer.parseInt(args[2]);
                    } catch (NumberFormatException exception) {
                        player.sendSystemMessage("Invalid number for duration: ‘" + args[2] + "’");
                        return false;
                    }

                    StatusEffectInstance appliedEffect;
                    try {
                        StatusEffect statusEffect = Registries.EFFECTS.getValue(Identifier.parse(args[1]));
                        if (statusEffect == null) {
                            appliedEffect = null;
                        } else {
                            appliedEffect = new StatusEffectInstance(statusEffect, duration, 1);
                        }
                    } catch (InvalidValueException exception) {
                        player.sendSystemMessage("Invalid key: " + args[1]);
                        return false;
                    }

                    if (appliedEffect == null) {
                        player.sendSystemMessage("EffectInstance with key ‘" + args[1] + "’ was not found.");
                        return true;
                    }
                    player.addEffect(appliedEffect);
                    return true;
                }
                break;
            case 4:
                if (args[0].equals("give")) {
                    int duration;
                    try {
                        duration = Integer.parseInt(args[2]);
                    } catch (NumberFormatException exception) {
                        player.sendSystemMessage("Invalid number for duration: ‘" + args[2] + "’");
                        return false;
                    }

                    int strength;
                    try {
                        strength = Integer.parseInt(args[3]);
                    } catch (NumberFormatException exception) {
                        player.sendSystemMessage("Invalid number for strength: ‘" + args[3] + "’");
                        return false;
                    }

                    if (strength < 1) {
                        player.sendSystemMessage("Strength is less than 1; range is 1 to 255!");
                        return false;
                    }

                    if (strength > 255) {
                        player.sendSystemMessage("Strength is more than 255; range is 1 to 255!");
                        return false;
                    }

                    StatusEffectInstance appliedEffect;
                    try {
                        StatusEffect statusEffect = Registries.EFFECTS.getValue(Identifier.parse(args[1]));
                        if (statusEffect == null) {
                            appliedEffect = null;
                        } else {
                            appliedEffect = new StatusEffectInstance(statusEffect, duration, strength);
                        }
                    } catch (InvalidValueException exception) {
                        player.sendSystemMessage("Invalid key: " + args[1]);
                        return false;
                    }

                    if (appliedEffect == null) {
                        player.sendSystemMessage("EffectInstance with key ‘" + args[1] + "’ was not found.");
                        return true;
                    }
                    player.addEffect(appliedEffect);
                    return true;
                }
                break;
        }

        player.sendSystemMessage("Usage: /effect clear");
        player.sendSystemMessage("Usage: /effect give <effect:key> [duration:int] [strength:byte]");

        return false;
    }
}
