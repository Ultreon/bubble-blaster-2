package com.ultreon.bubbles.entity.attribute;

import com.ultreon.bubbles.util.Enums;
import com.ultreon.data.types.MapType;

import java.util.Objects;
import java.util.UUID;

public final class AttributeModifier {
    private final UUID id;
    private final Type type;
    private final double value;

    public AttributeModifier(UUID id, Type type, double value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public MapType serialize() {
        var nbt = new MapType();
        nbt.putUUID("id", this.id);
        nbt.putString("type", this.type.name());
        nbt.putDouble("value", this.value);

        return nbt;
    }

    public static AttributeModifier deserialize(MapType tag) {
        var id = tag.getUUID("id");
        var type = Enums.byName(tag.getString("type"), Type.ADD);
        var value = tag.getDouble("value");

        return new AttributeModifier(id, type, value);
    }

    public enum Type {
        ADD, MULTIPLY
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        var modifier = (AttributeModifier) o;
        return Objects.equals(this.id, modifier.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public UUID id() {
        return this.id;
    }

    public Type type() {
        return this.type;
    }

    public double value() {
        return this.value;
    }

    @Override
    public String toString() {
        return "AttributeModifier[" +
                "id=" + this.id + ", " +
                "type=" + this.type + ", " +
                "value=" + this.value + ']';
    }

}
