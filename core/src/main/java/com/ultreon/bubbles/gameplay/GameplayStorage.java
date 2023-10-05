package com.ultreon.bubbles.gameplay;

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
        return this.storage.getMap(modId, new MapType());
    }

    public void set(String modId, MapType storage) {
        storage.put(modId, storage);
    }

    public MapType save() {
        return this.storage;
    }
}
