package com.ultreon.bubbles.data;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.vector.Vec2f;
import net.querz.nbt.tag.CompoundTag;

public class DataManager {
    public CompoundTag storeEntity(Entity entity) {
        CompoundTag nbt = new CompoundTag();
        nbt.put("position", storePosition(entity.getPos()));
        nbt.put("data", entity.save());
        return nbt;
    }

    private CompoundTag storePosition(Vec2f pos) {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("x", pos.getX());
        nbt.putFloat("y", pos.getY());
        return nbt;
    }
}
