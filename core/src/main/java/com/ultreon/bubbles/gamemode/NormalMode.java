package com.ultreon.bubbles.gamemode;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.entity.AbstractBubbleEntity;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.spawning.SpawnUsage;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.render.gui.hud.ModernHud;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.libs.commons.v0.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author XyperCode
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NormalMode extends Gamemode {
    // Threads
    private Thread spawner;

    public NormalMode() {
        super();
    }

    @Override
    public void onLoad(World world, GameSave save, Messenger messenger) {
        this.initialized = true;
    }

    @Override
    public int getGameTypeVersion() {
        return 0;
    }

    @Override
    public Rectangle getGameBounds() {
        return new Rectangle(0, 2, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight() - 2);
    }

    @Override
    public Player getPlayer() {
        return this.game.player;
    }

    /**
     * Trigger Game Over
     * Deletes player and set game over flag in ClassicHUD. Showing Game Over message.
     *
     * @see ModernHud#gameOver()
     */
    @Override
    public void onGameOver() {
        this.game.player.delete();
    }

    @Override
    public @NotNull Vector2 getSpawnPos(Entity entity, @Nullable Vector2 pos, SpawnUsage usage, RandomSource random, int retry) {
        if (pos != null) return pos;

        RandomSource spawnRng = random.nextRandom(usage);

        Rectangle bounds = this.getGameBounds();
        if (entity instanceof AbstractBubbleEntity) {
            AbstractBubbleEntity bubble = (AbstractBubbleEntity) entity;
            float radius = bubble.getRadius();
            float x = usage == SpawnUsage.BUBBLE_INIT_SPAWN ? spawnRng.nextFloat(bounds.x, bounds.x + bounds.width) : bounds.x + bounds.width + radius;
            float y = spawnRng.nextFloat(bounds.y - radius, bounds.y + bounds.height + radius);
            return new Vector2(x, y);
        }

        float x = spawnRng.nextFloat(bounds.x, bounds.x + bounds.width);
        float y = spawnRng.nextFloat(bounds.y, bounds.y + bounds.height);
        return new Vector2(x, y);
    }

    public Thread getSpawner() {
        return this.spawner;
    }

    @Override
    public long getEntityId(Entity entity, World world, long spawnIndex, int retry) {
        return spawnIndex;
    }
}
