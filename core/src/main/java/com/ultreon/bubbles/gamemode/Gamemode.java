package com.ultreon.bubbles.gamemode;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.Controllable;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.spawning.SpawnInformation;
import com.ultreon.bubbles.entity.spawning.SpawnUsage;
import com.ultreon.bubbles.init.BubbleTypes;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.hud.HudType;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.world.World;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.Messenger;
import com.ultreon.libs.text.v1.TextObject;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author XyperCode
 * @see HudType
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings({"unused", "UnusedReturnValue", "BooleanMethodIsAlwaysInverted", "RedundantThrows", "UnnecessaryLocalVariable"})
public abstract class Gamemode implements Controllable {
    // Types.
    protected final BubbleBlaster game = BubbleBlaster.getInstance();

    // Scene
    protected Screen screen;

    protected boolean initialized = false;
    private BubbleType defaultBubble = BubbleTypes.NORMAL;
    private boolean active;
    private final int randomIdx = 0;
    private HudType hud;

    public long getSeed(World world) {
        return world.getSeed();
    }

    public Gamemode() {

    }

    /**
     * This method gets called when the {@linkplain World world} is loaded without any previously saved data.
     *
     * @param world the world that's getting loaded.
     * @param messenger   messaging system for showing loading information when the save is being prepared for first init.
     */
    public void onFirstInit(World world, Messenger messenger) {

    }

    /**
     * Handles loading create world.
     * Made for loading things into other classes that are aware create world load / unload.
     *
     * @param world the world that's loading.
     * @param save        game save to load from.
     * @param messenger   messaging system for showing loading information when a save is getting loaded.
     */
    public void onLoad(World world, GameSave save, Messenger messenger) {

    }

    /**
     * Load State from Bytearray.
     * Loads the game-type state from a bytearray.
     *
     * @param save      a bytearray create data to get the game-type from.
     * @param Messenger info transporter for showing current status to load scene or save loading scene.
     * @return the game-type loaded from the save.
     */
    public static Gamemode loadState(GameSave save, Messenger Messenger) throws IOException {
        return save.getInfo().getGamemode();
    }

    /**
     * Get game-type build version.
     *
     * @return the game-type version.
     */
    public int getGameTypeVersion() {
        return -1;
    }

    /**
     * Check for missing entries in the registry to load the saved game.
     *
     * @param gameSave the saved game to check for.
     * @return a hashmap container as key the registry, and as value a list create missing resource locations create the registry.
     * @throws IOException when an I/O error occurred.
     */
    @Deprecated
    public HashMap<Registries, List<Identifier>> checkRegistry(GameSave gameSave) throws IOException {
        HashMap<Registries, List<Identifier>> missing = new HashMap<>();

        return missing;
    }

    @Override
    public void begin() {
        this.hud = HudType.getCurrent();
        this.hud.begin();
        this.active = true;
    }

    public void end() {
        if (this.hud != null) {
            this.hud.end();
            this.hud = null;
        }
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }

    public abstract Rectangle getGameBounds();

    @Nullable
    public abstract Player getPlayer();

    public abstract void onGameOver();

    /**
     * Used for modifying the spawn location of an entity.
     *
     * @param entity    the entity to get the spawn location for.
     * @param pos       the position passed to the entity.
     * @param usage     the spawn usage.
     * @param random    the source of random to maybe determine the location.
     * @param retry     the amount of retries it took to spawn the entity.
     * @return the spawn location for that entity, or null to use the entity location given to the entity using {@link SpawnInformation}.
     * @see World#spawn(Entity, SpawnInformation)
     */
    public @Nullable Vector2 getSpawnPos(@NotNull Entity entity, @Nullable Vector2 pos, @NotNull SpawnUsage usage, @NotNull RandomSource random, @IntRange(from = 0) int retry) {
        return null;
    }

    public boolean doesSpawn(Entity entity) {
        return true;
    }

    @Deprecated(forRemoval = true)
    public void onQuit() {
        this.end();
    }

    public boolean renderBackground(Renderer renderer, BubbleBlaster game) {
        return false;
    }

    public void drawBubble(Renderer renderer, double x, double y, int radius, Color... colors) {
        double i = 0f;
        for (Color color : colors) {
            if (i == 0) {
                if (colors.length >= 2) {
                    renderer.setLineThickness(2.2f);
                } else {
                    renderer.setLineThickness(2.0f);
                }
            } else if (i == colors.length - 1) {
                renderer.setLineThickness(2.0f);
            } else {
                renderer.setLineThickness(2.2f);
            }

            Circle ellipse = this.getCircle(x - (float) radius / 2, y - (float) radius / 2, radius, i);
            renderer.outline(ellipse, color);

            i += 2f;
        }
    }

    private Circle getCircle(double x, double y, double r, double i) {
        return new Circle((float) (x + i), (float) (y + i), (float) (r - i * 2f));
    }

    public void tick(World world) {

    }

    public abstract long getEntityId(Entity entity, World world, long spawnIndex, int retry);

    public BubbleRandomizer createBubbleRandomizer() {
        return new BubbleRandomizer();
    }

    public final BubbleType getDefaultBubble() {
        return this.defaultBubble;
    }

    protected final void setDefaultBubble(BubbleType defaultBubble) {
        this.defaultBubble = defaultBubble;
    }

    public void onSave(World world, GameSave save, Messenger messenger) {

    }

    public void onLevelUp(Player player, int to) {

    }

    public TextObject getName() {
        return TextObject.translation(this.getTranslationId());
    }

    public String getTranslationId() {
        Identifier id = this.getId();
        return id.location() + ".gamemode." + id.path();
    }

    public Identifier getId() {
        return Objects.requireNonNull(Registries.GAMEMODES.getKey(this), "Gamemode not registered: " + this.getClass().getName());
    }

    /**
     * @return true if post-spawn was changed.
     */
    public boolean onPostSpawn() {
        return false;
    }

    /**
     *
     * @return true to cancel default spawn
     */
    public boolean preSpawn() {
        return false;
    }

    /**
     * Randomly selects a bubble. Or just gives a constant output.
     *
     * @param random the source of random to select a random bubble type.
     * @param world the world where it's going to be used.
     * @return The randomly selected bubble type, or null to use default behaviour in {@link World#getRandomBubble(RandomSource)}
     * @see BubbleSystem#random(RandomSource, World)
     */
    @Nullable
    public BubbleType randomBubble(RandomSource random, World world) {
        return null;
    }

    public boolean firstInit(Messenger messenger, int maxBubbles) {
        return false;
    }
}
