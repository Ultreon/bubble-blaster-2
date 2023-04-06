package com.ultreon.bubbles.gamemode;

import com.ultreon.bubbles.bubble.BubbleSpawnContext;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.init.Bubbles;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.util.ExceptionUtils;
import com.ultreon.bubbles.vector.Vec2f;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.commons.crash.CrashLog;
import com.ultreon.commons.lang.Messenger;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.geom.Rectangle2D;

/**
 * @author Qboi123
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings({"unused", "deprecation"})
public class ClassicMode extends Gamemode {
    // Hud and events-active flag.
    private ClassicModeHud hud;

    // Threads
    private Thread spawner;

    public ClassicMode() {
        super();
    }

    /**
     * First-time loading.
     * It initializes the bubbles by placing them at random locations, including the x-axis.
     *
     * @param environment environment that's loading.
     * @param messenger   messaging system for showing loading information when a save is getting loaded.
     */
    @Override
    public void initEnv(Environment environment, Messenger messenger) {
        int maxBubbles = GameSettings.instance().getMaxBubbles();

        try {
            this.hud = new ClassicModeHud(this);

            // Spawn bubbles
            messenger.send("Spawning bubbles...");

            BubbleRandomizer randomizer = environment.getBubbleRandomizer();
            Rng xRng = randomizer.getXRng();
            Rng yRng = randomizer.getYRng();
            long spawnIndex = -1;
            for (int i = 0; i < maxBubbles; i++) {
                BubbleType bubble = environment.getRandomBubble(spawnIndex);
                int retry = 0;
                while (!bubble.canSpawn(environment)) {
                    bubble = environment.getRandomBubble(spawnIndex);
                    if (++retry == 5) {
                        spawnIndex--;
                    }
                }

                if (bubble != Bubbles.LEVEL_UP.get()) {
                    Vec2f pos = new Vec2f(xRng.getNumber(0, BubbleBlaster.getInstance().getWidth(), -i - 1), yRng.getNumber(0, BubbleBlaster.getInstance().getWidth(), -i - 1));
                    BubbleSpawnContext.inContext(spawnIndex, retry, () -> environment.spawn(Entities.BUBBLE.get().create(environment), pos));
                }

                spawnIndex--;

                messenger.send("Spawning bubble " + i + "/" + maxBubbles);
            }

            // Spawn player
            messenger.send("Spawning player...");
            game.loadPlayEnvironment();
            environment.spawn(game.player, new Vec2f(game.getScaledWidth() / 4f, BubbleBlaster.getInstance().getHeight() / 2f));
        } catch (Exception e) {
            CrashLog crashLog = new CrashLog("Could not initialize classic game type.", e);

            BubbleBlaster.crash(crashLog.createCrash());
        }

        this.make();
        this.initialized = true;
    }

    @Override
    public void onLoad(Environment environment, GameSave save, Messenger messenger) {
        this.hud = new ClassicModeHud(this);
        this.initialized = true;
    }

    /**
     * Load Game Type.
     * Used for initializing the game.
     */
    @Override
    public void start() {
//        spawner.start();
    }

    @SuppressWarnings("EmptyMethod")
    public void spawnerThread() {

    }

    @Override
    @Deprecated
    public void render(Renderer renderer) {

    }

    @Override
    @Deprecated
    public @NotNull CompoundTag save() {
        BubbleBlaster.getLogger().warn(ExceptionUtils.getStackTrace("Deprecated call on Gamemode.save."));
        return new CompoundTag();
    }

    @Override
    @Deprecated
    public void load(CompoundTag tag) {
        BubbleBlaster.getLogger().warn(ExceptionUtils.getStackTrace("Deprecated call on Gamemode.load."));
    }

    @Override
    @Deprecated
    public boolean repair(GameSave gameSave) {
        BubbleBlaster.getLogger().warn(ExceptionUtils.getStackTrace("Deprecated call on Gamemode.repair."));
        return false;
    }

    @Override
    @Deprecated
    public boolean convert(GameSave gameSave) {
        BubbleBlaster.getLogger().warn(ExceptionUtils.getStackTrace("Deprecated call on Gamemode.convert."));
        return false;
    }

    @Override
    public int getGameTypeVersion() {
        return 0;
    }

    @Override
    public void renderHUD(Renderer renderer) {
        hud.renderHUD(renderer);
    }

    @Override
    public void renderGUI(Renderer renderer) {

    }

    public ClassicModeHud getHud() {
        return this.hud;
    }

    @Override
    public Rectangle2D getGameBounds() {
        return new Rectangle2D.Double(0d, 70d, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight() - 70d);
    }

    @Override
    public Player getPlayer() {
        return this.game.player;
    }

    /**
     * Trigger Game Over
     * Deletes player and set game over flag in ClassicHUD. Showing Game Over message.
     *
     * @see ClassicModeHud#setGameOver()
     */
    @Override
    public void onGameOver() {
//        environment.gameOver(game.player);
        game.player.delete();
        hud.setGameOver();
//        gameOver = true;
    }

    @Override
    public @NotNull Vec2f getSpawnLocation(Entity entity, Identifier usageId, long spawnIndex, int retry) {
        return new Vec2f(
                (int) getGameBounds().getMaxX() + entity.getBounds().width,
                (int) entity.getYRng().getNumber(getGameBounds().getMinY() - entity.getBounds().height, getGameBounds().getMaxY() + entity.getBounds().height, usageId.toString().toCharArray(), spawnIndex, retry)
        );
    }

    @Override
    public void onQuit() {

    }

    public Thread getSpawner() {
        return spawner;
    }

    @Override
    public long getEntityId(Entity entity, Environment environment, long spawnIndex, int retry) {
        return spawnIndex;
    }
}
