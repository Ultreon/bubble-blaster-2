package com.ultreon.bubbles.entity.attribute;

import com.ultreon.bubbles.util.EnumUtils;
import com.ultreon.data.types.MapType;

import java.util.Objects;
import java.util.UUID;

public record AttributeModifier(UUID id, Type type, double value) {
    public MapType serialize() {
        MapType nbt = new MapType();
        nbt.putUUID("id", this.id);
        nbt.putString("type", this.type.name());
        nbt.putDouble("value", this.value);

        return nbt;
    }

    public static AttributeModifier deserialize(MapType tag) {
        UUID id = tag.getUUID("id");
        Type type = EnumUtils.byName(tag.getString("type"), Type.ADD);
        double value = tag.getDouble("value");

        return new AttributeModifier(id, type, value);
    }

    public enum Type {
        ADD, MULTIPLY
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        AttributeModifier modifier = (AttributeModifier) o;
        return Objects.equals(this.id, modifier.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
