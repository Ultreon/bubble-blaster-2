package com.ultreon.bubbles.item;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.TagHolder;
import com.ultreon.bubbles.common.interfaces.StateHolder;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.registry.Registers;
import net.querz.nbt.tag.CompoundTag;
import org.checkerframework.checker.nullness.qual.NonNull;

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
    public @NonNull CompoundTag save() {
        CompoundTag document = new CompoundTag();
        document.put("Tag", tag);
        document.putString("type", type.id().toString());

        return document;
    }

    @Override
    public void load(CompoundTag tag) {
        this.tag = tag.getCompoundTag("Tag");
        this.type = Registers.ITEMS.get(Identifier.parse(tag.getString("type")));
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
