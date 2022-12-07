package com.ultreon.bubbles.entity.attribute;

import com.ultreon.bubbles.util.EnumUtils;
import com.ultreon.bubbles.util.NbtUtils;
import net.querz.nbt.tag.CompoundTag;

import java.util.Objects;
import java.util.UUID;

public record AttributeModifier(UUID id, Type type, double value) {
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLongArray("id", NbtUtils.saveUuid(id));
        nbt.putString("type", type.name());
        nbt.putDouble("value", value);

        return nbt;
    }

    public static AttributeModifier deserialize(CompoundTag tag) {
        UUID id = NbtUtils.loadUuid(tag.getLongArray("id"));
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
        if (o == null || getClass() != o.getClass()) return false;
        AttributeModifier modifier = (AttributeModifier) o;
        return Objects.equals(id, modifier.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
