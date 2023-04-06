package com.ultreon.bubbles.gamemode;

import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.init.Bubbles;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.util.ExceptionUtils;
import com.ultreon.bubbles.vector.Vec2f;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.commons.lang.Messenger;
import net.querz.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.geom.Rectangle2D;

/**
 * @author Qboi123
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings({"unused", "deprecation"})
public class ImpossibleMode extends Gamemode {
    // Threads
    private Thread spawner;
    private ClassicModeHud classicHud;

    public ImpossibleMode() {
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
        initializeClassic(environment, messenger);
    }

    @Override
    public @Nullable BubbleType getRandomBubble(long spawnIndex) {
        return Bubbles.DAMAGE.get();
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
        ((ClassicModeHud)hud).renderHUD(renderer);
    }

    @Override
    public void renderGUI(Renderer renderer) {

    }

    public ClassicModeHud getHud() {
        return (ClassicModeHud) this.hud;
    }

    @Override
    public Rectangle2D getGameBounds() {
        return new Rectangle2D.Double(70d, 0d, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight() - 70d);
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
        classicHud.setGameOver();
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
        this.hud = null;
    }

    public Thread getSpawner() {
        return spawner;
    }

    @Override
    public long getEntityId(Entity entity, Environment environment, long spawnIndex, int retry) {
        return spawnIndex;
    }
}
