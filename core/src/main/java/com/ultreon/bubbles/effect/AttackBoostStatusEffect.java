package com.ultreon.bubbles.effect;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.ultreon.bubbles.entity.attribute.Attribute;
import com.ultreon.bubbles.entity.attribute.AttributeModifier;
import com.ultreon.commons.exceptions.InvalidValueException;

import java.util.UUID;

public class AttackBoostStatusEffect extends AttributeStatusEffect {
    private static final UUID ATTACK_BOOST_ID = UUID.fromString("eb2b4e86-77a9-4c2b-af97-783e41c4db42");

    public AttackBoostStatusEffect() throws InvalidValueException {
        super();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(int strength) {
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attribute.ATTACK, new AttributeModifier(ATTACK_BOOST_ID, AttributeModifier.Type.ADD, strength))
                .build();
    }
}
