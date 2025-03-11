package dev.ultreon.bubbles.data;

import com.badlogic.gdx.math.Vector2;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.vector.Vector2D;
import dev.ultreon.ubo.types.MapType;

public class DataManager {
    public MapType storeEntity(Entity entity) {
        var nbt = new MapType();
        nbt.put("position", this.storePosition(entity.getPos()));
        nbt.put("data", entity.save());
        return nbt;
    }

    private MapType storePosition(Vector2D pos) {
        var nbt = new MapType();
        nbt.putDouble("x", pos.x);
        nbt.putDouble("y", pos.y);
        return nbt;
    }
}
