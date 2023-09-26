package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.event.v1.VfxEffectBuilder;

public class BlindnessStatusEffect extends StatusEffect {
    private long startTime;

    public BlindnessStatusEffect() {
        super();
    }

    @Override
    public void buildVfx(StatusEffectInstance appliedEffect, VfxEffectBuilder builder) {
//        ContrastFilter filter = new ContrastFilter();
//        filter.setContrast(0.25f + 0.25f / (float) appliedEffect.getStrength());
//        builder.addEffect(filter);

//        ContrastFilter filter1 = new ContrastFilter();
//        filter1.setBrightness(0.5f / (float) appliedEffect.getStrength());
//        builder.addEffect(filter1);
    }

    @Override
    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {
        startTime = System.currentTimeMillis();
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

    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }

    @SuppressWarnings("unused")
    public long getStartTime() {
        return startTime;
    }
}
