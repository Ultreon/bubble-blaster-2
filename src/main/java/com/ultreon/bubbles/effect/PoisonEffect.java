package com.ultreon.bubbles.effect;

import com.jhlabs.image.HSBAdjustFilter;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.damage.DamageSourceType;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.event.v2.FilterBuilder;
import com.ultreon.commons.exceptions.InvalidValueException;
import net.querz.nbt.tag.CompoundTag;

public class PoisonEffect extends StatusEffect {
    public PoisonEffect() throws InvalidValueException {
        super();
    }

    @Override
    public void onFilter(AppliedEffect appliedEffect, FilterBuilder builder) {
        HSBAdjustFilter filter = new HSBAdjustFilter();
        filter.setHFactor((float) (System.currentTimeMillis() - appliedEffect.getStartTime()) / 3000 % 1);
        builder.addFilter(filter);
    }

    @Override
    protected boolean canExecute(Entity entity, AppliedEffect appliedEffect) {
        return System.currentTimeMillis() >= appliedEffect.getTag().getLong("nextDamage");
    }

    @Override
    public void execute(Entity entity, AppliedEffect appliedEffect) {
        entity.getEnvironment().attack(entity, (double) appliedEffect.getStrength() / 2, new EntityDamageSource(null, DamageSourceType.POISON));
        CompoundTag tag = appliedEffect.getTag();
        long nextDamage = tag.getLong("nextDamage");
        tag.putLong("nextDamage", nextDamage + 2000L);
    }

    @Override
    public void onStart(AppliedEffect appliedEffect, Entity entity) {
        CompoundTag tag = appliedEffect.getTag();
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
