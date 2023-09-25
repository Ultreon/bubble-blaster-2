package com.ultreon.bubbles.data;

import com.ultreon.bubbles.entity.Entity;
import com.ultreon.libs.commons.v0.vector.Vec2f;
import com.ultreon.data.types.MapType;

public class DataManager {
    public MapType storeEntity(Entity entity) {
        MapType nbt = new MapType();
        nbt.put("position", storePosition(entity.getPos()));
        nbt.put("data", entity.save());
        return nbt;
    }

    private MapType storePosition(Vec2f pos) {
        MapType nbt = new MapType();
        nbt.putFloat("x", pos.getX());
        nbt.putFloat("y", pos.getY());
        return nbt;
    }
}
