package dev.ultreon.bubbles.gamemode.openworld;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.gamemode.Gamemode;
import dev.ultreon.bubbles.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OpenWorldMode extends Gamemode {
    private final Map<GridPoint2, OpenWorldChunk> chunks = new ConcurrentHashMap<>();

    @Override
    public @NotNull Rectangle getGameBounds() {
        return new Rectangle(0, 2, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight() - 2);
    }

    @Override
    public @Nullable Player getPlayer() {
        return this.game.player;
    }

    @Override
    public void tick(@NotNull World world) {
        super.tick(world);

        if (this.game.player != null) {
            var pos = this.game.player.getPos();
            Set<GridPoint2> requiredChunks = new HashSet<>();
            for (var i = -8; i <= 8; i++) {
                for (var j = -8; j <= 8; j++) {
                    var chunkPos = new GridPoint2(Math.floorDiv((int) pos.x, OpenWorldChunk.SIZE) + i, Math.floorDiv((int) pos.y, OpenWorldChunk.SIZE) + j);
                    if (!this.chunks.containsKey(chunkPos)) {
                        var chunk = new OpenWorldChunk(world, this, chunkPos);
                        this.chunks.put(chunkPos, chunk);
                        chunk.spawnEntities();
                    }
                    requiredChunks.add(chunkPos);
                }
            }

            for (var chunkPos : this.chunks.keySet()) {
                if (!requiredChunks.contains(chunkPos)) {
                    var remove = this.chunks.remove(chunkPos);
                    remove.dispose();
                }
            }
        }
    }

    @Override
    public void onGameOver() {
        this.game.player.delete();
    }

    @Override
    public long getEntityId(@NotNull Entity entity, @NotNull World world, long spawnIndex, int retry) {
        return 0;
    }
}
