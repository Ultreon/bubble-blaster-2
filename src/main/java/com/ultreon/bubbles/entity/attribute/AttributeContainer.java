package com.ultreon.bubbles.entity.attribute;

import com.ultreon.bubbles.common.holders.ListDataHolder;
import com.ultreon.commons.exceptions.TODO;
import com.ultreon.commons.function.primitive.BiDouble2DoubleFunction;
import com.ultreon.commons.function.primitive.Double2DoubleFunction;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings({"DuplicatedCode", "unused"})
public class AttributeContainer implements ListDataHolder<CompoundTag> {
    private final Map<Attribute, Double> map = new HashMap<>();
    private final Map<Attribute, List<AttributeModifier>> modifierMap = new HashMap<>();

    public AttributeContainer() {

    }

    public void setBase(Attribute attribute, double value) {
        this.map.put(attribute, value);
    }

    public double getBase(Attribute attribute) {
        @Nullable Double value = this.map.get(attribute);
        if (value == null) {
            throw new NoSuchElementException("Attribute \"" + attribute.name() + "\" has no set base value.");
        }
        return value;
    }

    public void getModified(Attribute attribute, BiDouble2DoubleFunction function, List<AttributeContainer> attributeMaps) {
        if (!this.map.containsKey(attribute)) {
            throw new NoSuchElementException("Attribute \"" + attribute.name() + "\" has no set base value.");
        }

        double f = getBase(attribute);
        for (AttributeContainer map : attributeMaps) {
            f *= function.apply(f, map.getBase(attribute));
        }
    }

    public void getModified(Attribute attribute, BiDouble2DoubleFunction function, AttributeContainer... attributeMaps) {
        if (!this.map.containsKey(attribute)) {
            throw new NoSuchElementException("Attribute \"" + attribute.name() + "\" has no set base value.");
        }

        double f = getBase(attribute);
        for (AttributeContainer map : attributeMaps) {
            f *= function.apply(f, map.getBase(attribute));
        }
    }

    public void getModified(Attribute attribute, List<Double2DoubleFunction> functions) {
        if (!this.map.containsKey(attribute)) {
            throw new NoSuchElementException("Attribute \"" + attribute.name() + "\" has no set base value.");
        }

        double f = getBase(attribute);
        for (Double2DoubleFunction function : functions) {
            f *= function.apply(f);
        }
    }

    public void getModified(Attribute attribute, Double2DoubleFunction... functions) {
        if (!this.map.containsKey(attribute)) {
            throw new NoSuchElementException("Attribute \"" + attribute.name() + "\" has no set base value.");
        }

        double f = getBase(attribute);
        for (Double2DoubleFunction function : functions) {
            f *= function.apply(f);
        }
    }

    public ListTag<CompoundTag> save() {
        ListTag<CompoundTag> list = new ListTag<>(CompoundTag.class);
        for (Map.Entry<Attribute, Double> entry : this.map.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("name", entry.getKey().name());
            tag.putDouble("value", entry.getValue());
            list.add(tag);
        }
        return list;
    }

    public ListTag<CompoundTag> saveModifiers() {
        ListTag<CompoundTag> list = new ListTag<>(CompoundTag.class);
        for (Map.Entry<Attribute, List<AttributeModifier>> entry : this.modifierMap.entrySet()) {
            CompoundTag tag = new CompoundTag();

            ListTag<CompoundTag> modifiersTag = new ListTag<>(CompoundTag.class);
            for (AttributeModifier modifier : entry.getValue()) {
                modifiersTag.add(modifier.serialize());
            }

            tag.put("Modifiers", modifiersTag);
            tag.putString("name", entry.getKey().name());

            list.add(tag);
        }
        return list;
    }

    public ListTag<CompoundTag> loadModifiers() {
        throw new TODO("Not yet implemented"); // TODO: Implement load modifiers in attribute container.
    }

    public void load(ListTag<CompoundTag> list) {
        for (CompoundTag tag : list) {
            Attribute key = Attribute.fromName(tag.getString("name"));
            if (key == null) continue;
            double value = tag.getDouble("value");

            this.map.put(key, value);
        }
    }

    public void setAll(AttributeContainer defaultAttributes) {
        this.map.putAll(defaultAttributes.map);
    }

    public double get(Attribute attribute) {
        List<AttributeModifier> modifiers = modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        double base = getBase(attribute);
        if (modifiers.isEmpty()) {
            return base;
        }
        for (AttributeModifier modifier : modifiers) {
            AttributeModifier.Type type = modifier.type();
            final double modify = modifier.value();
            switch (type) {
                case ADD -> base += modify;
                case MULTIPLY -> base *= modify;
            }
        }
        return base;
    }

    public boolean has(Attribute attribute) {
        return map.containsKey(attribute);
    }

    public void addModifier(Attribute attribute, AttributeModifier modifier) {
        List<AttributeModifier> list = modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        list.remove(modifier);
        list.add(modifier);
    }

    public void removeModifier(Attribute attribute, UUID id) {
        List<AttributeModifier> list = modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        if (list.isEmpty()) return;
        list.removeIf(modifier -> modifier.id() == id);
    }

    public void removeModifier(Attribute attribute, AttributeModifier modifier) {
        List<AttributeModifier> list = modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        list.remove(modifier);
    }
}
