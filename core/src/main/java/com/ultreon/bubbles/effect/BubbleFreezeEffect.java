package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.Entity;

public class BubbleFreezeEffect extends StatusEffect {
    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }

    @Override
    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {
        entity.getWorld().setBubblesFrozen(true);
    }

    @Override
    public void onStop(Entity entity) {
        entity.getWorld().setBubblesFrozen(false);
    }
}
