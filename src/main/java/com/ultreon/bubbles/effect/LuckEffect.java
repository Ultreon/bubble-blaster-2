package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;

public class LuckEffect extends StatusEffect {
    @Override
    protected boolean canExecute(Entity entity, AppliedEffect appliedEffect) {
        return false;
    }

    @Override
    public AttributeContainer getAttributeModifiers() {
        AttributeContainer map = new AttributeContainer();
        map.setBase(Attribute.LUCK, 2.0f);
        return map;
    }
}
