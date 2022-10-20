package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.commons.exceptions.InvalidValueException;

public class BubbleFreezeEffect extends StatusEffect {
    public BubbleFreezeEffect() throws InvalidValueException {
        super();
    }

    @Override
    protected boolean canExecute(Entity entity, AppliedEffect appliedEffect) {
        return false;
    }

    @Override
    public void onStart(AppliedEffect appliedEffect, Entity entity) {
        if (entity instanceof Player) {
            if (!entity.getEnvironment().isGlobalBubbleFreeze()) {
                entity.getEnvironment().setGlobalBubbleFreeze(true);
            }
        }
    }

    @Override
    public void onStop(Entity entity) {
        if (entity instanceof Player) {
            if (entity.getEnvironment().isGlobalBubbleFreeze()) {
                entity.getEnvironment().setGlobalBubbleFreeze(false);
            }
        }
    }
}
