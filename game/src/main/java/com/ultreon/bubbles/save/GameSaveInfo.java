package com.ultreon.bubbles.save;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.registry.Registry;
import net.querz.nbt.tag.CompoundTag;

public class GameSaveInfo {
    private final String name;
    private final long savedTime;
    private final long highScore;
    private final Gamemode gamemode;
    private long seed;

    public GameSaveInfo(CompoundTag tag) {
        this.name = tag.getString("name");
        this.savedTime = tag.getLong("savedTime");
        this.highScore = tag.getLong("highScore");
        this.seed = tag.getLong("seed");
        this.gamemode = Registry.GAMEMODES.getValue(Identifier.tryParse(tag.getString("gamemode", Gamemodes.CLASSIC.id().toString())));
    }

    public String getName() {
        return name;
    }

    public long getSavedTime() {
        return savedTime;
    }

    public long getHighScore() {
        return highScore;
    }

    public Gamemode getGamemode() {
        return gamemode;
    }

    public long getSeed() {
        return seed;
    }
}
