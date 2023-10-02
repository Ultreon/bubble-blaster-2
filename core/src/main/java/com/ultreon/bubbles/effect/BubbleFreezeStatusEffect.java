package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.commons.exceptions.InvalidValueException;

public class BubbleFreezeStatusEffect extends StatusEffect {
    public BubbleFreezeStatusEffect() throws InvalidValueException {
        super();
    }

    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }

    @Override
    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {
        if (entity instanceof Player) {
            if (!entity.getWorld().isBubblesFrozen()) {
                entity.getWorld().setBubblesFrozen(true);
            }
        }
    }

    @Override
    public void onStop(Entity entity) {
        if (entity instanceof Player) {
            if (entity.getWorld().isBubblesFrozen()) {
                entity.getWorld().setBubblesFrozen(false);
            }
        }
    }
}
