package dev.ultreon.bubbles.save;

import dev.ultreon.bubbles.common.Difficulty;
import dev.ultreon.bubbles.gamemode.Gamemode;
import dev.ultreon.bubbles.init.Gamemodes;
import dev.ultreon.bubbles.registry.Registries;
import dev.ultreon.bubbles.util.Enums;
import dev.ultreon.ubo.types.MapType;
import dev.ultreon.libs.commons.v0.Identifier;
import dev.ultreon.libs.datetime.v0.DateTime;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class GameSaveInfo {
    private final String name;
    private final DateTime savedTime;
    private final long seed;
    private final Difficulty difficulty;
    private final Gamemode gamemode;
    private String savedTimeFormatted;

    public GameSaveInfo(MapType tag) {
        this.name = tag.getString("name", "null");
        this.savedTime = DateTime.ofEpochMilli(tag.getLong("savedTime", 0L), ZoneOffset.UTC);
        this.seed = tag.getLong("seed",  0L);
        this.difficulty = Enums.byName(tag.getString("difficulty"), Difficulty.NORMAL);
        this.gamemode = Registries.GAMEMODES.getValue(Identifier.tryParse(tag.getString("gamemode", Gamemodes.NORMAL.id().toString())));
    }

    public String getName() {
        return this.name;
    }

    public DateTime getSavedTime() {
        return this.savedTime;
    }

    @Deprecated(forRemoval = true)
    public long getHighScore() {
        return 0;
    }

    public Gamemode getGamemode() {
        return this.gamemode;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public long getSeed() {
        return this.seed;
    }

    public String getSavedTimeFormatted() {
        if (this.savedTimeFormatted != null) return this.savedTimeFormatted;
        return this.savedTimeFormatted = this.savedTime.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/LLL/yyyy HH:mm:ss"));
    }
}
