package dev.ultreon.bubbles.gamemode.openworld;

import com.badlogic.gdx.math.GridPoint2;
import com.google.common.base.Preconditions;
import dev.ultreon.bubbles.entity.Bubble;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.spawning.SpawnInformation;
import dev.ultreon.bubbles.entity.spawning.SpawnUsage;
import dev.ultreon.bubbles.vector.Vector2D;
import dev.ultreon.bubbles.world.World;

import java.util.stream.Stream;

public class OpenWorldChunk {
    public static final int SIZE = 128;

    public World world;
    public OpenWorldMode gamemode;
    public GridPoint2 position;

    public OpenWorldChunk(World world, OpenWorldMode gamemode, GridPoint2 position) {
        Preconditions.checkNotNull(world, "World should not be null");
        Preconditions.checkNotNull(gamemode, "Gamemode should not be null");
        Preconditions.checkNotNull(position, "Position should not be null");
        this.world = world;
        this.gamemode = gamemode;
        this.position = position;
    }

    public OpenWorldChunk(World world, OpenWorldMode gamemode, int x, int y) {
        Preconditions.checkNotNull(world, "World should not be null");
        Preconditions.checkNotNull(gamemode, "Gamemode should not be null");
        this.world = world;
        this.gamemode = gamemode;
        this.position = new GridPoint2(x, y);
    }

    public void spawnBubbles() {
        for (var i = 0; i < 10; i++) {
            var bubble = new Bubble(this.world);
            var vector2D = new Vector2D(
                    this.position.x * SIZE + this.world.game().random.nextFloat() * SIZE,
                    this.position.y * SIZE + this.world.game().random.nextFloat() * SIZE
            );
            bubble.preSpawn(SpawnInformation.naturalSpawn(vector2D, this.world.game().random, SpawnUsage.BUBBLE_SPAWN, 0, this.world));
            bubble.onSpawn(bubble.pos, this.world);

            this.world.addEntity(bubble);
        }
    }

    public void spawnEntities() {
        this.spawnBubbles();
    }

    public Stream<Entity> getEntities() {
        return this.world.getEntities().stream().filter(entity -> {
            var x = entity.pos.x;
            var y = entity.pos.y;
            return x >= this.position.x * SIZE && x < (this.position.x + 1) * SIZE
                    && y >= this.position.y * SIZE && y < (this.position.y + 1) * SIZE;
        });
    }

    public void dispose() {
        for (var entity : this.getEntities().toArray(Entity[]::new)) {
            entity.delete();
        }

        this.world = null;
        this.gamemode = null;
    }
}
