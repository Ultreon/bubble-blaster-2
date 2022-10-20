package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.commons.exceptions.InvalidValueException;

public class ParalyzeEffect extends StatusEffect {
    public ParalyzeEffect() throws InvalidValueException {
        super();
    }

    @Override
    protected boolean canExecute(Entity entity, AppliedEffect appliedEffect) {
        return false;
    }

    @Override
    public void onStart(AppliedEffect appliedEffect, Entity entity) {
        if (entity instanceof Player) {
            entity.setMobile(false);
        }
    }

    @Override
    public void onStop(Entity entity) {
        if (entity instanceof Player) {
            entity.setMobile(true);
        }
    }

    @Override
    protected void updateStrength() {
        // Do nothing.
    }
}
