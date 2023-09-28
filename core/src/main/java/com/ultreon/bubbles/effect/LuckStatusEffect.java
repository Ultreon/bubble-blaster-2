package com.ultreon.bubbles.effect;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.ultreon.bubbles.entity.attribute.AttributeContainer;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeModifier;

import java.util.UUID;

public class LuckStatusEffect extends AttributeStatusEffect {
    private static final UUID LUCK_ID = UUID.fromString("f0d35fc0-7b46-4c39-b4f4-58bf0d13401a");

    @Override
    protected boolean canExecute(Entity entity, StatusEffectInstance appliedEffect) {
        return false;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(int strength) {
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attribute.LUCK, new AttributeModifier(LUCK_ID, AttributeModifier.Type.ADD, strength))
                .build();
    }
}