package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.LivingEntity;

public class InvincibilityEffect extends StatusEffect {
    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }

    @Override
    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setInvincible(true);
        }
    }

    @Override
    public void onStop(Entity entity) {
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setInvincible(false);
        }
    }
}
