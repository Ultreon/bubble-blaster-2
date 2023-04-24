package com.ultreon.bubbles.effect;

import com.jhlabs.image.ContrastFilter;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.event.v1.FilterBuilder;

public class BlindnessEffect extends StatusEffect {
    private long startTime;

    public BlindnessEffect() {
        super();
    }

    @Override
    public void onFilter(AppliedEffect appliedEffect, FilterBuilder builder) {
        ContrastFilter filter = new ContrastFilter();
        filter.setContrast(0.25f + 0.25f / (float) appliedEffect.getStrength());
        builder.addFilter(filter);

        ContrastFilter filter1 = new ContrastFilter();
        filter1.setBrightness(0.5f / (float) appliedEffect.getStrength());
        builder.addFilter(filter1);
    }

    @Override
    public void onStart(AppliedEffect appliedEffect, Entity entity) {
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
    protected boolean canExecute(Entity entity, AppliedEffect appliedEffect) {
        return false;
    }

    @SuppressWarnings("unused")
    public long getStartTime() {
        return startTime;
    }
}
