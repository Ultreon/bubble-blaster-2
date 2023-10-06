package com.ultreon.bubbles.gameplay;

import com.ultreon.bubbles.GamePlatform;
import com.ultreon.data.types.MapType;

public class GameplayStorage {
    private final MapType storage;

    public GameplayStorage(MapType storage) {
        this.storage = storage;
    }

    public GameplayStorage() {
        this.storage = new MapType();
    }

    public MapType get(String modId) {
        if (GamePlatform.get().isModLoaded(modId)) return new MapType();

        MapType data = this.storage.getMap(modId, new MapType());
        this.storage.put(modId, data);
        return data;
    }

    public void set(String modId, MapType data) {
        if (GamePlatform.get().isModLoaded(modId)) return;

        this.storage.put(modId, data);
    }

    public MapType save() {
        return this.storage;
    }
}
