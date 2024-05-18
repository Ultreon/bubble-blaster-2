package dev.ultreon.bubbles.common.gamestate;

import dev.ultreon.bubbles.gamemode.Gamemode;
import dev.ultreon.bubbles.gameplay.GameplayStorage;
import dev.ultreon.bubbles.world.World;

import java.time.Instant;
import java.util.Objects;

public final class GameplayContext {
    private final Instant time;
    private final World world;
    private final Gamemode gamemode;
    private final GameplayStorage gameplayStorage;

    public GameplayContext(Instant time, World world, Gamemode gamemode, GameplayStorage gameplayStorage) {
        this.time = time;
        this.world = world;
        this.gamemode = gamemode;
        this.gameplayStorage = gameplayStorage;
    }

    public Instant time() {
        return this.time;
    }

    public World world() {
        return this.world;
    }

    public Gamemode gamemode() {
        return this.gamemode;
    }

    public GameplayStorage gameplayStorage() {
        return this.gameplayStorage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GameplayContext) obj;
        return Objects.equals(this.time, that.time) &&
                Objects.equals(this.world, that.world) &&
                Objects.equals(this.gamemode, that.gamemode) &&
                Objects.equals(this.gameplayStorage, that.gameplayStorage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.time, this.world, this.gamemode, this.gameplayStorage);
    }

    @Override
    public String toString() {
        return "GameplayContext[" +
                "time=" + this.time + ", " +
                "world=" + this.world + ", " +
                "gamemode=" + this.gamemode + ", " +
                "gameplayStorage=" + this.gameplayStorage + ']';
    }


}
