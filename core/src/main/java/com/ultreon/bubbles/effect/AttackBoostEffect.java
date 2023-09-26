package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.commons.exceptions.InvalidValueException;

public class AttackBoostEffect extends StatusEffect {
    public AttackBoostEffect() throws InvalidValueException {
        super();
    }

    @Override
    public void execute(Entity entity, StatusEffectInstance appliedEffect) {

    }

    @Override
    protected void updateStrength() {

    }

    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }

    @Override
    public AttributeContainer getAttributeModifiers() {
        AttributeContainer attributes = new AttributeContainer();
        attributes.setBase(Attribute.ATTACK, 1f);

        return attributes;
    }
}
