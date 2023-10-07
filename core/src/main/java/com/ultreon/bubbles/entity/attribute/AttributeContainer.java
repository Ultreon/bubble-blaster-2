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
        @Nullable var value = this.map.get(attribute);
        if (value == null) {
            throw new NoSuchElementException("Attribute \"" + attribute.name() + "\" has no set base value.");
        }
        return value;
    }

    @Override
    public ListType<MapType> save() {
        var list = new ListType<MapType>();
        for (var entry : this.map.entrySet()) {
            var tag = new MapType();
            tag.putString("name", entry.getKey().name());
            tag.putDouble("value", entry.getValue());
            list.add(tag);
        }
        return list;
    }

    public ListType<MapType> saveModifiers() {
        var list = new ListType<MapType>();
        for (var entry : this.modifierMap.entrySet()) {
            var tag = new MapType();

            var modifiersTag = new ListType<MapType>();
            for (var modifier : entry.getValue()) {
                modifiersTag.add(modifier.serialize());
            }

            tag.put("Modifiers", modifiersTag);
            tag.putString("name", entry.getKey().name());

            list.add(tag);
        }
        return list;
    }

    public void loadModifiers(ListType<MapType> list) {
        for (var tag : list) {
            var key = Attribute.fromName(tag.getString("name"));
            if (key == null) continue;
            ListType<MapType> modifiersTag = tag.getList("Modifiers");
            List<AttributeModifier> modifiers = new ArrayList<>();
            for (var modifierTag : modifiersTag) {
                modifiers.add(AttributeModifier.deserialize(modifierTag));
            }

            this.modifierMap.put(key, modifiers);
        }
    }

    @Override
    public void load(ListType<MapType> list) {
        for (var tag : list) {
            var key = Attribute.fromName(tag.getString("name"));
            if (key == null) continue;
            var value = tag.getDouble("value");

            this.map.put(key, value);
        }
    }

    public void setAll(AttributeContainer defaultAttributes) {
        this.map.putAll(defaultAttributes.map);
    }

    public double get(Attribute attribute) {
        var modifiers = this.modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        var base = this.getBase(attribute);
        if (modifiers.isEmpty()) {
            return base;
        }
        for (var modifier : modifiers) {
            var type = modifier.type();
            final var modify = modifier.value();
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
        var list = this.modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        list.remove(modifier);
        list.add(modifier);
    }

    public void removeModifier(Attribute attribute, UUID id) {
        var list = this.modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        if (list.isEmpty()) return;
        list.removeIf(modifier -> modifier.id() == id);
    }

    public void removeModifier(Attribute attribute, AttributeModifier modifier) {
        var list = this.modifierMap.computeIfAbsent(attribute, attr -> new ArrayList<>());
        list.remove(modifier);
    }

    public void removeModifiers(Attribute attribute) {
        this.modifierMap.remove(attribute);
    }
}
