package com.ultreon.bubbles.data;

import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.data.types.MapType;

public class DataManager {
    public MapType storeEntity(Entity entity) {
        var nbt = new MapType();
        nbt.put("position", this.storePosition(entity.getPos()));
        nbt.put("data", entity.save());
        return nbt;
    }

    private MapType storePosition(Vector2 pos) {
        var nbt = new MapType();
        nbt.putFloat("x", pos.x);
        nbt.putFloat("y", pos.y);
        return nbt;
    }
}
