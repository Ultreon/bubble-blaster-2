package com.ultreon.bubbles.gamemode;

import com.ultreon.bubbles.bubble.BubbleSpawnContext;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.StateListener;
import com.ultreon.bubbles.common.interfaces.DefaultSaver;
import com.ultreon.bubbles.common.interfaces.StateHolder;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.common.random.PseudoRandom;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.init.Bubbles;
import com.ultreon.bubbles.init.Entities;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.vector.Vec2f;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import com.ultreon.commons.crash.CrashLog;
import com.ultreon.commons.lang.Messenger;
import com.ultreon.data.types.MapType;
import com.ultreon.data.types.MapType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

/**
 * GameType base class.
 * Base class for all game-types, such as {@link ClassicMode}
 *
 * @author Qboi
 * @see ClassicMode
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings({"unused", "FieldCanBeLocal", "UnusedReturnValue", "BooleanMethodIsAlwaysInverted", "RedundantThrows", "UnnecessaryLocalVariable"})
public abstract class Gamemode implements StateHolder, DefaultSaver, StateListener {
    // Types.
    protected final BubbleBlaster game = BubbleBlaster.getInstance();

    // Scene
    protected Screen screen;

    protected boolean initialized = false;
    private BubbleType defaultBubble = Bubbles.NORMAL.get();
    protected GameHud hud;
    private boolean valid;

    // Random & seeding.
    public PseudoRandom getRNG() {
        return rng;
    }

    public BigInteger getSeed() {
        return rng.getSeed();
    }

    public byte[] getSeedBytes() {
        return rng.getSeed().toByteArray();
    }

    // Randomizers
    protected PseudoRandom rng;
    protected int rngIndex = 0;
    protected Rng bubbleTypesRng;
    protected Rng bubblesXPosRng;
    protected Rng bubblesYPosRng;
    protected Rng bubblesSpeedRng;
    protected Rng bubblesRadiusRng;
    protected Rng bubblesDefenseRng;
    protected Rng bubblesAttackRng;
    protected Rng bubblesScoreRng;
    protected BubbleRandomizer bubbleRandomizer;
    protected final HashMap<Identifier, Rng> rngTypes = new HashMap<>();

    public Gamemode() {
    }


    /**
     * Initialize Randomizers.
     * Initializes the randomizers such as for bubble position, or radius.
     * Base rng defaults:
     * - Bubble Type
     * - Bubble X Position
     * - Bubble Y Position
     * - Bubble Speed
     * - Bubble Radius
     * - Bubble Defense
     * - Bubble Attack Damage
     * - Bubble Score
     *
     * @see #addRNG(String, int, int)
     */
    @Deprecated
    protected void initDefaults() {

    }

    /**
     * Add Randomizer
     * Adds a randomizer to the game type.
     *
     * @param key The key (name) to save it to.
     * @return A {@link Rng} object.
     */
    protected Rng addRNG(String key, int index, int subIndex) {
        Rng rand = new Rng(rng, index, subIndex);
        rngTypes.put(Identifier.parse(key), rand);
        return rand;
    }

    /**
     * Load Game Type.
     * Used for start the game-type.
     */
    public abstract void start();

    /**
     * Handles initialization create the environment.
     * Like in {@link ClassicMode} it's used to do things on first-time load.
     *
     * @param messenger messaging system for showing loading information when a save is getting loaded.
     * @see ClassicMode#initEnv(Environment, Messenger)
     */
    public abstract void initEnv(Environment environment, Messenger messenger);

    /**
     * Handles loading create environment.
     * Made for loading things into other classes that are aware create environment load / unload.
     *
     * @param environment environment that's loading.
     * @param save        game save to load from.
     * @param messenger   messaging system for showing loading information when a save is getting loaded.
     */
    public void onLoad(Environment environment, GameSave save, Messenger messenger) {

    }

    /**
     * Does gamemode rendering.
     */
    public abstract void render(Renderer renderer);

    /**
     * Load State from Bytearray.
     * Loads the game-type state from a bytearray.
     *
     * @param save      a bytearray create data to get the game-type from.
     * @param Messenger info transporter for showing current status to load scene or save loading scene.
     * @return the game-type loaded from the save.
     */
    public static Gamemode loadState(GameSave save, Messenger Messenger) throws IOException {
        return Gamemodes.CLASSIC.get();
    }

    /**
     * Repair a saved game.
     *
     * @param gameSave the saved game to repair.
     * @return if repair is successful.
     */
    @Deprecated
    public boolean repair(GameSave gameSave) {
        return false;
    }

    /**
     * Convert a saved game.
     *
     * @param gameSave the saved game to convert.
     * @return if conversion is successful.
     */
    @Deprecated
    public boolean convert(GameSave gameSave) {
        return false;
    }

    /**
     * Get game-type build version.
     *
     * @return the game-type version.
     */
    public abstract int getGameTypeVersion();

    /**
     * Check for missing entries in the registry to load the saved game.
     *
     * @param gameSave the saved game to check for.
     * @return a hashmap container as key the registry, and as value a list create missing resource locations create the registry.
     * @throws IOException when an I/O error occurred.
     */
    @Deprecated
    public HashMap<Registry<?>, List<Identifier>> checkRegistry(GameSave gameSave) throws IOException {
        HashMap<Registry<?>, List<Identifier>> missing = new HashMap<>();

        return missing;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public void make() {
        valid = true;
    }

    public void destroy() {
        valid = false;
    }

    /**
     * Get a Random Bubble
     * Gets a random bubble from the bubble system.
     * Uses the randoms initiated in {@link #initDefaults()}.
     *
     * @return The bubble type.
     * @see BubbleSystem#random(Rng, long, int, Environment)
     */
    @Nullable
    public BubbleType getRandomBubble(long spawnIndex) {
        return null;
    }

    public abstract GameHud getHud();

    public abstract Rectangle2D getGameBounds();

    @Nullable
    public abstract Player getPlayer();

    public abstract void onGameOver();

    @Override
    public final MapType saveDefaults() {
        return new MapType();
    }

    /**
     * Get State from the Game-type to a Bson Document
     * Dumps the game-type's state to a bson document.
     */
    @NotNull
    @Override
    public MapType save() {
        return new MapType();
    }

    /**
     * Load State from a Bson Document to the Game-type
     * Loads the game-type's state from a bson document.
     *
     * @param tag the bson document containing the game-type data.
     */
    @NotNull
    @Override
    public void load(MapType tag) {

    }

    @Nullable
    public static Gamemode getFromNbt(@NotNull MapType nbt) {
        try {
            return Registry.GAMEMODES.getValue(Identifier.parse(nbt.getString("Name")));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @NotNull
    public abstract Vec2f getSpawnLocation(Entity entity, Identifier usageId, long spawnIndex, int retry);

    public boolean doesSpawn(Entity entity) {
        return true;
    }

    public Screen getScreen() {
        return screen;
    }

    public void onQuit() {

    }

    public abstract void renderHUD(Renderer renderer);

    @SuppressWarnings("EmptyMethod")
    public abstract void renderGUI(Renderer renderer);

    public void drawBubble(Renderer renderer, double x, double y, int radius, Color... colors) {
        double i = 0f;
        for (Color color : colors) {
            if (i == 0) {
                if (colors.length >= 2) {
                    renderer.stroke(new BasicStroke(2.2f));
                } else {
                    renderer.stroke(new BasicStroke(2.0f));
                }
            } else if (i == colors.length - 1) {
                renderer.stroke(new BasicStroke(2.0f));
            } else {
                renderer.stroke(new BasicStroke(2.2f));
            }

            renderer.color(color);

            Ellipse2D ellipse = this.getEllipse(x - (float) radius / 2, y - (float) radius / 2, radius, i);
            renderer.outline(ellipse);

            i += 2f;
        }
    }

    private Ellipse2D getEllipse(double x, double y, double r, double i) {
        return new Ellipse2D.Double(x + i, y + i, r - i * 2f, r - i * 2f);
    }

    public void tick(Environment environment) {

    }

    public abstract long getEntityId(Entity entity, Environment environment, long spawnIndex, int retry);

    public BubbleRandomizer createBubbleRandomizer(Environment environment, Rng rng) {
        return new BubbleRandomizer(environment, rng);
    }

    public final BubbleType getDefaultBubble() {
        return defaultBubble;
    }

    protected final void setDefaultBubble(BubbleType defaultBubble) {
        this.defaultBubble = defaultBubble;
    }

    public void onSave(Environment environment, GameSave save, Messenger messenger) {

    }

    public void onLevelUp(Player player, int to) {

    }


    protected void initializeClassic(Environment environment, Messenger messenger) {
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
                var bubble = Bubbles.DAMAGE.get();

                Vec2f pos = new Vec2f(xRng.getNumber(0, BubbleBlaster.getInstance().getWidth(), -i - 1), yRng.getNumber(0, BubbleBlaster.getInstance().getWidth(), -i - 1));
                BubbleSpawnContext.inContext(spawnIndex, 0, () -> environment.spawn(Entities.BUBBLE.get().create(environment), pos));

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
}
