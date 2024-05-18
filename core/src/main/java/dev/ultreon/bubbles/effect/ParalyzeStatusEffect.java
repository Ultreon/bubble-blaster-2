package dev.ultreon.bubbles.effect;

import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.util.exceptions.InvalidValueException;

public class ParalyzeStatusEffect extends StatusEffect {
    public ParalyzeStatusEffect() throws InvalidValueException {
        super();
    }

    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }

    @Override
    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {
        if (entity instanceof Player) {
            entity.canMove = false;
        }
    }

    @Override
    public void onStop(Entity entity) {
        if (entity instanceof Player) {
            entity.canMove = true;
        }
    }
}
