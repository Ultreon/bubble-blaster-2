package com.ultreon.bubbles.effect;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeModifier;

import java.util.Map;

public abstract class AttributeStatusEffect extends StatusEffect {
    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }

    @Override
    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {
        super.onStart(appliedEffect, entity);

        for (Map.Entry<Attribute, AttributeModifier> entry : getAttributeModifiers(appliedEffect.getStrength()).entries()) {
            Attribute key = entry.getKey();
            AttributeModifier value = entry.getValue();
            entity.getAttributes().addModifier(key, value);
        }
    }

    @Override
    public void onStop(Entity entity) {
        super.onStop(entity);

        for (Map.Entry<Attribute, AttributeModifier> entry : getAttributeModifiers(-1).entries()) {
            Attribute key = entry.getKey();
            AttributeModifier value = entry.getValue();
            entity.getAttributes().removeModifier(key, value.id());
        }
    }

    public abstract Multimap<Attribute, AttributeModifier> getAttributeModifiers(int strength);
}
