package com.ultreon.bubbles.save;

import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.util.EnumUtils;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.data.types.MapType;

public class GameSaveInfo {
    private final String name;
    private final long savedTime;
    private final long seed;
    private final Difficulty difficulty;
    private final Gamemode gamemode;

    public GameSaveInfo(MapType tag) {
        this.name = tag.getString("name", "null");
        this.savedTime = tag.getLong("savedTime", 0L);
        this.seed = tag.getLong("seed",  0L);
        this.difficulty = EnumUtils.byName(tag.getString("difficulty"), Difficulty.NORMAL);
        this.gamemode = Registries.GAMEMODES.getValue(Identifier.tryParse(tag.getString("gamemode", Gamemodes.NORMAL.id().toString())));
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

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public long getSeed() {
        return seed;
    }
}
