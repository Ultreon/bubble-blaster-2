package com.ultreon.bubbles.effect;

import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;

public class ResistanceStatusEffect extends StatusEffect {
    public ResistanceStatusEffect() {
        super();
    }

    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }

    @Override
    public void execute(Entity entity, StatusEffectInstance appliedEffect) {

    }

    @Override
    public AttributeContainer getAttributeModifiers() {
        AttributeContainer map = new AttributeContainer();
        map.setBase(Attribute.DEFENSE, 1f);
        return map;
    }
}
