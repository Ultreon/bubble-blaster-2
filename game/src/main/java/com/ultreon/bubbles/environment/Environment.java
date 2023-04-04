package com.ultreon.bubbles.environment;

import com.ultreon.bubbles.entity.*;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.game.Constants;
import com.ultreon.bubbles.game.LoadedGame;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.bubble.BubbleSpawnContext;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.common.random.PseudoRandom;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.init.GameplayEvents;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.ValueAnimator;
import com.ultreon.bubbles.render.screen.GameOverScreen;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.util.CollectionsUtils;
import com.ultreon.bubbles.util.Util;
import com.ultreon.bubbles.vector.Vec2f;
import com.ultreon.bubbles.vector.Vec2i;
import com.ultreon.commons.lang.Messenger;
import com.ultreon.commons.time.DateTime;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.StringTag;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.IntRange;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Environment {
    private static final int RNG_INDEX_SPAWN = 0;
    private static final int RNG_INDEX_SPAWN_BUBBLE = 0;
    private static final Identifier BUBBLE_SPAWN_USAGE = new Identifier("bubble_spawn_usage");
    private final List<Entity> entities = new CopyOnWriteArrayList<>();
    private final List<Player> players = new CopyOnWriteArrayList<>();
    private Gamemode gamemode;
    private long seed;
    private GameplayEvent currentGameplayEvent;
    private Thread gameEventHandlerThread;

    // Flags.
    private volatile boolean gameOver = false;
    private boolean globalBubbleFreeze = false;
    private boolean bloodMoonActive;
    private boolean bloodMoonTriggered;

    // Enums.
    @SuppressWarnings("FieldMayBeFinal")
    private Difficulty difficulty;

    // State difficulties.
    @SuppressWarnings("FieldCanBeLocal")
    private final Map<GameplayEvent, Float> stateDifficultyModifiers = new ConcurrentHashMap<>();

    // Animations:
    private ValueAnimator bloodMoonValueAnimator = null;
    private ValueAnimator bloodMoonValueAnimator1;

    // Modifiers
    private double globalBubbleSpeedModifier = 1;
    private float stateDifficultyModifier = 1;
    private final HashSet<GameplayEvent> gameplayEventActive = new HashSet<>();
    private final Rng bloodMoonRng;
    private final GameSave gameSave;

    // Checks:
    private long nextBloodMoonCheck;

    // Values:
    private long resultScore;
    @IntRange(from = 0)
    private long ticks;

    private final Difficulty.ModifierMap modifierMap = new Difficulty.ModifierMap();

    private final BubbleRandomizer bubbleRandomizer;
    private boolean initialized;
    private Player player;

    // Locks
    private final Object gameOverLock = new Object();
    private final Object entitiesLock = new Object();

    // Game
    private static final BubbleBlaster game = BubbleBlaster.getInstance();
    private String name = "UNKNOWN WORLD";
    private int freezeTicks;
    /// Constructors.

    public Environment(GameSave save, Gamemode gamemode, int seed) {
        this(save, gamemode, (long) seed);
    }

    public Environment(GameSave save, Gamemode gamemode, long seed) {
        this.gamemode = gamemode;
        PseudoRandom random = new PseudoRandom(seed);
        this.bubbleRandomizer = this.gamemode.createBubbleRandomizer(this, new Rng(random, RNG_INDEX_SPAWN, RNG_INDEX_SPAWN_BUBBLE));
        this.seed = seed;
        this.gameSave = save;
        this.difficulty = GameSettings.instance().getDifficulty();

        this.bloodMoonRng = new Rng(random, 69, 0);
    }

    public void initSave(Messenger messenger) {
        this.gamemode.initEnv(this, messenger);
        this.player = game.player;
        this.initialized = true;
    }

    public void load(GameSave save, Messenger messenger) throws IOException {
        this.gamemode.onLoad(this, save, messenger);

        loadEnvironment(save, save.load("Environment.dat", true));

        this.initialized = true;
    }

    public void save(GameSave save, Messenger messenger) throws IOException {
        this.gamemode.onSave(this, save, messenger);

        dumpRegistries(save, messenger);
        dumpPlayers(save, messenger);
        save.dump("Environment", saveEnvironment(), true);
    }

    private void dumpPlayers(GameSave save, Messenger messenger) throws IOException {
        messenger.send("Saving players...");
        for (Player p : players) {
            dumpPlayer(save, p);
        }
    }

    private void dumpPlayer(GameSave save, Player player) throws IOException {
        CompoundTag data = player.save();
        UUID uniqueId = player.getUniqueId();
        save.dump("Players/" + uniqueId, data, true);
    }

    private void loadPlayer(GameSave save, UUID uuid) throws IOException {
        CompoundTag data = save.load("Players/" + uuid, true);
        Player player = new Player(this);
        player.load(data);
        players.add(player);
        entities.add(player);
    }

    private void dumpRegistries(GameSave save, Messenger messenger) throws IOException {
        messenger.send("Dumping registries.");
        for (Registry<?> registry : Registry.getRegistries()) {
            dumpRegistryData(save, registry);
        }
    }

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private <T> void dumpRegistryData(GameSave gameSave, Registry<T> registry) throws IOException {
        CompoundTag tag = new CompoundTag();
        ListTag<StringTag> entriesTag = new ListTag<>(StringTag.class);

        for (T type : registry.values()) {
            entriesTag.add(new StringTag(registry.getKey(type).toString()));
        }
        tag.put("Entries", entriesTag);

        gameSave.createFolders("Registries/" + registry.id().location() + "/");
        gameSave.dump("Registries/" + registry.id().location() + "/" + registry.id().path().replaceAll("/", "-"), tag);
    }

    private void loadEnvironment(GameSave save, CompoundTag tag) throws IOException {
        ListTag<CompoundTag> entitiesTag = tag.getListTag("Entities").asCompoundTagList();
        for (CompoundTag entityTag : entitiesTag) {
            this.entities.add(Entity.loadFully(this, entityTag));
        }
        this.name = tag.getString("name", "INVALID SAVE NAME");
        this.seed = tag.getLong("seed");
        long[] playerUuid = tag.getLongArray("playerUuid");
        loadPlayer(save, new UUID(playerUuid[0], playerUuid[1]));
        String gameTypeId = tag.getString("gameType", null);
        if (gameTypeId == null) {
            this.gamemode = Gamemodes.CLASSIC.get();
        } else {
            this.gamemode = Registry.GAMEMODES.getValue(Identifier.parse(gameTypeId));
        }
    }

    private CompoundTag saveEnvironment() {
        CompoundTag tag = new CompoundTag();
        ListTag<CompoundTag> entitiesTag = new ListTag<>(CompoundTag.class);
        for (Entity entity : entities) {
            entitiesTag.add(entity.save());
        }
        tag.put("Entities", entitiesTag);
        tag.putString("name", name);
        tag.putString("gameType", Registry.GAMEMODES.getKey(gamemode).toString());
        tag.putLong("seed", seed);
        return tag;
    }

    public BubbleRandomizer getBubbleRandomizer() {
        return bubbleRandomizer;
    }

    public void triggerGameOver() {
        synchronized (gameOverLock) {
            if (isAlive()) {
                setResultScore(Math.round(Objects.requireNonNull(getPlayer()).getScore()));
            }

            gameOver = true;
            gamemode.onGameOver();
            Util.getSceneManager().displayScreen(new GameOverScreen(this.getResultScore()));
        }
    }

    public float getLocalDifficulty() {
        stateDifficultyModifier = CollectionsUtils.max(new ArrayList<>(stateDifficultyModifiers.values()), 1f);

        Difficulty diff = getDifficulty();
        if (getPlayer() == null) return diff.getPlainModifier() * stateDifficultyModifier;

        return ((getPlayer().getLevel() - 1) * 5 + 1) * diff.getPlainModifier() * stateDifficultyModifier;
    }

    public float getStateDifficultyModifier() {
        return stateDifficultyModifier;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setGlobalBubbleSpeedModifier(double speedModifier) {
        this.globalBubbleSpeedModifier = speedModifier;
    }

    public double getGlobalBubbleSpeedModifier() {
        return globalBubbleFreeze ? 0 : globalBubbleSpeedModifier * Constants.BUBBLE_SPEED_MODIFIER;
    }

    public void setGlobalBubbleFreeze(boolean b) {
        this.globalBubbleFreeze = b;
    }

    public boolean isGlobalBubbleFreeze() {
        return this.globalBubbleFreeze;
    }

    public boolean isGameStateActive(GameplayEvent gameplayEvent) {
        return gameplayEventActive.contains(gameplayEvent);
    }

    public void addGameStateActive(GameplayEvent gameplayEvent) {
        gameplayEventActive.add(gameplayEvent);
    }

    public void removeGameStateActive(GameplayEvent gameplayEvent) {
        gameplayEventActive.remove(gameplayEvent);
    }

    public boolean isBloodMoonActive() {
        return bloodMoonActive;
    }

    public void tickBloodMoon() {
        LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        if (loadedGame == null) {
            return;
        }

        if (!bloodMoonTriggered) {
            if (nextBloodMoonCheck == 0) {
                nextBloodMoonCheck = System.currentTimeMillis() + 10000;
            }

            if (nextBloodMoonCheck < System.currentTimeMillis()) {
                if (bloodMoonRng.getNumber(0, 720, getTicks()) == 0) {
                    triggerBloodMoon();
                } else {
                    nextBloodMoonCheck = System.currentTimeMillis() + 10000;
                }
            }
        } else {
            if (bloodMoonValueAnimator != null) {
                setGlobalBubbleSpeedModifier(bloodMoonValueAnimator.animate());
                if (bloodMoonValueAnimator.isEnded()) {
                    GameplayEvents.BLOOD_MOON_EVENT.get().activate();
                    this.setCurrentGameEvent(GameplayEvents.BLOOD_MOON_EVENT.get());
                    bloodMoonActive = true;

                    if (loadedGame.getAmbientAudio() != null) {
                        loadedGame.getAmbientAudio().stop();
                    }
                    bloodMoonValueAnimator = null;
                    bloodMoonValueAnimator1 = new ValueAnimator(8d, 1d, 1000d);
                }
            } else if (bloodMoonValueAnimator1 != null) {
                setGlobalBubbleSpeedModifier(bloodMoonValueAnimator1.animate());
                if (bloodMoonValueAnimator1.isEnded()) {
                    bloodMoonValueAnimator1 = null;
                }
            } else {
                setGlobalBubbleSpeedModifier(1);
            }
        }
    }

    public void triggerBloodMoon() {
        if (!bloodMoonTriggered) {
            BubbleBlaster.getLogger().info("Triggered blood moon.");
            bloodMoonTriggered = true;
            bloodMoonValueAnimator = new ValueAnimator(1d, 8d, 10000d);
        } else {
            BubbleBlaster.getLogger().info("Blood moon already triggered!");
        }

        BubbleBlaster.getInstance().getRenderSettings().disableAntialiasing();
    }

    public void stopBloodMoon() {
        LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        if (loadedGame == null) {
            return;
        }

        if (bloodMoonActive) {
            bloodMoonActive = false;
            bloodMoonTriggered = false;
            GameplayEvents.BLOOD_MOON_EVENT.get().deactivate();
            loadedGame.getAmbientAudio().stop();
            currentGameplayEvent = null;
        }

        BubbleBlaster.getInstance().getRenderSettings().resetAntialiasing();
    }

    public long getResultScore() {
        return resultScore;
    }

    public void setResultScore(long resultScore) {
        this.resultScore = resultScore;
    }

    @IntRange(from = 0)
    public long getTicks() {
        return ticks;
    }

    /**
     * Get a Random Bubble
     * Gets a random bubble from the bubble system.
     *
     * @return The bubble type.
     * @see BubbleSystem#random(Rng, long, int, Environment)
     */
    @NonNull
    public BubbleType getRandomBubble(long spawnIndex) {
        var bubbleType = gamemode.getRandomBubble(spawnIndex);
        if (bubbleType != null) {
            return bubbleType;
        }

        bubbleType = BubbleSystem.random(bubbleRandomizer.getVariantRng(), spawnIndex, 0, this);

        int retries = 0;
        while (bubbleType == null) {
            bubbleType = BubbleSystem.random(bubbleRandomizer.getVariantRng(), spawnIndex, retries + 1, this);
            if (++retries == 5) {
                return gamemode.getDefaultBubble();
            }
        }

        boolean canSpawn = bubbleType.canSpawn(this);

        if (canSpawn) {
            return bubbleType;
        }
        return gamemode.getDefaultBubble();
    }

    public void attack(Entity target, double damage, EntityDamageSource damageSource) {
        if (target instanceof LivingEntity e) {
            e.damage(damage, damageSource);
        }
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setStateDifficultyModifier(GameplayEvent gameplayEvent, float modifier) {
        stateDifficultyModifiers.put(gameplayEvent, modifier);
    }

    public void removeStateDifficultyModifier(GameplayEvent gameplayEvent) {
        stateDifficultyModifiers.remove(gameplayEvent);
    }

    public Object getStateDifficultyModifier(GameplayEvent gameplayEvent) {
        return stateDifficultyModifiers.get(gameplayEvent);
    }

    /**
     * Get all entities currently spawned.
     *
     * @return all the entities.
     */
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    /**
     * Spawn entity from loading.
     *
     * @param entityData the data create the entity to spawn.
     */
    public void spawnEntityFromState(CompoundTag entityData) {
        if (!BubbleBlaster.getInstance().isOnTickingThread()) {
            BubbleBlaster.runLater(() -> loadAndSpawnEntity(entityData));
            return;
        }
        loadAndSpawnEntity(entityData);
    }

    private void loadAndSpawnEntity(CompoundTag tag) {
        String type = tag.getString("Type");
        EntityType<?> entityType = Registry.ENTITIES.getValue(Identifier.parse(type));
        Entity entity = entityType.create(this, tag);
        entity.prepareSpawn(SpawnInformation.fromLoadSpawn(tag));
        entity.load(tag);

        this.entities.add(entity);
    }

    private void gameEventHandlerThread() {
        while (BubbleBlaster.getInstance().environment == this) {
            if (currentGameplayEvent != null) {
                if (!currentGameplayEvent.isActive(DateTime.current())) {
                    currentGameplayEvent = null;
                }

                continue;
            }
            for (GameplayEvent gameplayEvent : Registry.GAMEPLAY_EVENTS.values()) {
                if (gameplayEvent.isActive(DateTime.current())) {
                    currentGameplayEvent = gameplayEvent;
                    break;
                }
            }
        }
    }

    /**
     * Naturally spawn an entity.
     *
     * @param entityType type create entity to spawn.
     */
    public void spawn(EntityType<?> entityType, SpawnInformation.SpawnReason reason, long spawnIndex, int retry) {
        BubbleBlaster.runOnMainThread(() -> {
            Entity entity = entityType.create(this);
            @NonNull Vec2f pos = gamemode.getSpawnLocation(entity, new Identifier(reason.name()), spawnIndex, retry);
            spawn(entity, pos);
        });
    }

    /**
     * Naturally spawn an entity using a specific position.
     *
     * @param entity entity to spawn.
     * @param pos    spawn location.
     */
    public void spawn(Entity entity, Vec2f pos) {
        BubbleBlaster.runOnMainThread(() -> {
            entity.prepareSpawn(SpawnInformation.fromNaturalSpawn(pos));
            entity.onSpawn(pos, this);
            synchronized (entitiesLock) {
                this.entities.add(entity);
            }
        });
    }

    public void spawn(Entity entity) {
        Vec2f pos = entity.getPos();
        entity.prepareSpawn(SpawnInformation.fromNaturalSpawn(pos));
        entity.onSpawn(pos, this);
        synchronized (entitiesLock) {
            this.entities.add(entity);
        }
    }

    /**
     * @return the game type bound to this environment.
     */
    public Gamemode getGamemode() {
        return gamemode;
    }

    /**
     * Check if the environment is initialized.
     *
     * @return true if initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Tick the environment.<br>
     * <b>DO NOT CALL, THIS IS FOR INTERNAL USE ONLY</b>
     */
    public void tick() {
        if (globalBubbleFreeze && freezeTicks-- <= 0) {
            freezeTicks = 0;
            globalBubbleFreeze = false;
        }

        if (initialized) {
            synchronized (entitiesLock) {
                this.entities.removeIf(Entity::willBeDeleted);
                for (Entity entity : this.entities) {
                    entity.tick(this);
                }
                if (entities.stream().filter(Bubble.class::isInstance).count() < GameSettings.instance().getMaxBubbles()) {
                    EntityType<? extends Bubble> entityType = Bubble.getRandomType(this, bubbleRandomizer.getVariantRng());

                    Bubble bubble = BubbleSpawnContext.inContext(ticks, 0, () -> entityType.create(this));
                    if (bubble.getBubbleType().canSpawn(this)) {
                        spawn(bubble, gamemode.getSpawnLocation(bubble, BUBBLE_SPAWN_USAGE, ticks, 0));
                    }
                }
            }

            this.ticks++;
            tickBloodMoon();
        }
    }

    public void gameOver(Player player) {
        synchronized (entitiesLock) {
            entities.remove(player);
        }
    }

    public void joinPlayer(Player player) {
        synchronized (entitiesLock) {
            entities.add(player);
        }
    }

    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> bubbleEntityClass) {
        synchronized (entitiesLock) {
            return entities.stream()
                    .filter(entity -> bubbleEntityClass.isAssignableFrom(entity.getClass()))
                    .map(bubbleEntityClass::cast).toList();
        }
    }

    public GameplayEvent getCurrentGameEvent() {
        return currentGameplayEvent;
    }

    public void start() {
        this.gameEventHandlerThread = new Thread(this::gameEventHandlerThread, "GameEventHandler");
        this.gameEventHandlerThread.start();
    }

    public void shutdown() {
        if (gameEventHandlerThread != null) {
            this.gameEventHandlerThread.interrupt();
        }
        synchronized (entitiesLock) {
            for (Entity entity : entities) {
                entity.delete();
            }
        }
        this.gamemode.onQuit();
    }

    public void setCurrentGameEvent(GameplayEvent currentGameplayEvent) {
        this.currentGameplayEvent = currentGameplayEvent;
    }

    public long getSeed() {
        return seed;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isAlive() {
        return !gameOver;
    }

    public Player getPlayer() {
        return player;
    }

    public BubbleBlaster game() {
        return game;
    }

    @SuppressWarnings("unused")
    public long getEntityId(Entity entity) {
        return ticks;
    }

    public GameSave getGameSave() {
        return gameSave;
    }

    public void prepareCreation(GameSave save) throws IOException {
        save.createFolders("Registry");
    }

    @Nullable
    public Entity getEntityAt(Vec2i pos) {
        for (Entity entity : entities) {
            if (entity.getShape().contains(new Point(pos.x, pos.y))) {
                return entity;
            }
        }

        return null;
    }

    public void onLevelUp(Player player, int to) {
        if (player == this.player) {
            this.gamemode.onLevelUp(player, to);
            this.gamemode.getHud().onLevelUp(to);
        }
    }

    @Nullable
    public Entity getNearestEntity(Vec2f pos) {
        double distance = Double.MAX_VALUE;
        Entity nearest = null;
        for (Entity entity : entities) {
            double cur = entity.distanceTo(pos);
            if (cur < distance) {
                distance = cur;
                nearest = entity;
            }
        }
        return nearest;
    }

    public Entity getNearestEntity(Vec2f pos, EntityType<?> targetType) {
        double distance = Double.MAX_VALUE;
        Entity nearest = null;
        for (Entity entity : entities) {
            if (!entity.getType().equals(targetType)) continue;
            double cur = entity.distanceTo(pos);
            if (cur < distance) {
                distance = cur;
                nearest = entity;
            }
        }
        return nearest;
    }

    public void triggerBubbleFreeze(int ticks) {
        this.freezeTicks = ticks;
        this.globalBubbleFreeze = true;
    }
}
