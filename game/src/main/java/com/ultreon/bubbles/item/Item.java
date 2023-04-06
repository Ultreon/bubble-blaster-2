package com.ultreon.bubbles.item;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.TagHolder;
import com.ultreon.bubbles.common.interfaces.StateHolder;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.registry.Registry;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

public final class Item implements IItemProvider, StateHolder, TagHolder {
    private ItemType type;
    private CompoundTag tag;

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
    public @NotNull CompoundTag save() {
        CompoundTag document = new CompoundTag();
        document.put("Tag", tag);
        document.putString("type", Registry.ITEMS.getKey(type).toString());

        return document;
    }

    @Override
    public void load(CompoundTag tag) {
        this.tag = tag.getCompoundTag("Tag");
        this.type = Registry.ITEMS.getValue(Identifier.parse(tag.getString("type")));
    }

    @Override
    public CompoundTag getTag() {
        return tag;
    }

    public void onEntityTick(Entity entity) {
        this.type.onEntityTick();
    }

    public void tick() {
        this.type.tick();
    }
}
