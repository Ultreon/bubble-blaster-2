package com.ultreon.bubbles.gamemode;

import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.util.ExceptionUtils;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.data.types.MapType;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.Messenger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.geom.Rectangle2D;

/**
 * @author XyperCode
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings({"unused", "deprecation"})
public class LegacyMode extends ClassicMode {
    private static final Color BACKGROUND_COLOR = Color.rgb(0x00a7a7);
    // Threads
    private Thread spawner;

    public LegacyMode() {
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
        this.hud = new LegacyHud(this);

        initializeClassic(environment, messenger);
    }

    @Override
    public void onLoad(Environment environment, GameSave save, Messenger messenger) {
        this.hud = new LegacyHud(this);
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
    public @NotNull MapType save() {
        BubbleBlaster.getLogger().warn(ExceptionUtils.getStackTrace("Deprecated call on Gamemode.save."));
        return new MapType();
    }

    @Override
    @Deprecated
    public void load(MapType tag) {
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

    public boolean renderBackground(Renderer renderer, BubbleBlaster game) {
        renderer.fill(0, 0, renderer.getWidth(), renderer.getHeight(), BACKGROUND_COLOR);
        return true;
    }

    @Override
    public void renderHUD(Renderer renderer) {
        getHud().renderHUD(renderer);
    }

    @Override
    public void renderGUI(Renderer renderer) {

    }

    public LegacyHud getHud() {
        return (LegacyHud) this.hud;
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
     * @see LegacyHud#setGameOver()
     */
    @Override
    public void onGameOver() {
//        environment.gameOver(game.player);
        game.player.delete();
        getHud().setGameOver();
//        gameOver = true;
    }

    @Override
    public @NotNull Vector2 getSpawnLocation(Entity entity, Identifier usageId, long spawnIndex, int retry) {
        return new Vector2(
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
