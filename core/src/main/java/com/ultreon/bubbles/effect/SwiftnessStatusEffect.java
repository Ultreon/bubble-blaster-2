package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeModifier;
import com.ultreon.commons.exceptions.InvalidValueException;

import java.util.UUID;

public class SwiftnessStatusEffect extends StatusEffect {
    private static final UUID SPEED_MODIFIER = UUID.fromString("6893c418-6fa3-457b-b012-6b07d1af7e12");

    public SwiftnessStatusEffect() throws InvalidValueException {
        super();
    }

    @Override
    public void onStart(StatusEffectInstance appliedEffect, Entity entity) {
        super.onStart(appliedEffect, entity);

        entity.getAttributes().addModifier(Attribute.SPEED, new AttributeModifier(SPEED_MODIFIER, AttributeModifier.Type.MULTIPLY, 1.5));
    }

    @Override
    public void onStop(Entity entity) {
        super.onStop(entity);

        entity.getAttributes().removeModifier(Attribute.SPEED, SPEED_MODIFIER);
    }

    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }
}
