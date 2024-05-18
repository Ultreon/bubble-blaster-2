package dev.ultreon.bubbles.effect;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.ultreon.bubbles.entity.attribute.Attribute;
import dev.ultreon.bubbles.entity.attribute.AttributeModifier;
import dev.ultreon.bubbles.util.exceptions.InvalidValueException;

import java.util.UUID;

public class ScoreStatusEffect extends AttributeStatusEffect {
    private static final UUID SCORE_ID = UUID.fromString("fe71475d-c027-4939-af01-fa40e7538cbf");

    public ScoreStatusEffect() throws InvalidValueException {
        super();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(float strength) {
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attribute.SCORE_MODIFIER, new AttributeModifier(SCORE_ID, AttributeModifier.Type.ADD, strength))
                .build();
    }

}
