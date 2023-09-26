package com.ultreon.bubbles.gameplay;

import com.ultreon.data.types.MapType;
import net.fabricmc.loader.api.ModContainer;

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

    public MapType get(ModContainer modContainer) {
        return this.storage.getMap(modContainer.getMetadata().getId(), new MapType());
    }

    public void set(String modId, MapType storage) {
        storage.put(modId, storage);
    }

    public void set(ModContainer modContainer, MapType storage) {
        storage.put(modContainer.getMetadata().getId(), storage);
    }

    public MapType save() {
        return this.storage;
    }
}
