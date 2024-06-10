package dev.ultreon.bubbles.gamemode;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import dev.ultreon.bubbles.BubbleBlaster;
import dev.ultreon.bubbles.entity.Coin;
import dev.ultreon.bubbles.entity.Entity;
import dev.ultreon.bubbles.entity.player.Player;
import dev.ultreon.bubbles.entity.spawning.SpawnInformation;
import dev.ultreon.bubbles.entity.spawning.SpawnUsage;
import dev.ultreon.bubbles.random.JavaRandom;
import dev.ultreon.bubbles.random.RandomSource;
import dev.ultreon.bubbles.render.gui.hud.ModernHud;
import dev.ultreon.bubbles.save.GameSave;
import dev.ultreon.bubbles.world.World;
import dev.ultreon.bubbles.util.annotation.MethodsReturnNonnullByDefault;
import dev.ultreon.libs.commons.v0.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author XyperCode
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NormalMode extends Gamemode {
    private final JavaRandom random = new JavaRandom();
    private int nextCoin = this.random.nextInt(1000, 2000);

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
    public void tick(World world) {
        super.tick(world);

        if (this.nextCoin <= 0) {
            Coin coin = new Coin(world);
            Vector2 spawnPos = this.getSpawnPos(coin, null, SpawnUsage.SPAWN, this.random, 0);
            coin.setPos(spawnPos);
            world.spawn(coin, SpawnInformation.naturalSpawn(spawnPos, this.random.nextRandom(), SpawnUsage.SPAWN, 0, world));
            this.nextCoin = this.random.nextInt(1000, 2000);
        }

        this.nextCoin--;
    }

    @Override
    public Player getPlayer() {
        return this.game.player;
    }

    /**
     * Trigger Game Over
     * Deletes player and set the "game over" flag in ClassicHUD.
     * Showing the "Game Over" message.
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

        var spawnRng = random.nextRandom(usage);

        var bounds = this.getGameBounds();
        var radius = entity.radius();
        var x = usage == SpawnUsage.INIT_SPAWN ? spawnRng.nextFloat(bounds.x, bounds.x + bounds.width) : bounds.x + bounds.width + radius;
        var y = spawnRng.nextFloat(bounds.y - radius, bounds.y + bounds.height + radius);
        return new Vector2(x, y);
    }

    @Override
    public long getEntityId(Entity entity, World world, long spawnIndex, int retry) {
        return spawnIndex;
    }
}
