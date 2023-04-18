package com.ultreon.bubbles.item;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.bubbles.common.TagHolder;
import com.ultreon.bubbles.common.interfaces.StateHolder;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.data.types.MapType;
import org.jetbrains.annotations.NotNull;

public final class Item implements IItemProvider, StateHolder, TagHolder {
    private ItemType type;
    private MapType tag;

    @Override
    public ItemType getItem() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public ItemType getType() {
        return type;
    }

    @Override
    public @NotNull MapType save() {
        MapType document = new MapType();
        document.put("Tag", tag);
        document.putString("type", Registries.ITEMS.getKey(type).toString());

        return document;
    }

    @Override
    public void load(MapType tag) {
        this.tag = tag.getMap("Tag");
        this.type = Registries.ITEMS.getValue(Identifier.parse(tag.getString("type")));
    }

    @Override
    public MapType getTag() {
        return tag;
    }

    public void onEntityTick(Entity entity) {
        this.type.onEntityTick();
    }

    public void tick() {
        this.type.tick();
    }
}
