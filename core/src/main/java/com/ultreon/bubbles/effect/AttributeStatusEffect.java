package com.ultreon.bubbles.effect;

import com.google.common.collect.Multimap;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeModifier;

public abstract class AttributeStatusEffect extends StatusEffect {
    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }

    @Override
    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {
        super.onStart(appliedEffect, entity);

        for (var entry : this.getAttributeModifiers(appliedEffect.getStrength()).entries()) {
            var key = entry.getKey();
            var value = entry.getValue();
            entity.getAttributes().addModifier(key, value);
        }
    }

    @Override
    public void onStop(Entity entity) {
        super.onStop(entity);

        for (var entry : this.getAttributeModifiers(-1).entries()) {
            var key = entry.getKey();
            var value = entry.getValue();
            entity.getAttributes().removeModifier(key, value.id());
        }
    }

    public abstract Multimap<Attribute, AttributeModifier> getAttributeModifiers(float strength);
}
