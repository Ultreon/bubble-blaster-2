package dev.ultreon.bubbles.effect;

import com.crashinvaders.vfx.effects.VignettingEffect;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.damage.DamageType;
import dev.ultreon.bubbles.entity.damage.EntityDamageSource;
import dev.ultreon.bubbles.event.v1.VfxEffectBuilder;
import dev.ultreon.bubbles.util.exceptions.InvalidValueException;

import java.util.UUID;

public class PoisonStatusEffect extends StatusEffect {
    private static final UUID EFFECT_ID = UUID.fromString("2883cf1e-2639-4dcb-a65c-0e562b79e717");

    public PoisonStatusEffect() throws InvalidValueException {
        super();
    }

    @Override
    public void buildVfx(StatusEffectInstance appliedEffect, VfxEffectBuilder builder) {
        var effect = new VignettingEffect(true);
        var timeActive = appliedEffect.getTimeActive();
        if (timeActive < 100) {
            effect.setIntensity(timeActive / 100f);
        }
        var remainingTime = appliedEffect.getRemainingTime().toMillis();
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
        entity.getWorld().attack(entity, (double) appliedEffect.getStrength() / 2, new EntityDamageSource(null, DamageType.POISON));
        var tag = appliedEffect.getTag();
        var nextDamage = tag.getLong("nextDamage");
        tag.putLong("nextDamage", nextDamage + 2000L);
    }

    @Override
    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {
        var tag = appliedEffect.getTag();
        tag.putLong("nextDamage", System.currentTimeMillis() + 2000);
        tag.putLong("startTime", System.currentTimeMillis());
    }
}
