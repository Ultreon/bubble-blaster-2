package dev.ultreon.bubbles.effect;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.ultreon.bubbles.entity.attribute.Attribute;
import dev.ultreon.bubbles.entity.attribute.AttributeModifier;
import dev.ultreon.bubbles.util.exceptions.InvalidValueException;

import java.util.UUID;

public class SwiftnessStatusEffect extends AttributeStatusEffect {
    private static final UUID SPEED_MODIFIER = UUID.fromString("6893c418-6fa3-457b-b012-6b07d1af7e12");

    public SwiftnessStatusEffect() throws InvalidValueException {
        super();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(float strength) {
        return ImmutableMultimap.of(Attribute.SPEED, new AttributeModifier(SPEED_MODIFIER, AttributeModifier.Type.MULTIPLY, 1.5));
    }
}
