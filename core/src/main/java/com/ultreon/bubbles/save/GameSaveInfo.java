package com.ultreon.bubbles.save;

import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.data.types.MapType;

public class GameSaveInfo {
    private final String name;
    private final long savedTime;
    private final Gamemode gamemode;
    private final long seed;

    public GameSaveInfo(MapType tag) {
        this.name = tag.getString("name");
        this.savedTime = tag.getLong("savedTime");
        this.seed = tag.getLong("seed");
        this.gamemode = Registries.GAMEMODES.getValue(Identifier.tryParse(tag.getString("gamemode", Gamemodes.MODERN.id().toString())));
    }

    public String getName() {
        return name;
    }

    public long getSavedTime() {
        return savedTime;
    }

    @Deprecated(forRemoval = true)
    public long getHighScore() {
        return 0;
    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public long getSeed() {
        return seed;
    }
}
