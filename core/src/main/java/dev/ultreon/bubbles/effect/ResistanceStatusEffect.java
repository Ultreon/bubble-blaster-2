package dev.ultreon.bubbles.effect;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.ultreon.bubbles.entity.attribute.Attribute;
import dev.ultreon.bubbles.entity.attribute.AttributeModifier;

import java.util.UUID;

public class ResistanceStatusEffect extends AttributeStatusEffect {
    private static final UUID RESISTANCE_ID = UUID.fromString("f498dd88-eca5-436f-9f85-ba6a934f424a");

    public ResistanceStatusEffect() {
        super();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(float strength) {
        return ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attribute.DEFENSE, new AttributeModifier(RESISTANCE_ID, AttributeModifier.Type.ADD, strength))
                .build();
    }
}
