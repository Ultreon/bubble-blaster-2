package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.commons.exceptions.InvalidValueException;

public class MultiScoreEffect extends StatusEffect {
    public MultiScoreEffect() throws InvalidValueException {
        super();
    }

    @Override
    public AttributeContainer getAttributeModifiers() {
        AttributeContainer attributeMap = new AttributeContainer();
        attributeMap.setBase(Attribute.SCORE_MODIFIER, 1f);
        return attributeMap;
    }

    @Override
    protected boolean canExecute(Entity entity, AppliedEffect appliedEffect) {
        return false;
    }
}
