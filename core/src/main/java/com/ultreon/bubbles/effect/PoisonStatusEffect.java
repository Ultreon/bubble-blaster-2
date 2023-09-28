package com.ultreon.bubbles.effect;

import com.crashinvaders.vfx.effects.VignettingEffect;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.damage.DamageType;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.event.v1.VfxEffectBuilder;
import com.ultreon.commons.exceptions.InvalidValueException;
import com.ultreon.data.types.MapType;

import java.util.UUID;

public class PoisonStatusEffect extends StatusEffect {
    private static final UUID EFFECT_ID = UUID.fromString("2883cf1e-2639-4dcb-a65c-0e562b79e717");

    public PoisonStatusEffect() throws InvalidValueException {
        super();
    }

    @Override
    public void buildVfx(StatusEffectInstance appliedEffect, VfxEffectBuilder builder) {
        VignettingEffect effect = new VignettingEffect(true);
        long timeActive = appliedEffect.getTimeActive();
        if (timeActive < 100) {
            effect.setIntensity(timeActive / 100f);
        }
        long remainingTime = appliedEffect.getRemainingTime().toMillis();
        if (remainingTime < 1500) {
            effect.setIntensity((1500f - remainingTime) / 1500f);
        }
        builder.set(EFFECT_ID, effect);
    }

    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return System.currentTimeMillis() >= appliedEffect.getTag().getLong("nextDamage");
    }

    @Override
    public void execute(Entity entity, StatusEffectInstance appliedEffect) {
        entity.getEnvironment().attack(entity, (double) appliedEffect.getStrength() / 2, new EntityDamageSource(null, DamageType.POISON));
        MapType tag = appliedEffect.getTag();
        long nextDamage = tag.getLong("nextDamage");
        tag.putLong("nextDamage", nextDamage + 2000L);
    }

    @Override
    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {
        MapType tag = appliedEffect.getTag();
        tag.putLong("nextDamage", System.currentTimeMillis() + 2000);
        tag.putLong("startTime", System.currentTimeMillis());
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onStop(Entity entity) {
        // Do nothing
    }

    @Override
    protected void updateStrength() {
        // Do nothing
    }
}
