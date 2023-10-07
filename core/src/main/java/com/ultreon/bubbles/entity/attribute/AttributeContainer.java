package com.ultreon.bubbles.entity.attribute;

import com.ultreon.bubbles.common.holders.ListDataHolder;
import com.ultreon.data.types.ListType;
import com.ultreon.data.types.MapType;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings({"DuplicatedCode", "unused"})
public class AttributeContainer implements ListDataHolder<MapType> {
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

    @Override
    public ListType<MapType> save() {
        ListType<MapType> list = new ListType<>();
        for (Map.Entry<Attribute, Double> entry : this.map.entrySet()) {
            MapType tag = new MapType();
            tag.putString("name", entry.getKey().name());
            tag.putDouble("value", entry.getValue());
            list.add(tag);
        }
        return list;
    }

    public ListType<MapType> saveModifiers() {
        ListType<MapType> list = new ListType<>();
        for (Map.Entry<Attribute, List<AttributeModifier>> entry : this.modifierMap.entrySet()) {
            MapType tag = new MapType();

            ListType<MapType> modifiersTag = new ListType<>();
            for (AttributeModifier modifier : entry.getValue()) {
                modifiersTag.add(modifier.serialize());
            }

            tag.put("Modifiers", modifiersTag);
            tag.putString("name", entry.getKey().name());

            list.add(tag);
        }
        return list;
    }

    public void loadModifiers(ListType<MapType> list) {
        for (MapType tag : list) {
            Attribute key = Attribute.fromName(tag.getString("name"));
            if (key == null) continue;
            ListType<MapType> modifiersTag = tag.getList("Modifiers");
            List<AttributeModifier> modifiers = new ArrayList<>();
            for (MapType modifierTag : modifiersTag) {
                modifiers.add(AttributeModifier.deserialize(modifierTag));
            }

            this.modifierMap.put(key, modifiers);
        }
    }

    @Override
    public void load(ListType<MapType> list) {
        for (MapType tag : list) {
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
        List<AttributeModifier> modifiers = this.modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        double base = this.getBase(attribute);
        if (modifiers.isEmpty()) {
            return base;
        }
        for (AttributeModifier modifier : modifiers) {
            AttributeModifier.Type type = modifier.type();
            final double modify = modifier.value();
            switch (type) {
                case ADD:
                    base += modify;
                    break;
                case MULTIPLY:
                    base *= modify;
                    break;
            }
        }
        return base;
    }

    public boolean has(Attribute attribute) {
        return this.map.containsKey(attribute);
    }

    public void addModifier(Attribute attribute, AttributeModifier modifier) {
        List<AttributeModifier> list = this.modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        list.remove(modifier);
        list.add(modifier);
    }

    public void removeModifier(Attribute attribute, UUID id) {
        List<AttributeModifier> list = this.modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        if (list.isEmpty()) return;
        list.removeIf(modifier -> modifier.id() == id);
    }

    public void removeModifier(Attribute attribute, AttributeModifier modifier) {
        List<AttributeModifier> list = this.modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        list.remove(modifier);
    }

    public void removeModifiers(Attribute attribute) {
        this.modifierMap.remove(attribute);
    }
}
