package com.ultreon.bubbles.environment;

import com.badlogic.gdx.math.Vector2;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.CrashFiller;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.bubble.BubbleSpawnContext;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.common.gamestate.GameplayContext;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.common.random.PseudoRandom;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.data.DataKeys;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.LivingEntity;
import com.ultreon.bubbles.entity.SpawnInformation;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.gameplay.GameplayStorage;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.init.GameplayEvents;
import com.ultreon.bubbles.notification.Notification;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.ValueAnimator;
import com.ultreon.bubbles.render.gui.screen.GameOverScreen;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.util.CollectionsUtils;
import com.ultreon.bubbles.util.RngUtils;
import com.ultreon.data.types.ListType;
import com.ultreon.data.types.MapType;
import com.ultreon.data.types.StringType;
import com.ultreon.libs.commons.v0.DummyMessenger;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.Messenger;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import com.ultreon.libs.crash.v0.CrashCategory;
import com.ultreon.libs.crash.v0.CrashLog;
import com.ultreon.libs.registries.v0.Registry;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public final class Environment implements CrashFiller {
    private static final int RNG_INDEX_SPAWN = 0;
    private static final int RNG_INDEX_SPAWN_BUBBLE = 0;
    private static final Identifier BUBBLE_SPAWN_USAGE = new Identifier("bubble_spawn_usage");
    private final List<Entity> entities = new CopyOnWriteArrayList<>();
    private final List<Player> players = new CopyOnWriteArrayList<>();
    private final Gamemode gamemode;
    private final long seed;
    private GameplayEvent currentGameplayEvent;

    // Flags.
    private volatile boolean gameOver = false;
    private boolean globalBubbleFreeze = false;
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
    private float globalBubbleSpeedModifier = 1;
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
    private final ReentrantLock gameOverLock = new ReentrantLock();
    private final ReentrantLock entitiesLock = new ReentrantLock();

    // Game
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private String name = "UNKNOWN WORLD";
    private int freezeTicks;
    boolean shuttingDown;
    private GameplayStorage gameplayStorage = new GameplayStorage();
    public final ReentrantLock saveLock = new ReentrantLock(true);

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
        this.name = save.getInfo().getName();

        loadEnvironment(save.load("environment", true));

        this.gameplayStorage = new GameplayStorage(save.load("gameplay"));

        this.initialized = true;
    }

    public void save() {
        try {
            save(gameSave, new DummyMessenger());
        } catch (IOException e) {
            BubbleBlaster.getLogger().error("Error occurred when saving the game: ", e);
        }
    }

    public boolean save(GameSave save, Messenger messenger) throws IOException {
        if (this.saveLock.tryLock()) return false;
        this.game.notifications.notify(new Notification("Saving", "The game is being saved...", "Auto Save Feature"));

        // Gamemode implementation for saving data.
        this.gamemode.onSave(this, save, messenger);

        // Save environment data.
        dumpRegistries(save, messenger);
        dumpPlayers(save, messenger);
        save.dump("environment", saveEnvironment(), true);
        save.dump("info", saveInfo(), true);
        save.dump("gameplay", this.gameplayStorage.save());

        this.saveLock.unlock();
        this.game.notifications.notify(new Notification("Saved", "The game has been saved!", "Auto Save Feature"));
        return true;
    }

    private void dumpPlayers(GameSave save, Messenger messenger) throws IOException {
        messenger.send("Saving players...");
        for (Player p : this.players) {
            dumpPlayer(save, p);
        }
    }

    private void dumpPlayer(GameSave save, Player player) throws IOException {
        MapType data = player.save();
        UUID uniqueId = player.getUniqueId();
        save.dump("players/" + uniqueId, data, true);
    }

    private void loadPlayer(GameSave save, UUID uuid) throws IOException {
        MapType data = save.load("Players/" + uuid, true);
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
        MapType tag = new MapType();
        ListType<StringType> entriesTag = new ListType<>();

        for (T type : registry.values()) {
            entriesTag.add(new StringType(Objects.requireNonNull(registry.getKey(type)).toString()));
        }
        tag.put("Entries", entriesTag);

        gameSave.createFolders("registries/" + registry.id().location() + "/");
        gameSave.dump("registries/" + registry.id().location() + "/" + registry.id().path().replaceAll("/", "-"), tag);
    }

    private void loadEnvironment(MapType tag) {
        ListType<MapType> entitiesTag = tag.getList("Entities");
        for (MapType entityTag : entitiesTag) {
            Entity entity = Entity.loadFully(this, entityTag);
            this.entities.add(entity);
        }
        this.player = (Player) Entity.loadFully(this, tag.getMap("Player"));
        this.gameOver = tag.getBoolean("gameOver");
    }

    private MapType saveEnvironment() {
        MapType tag = new MapType();
        ListType<MapType> entitiesTag = new ListType<>();
        for (Entity entity : entities) {
            entitiesTag.add(entity.save());
        }
        tag.put("Entities", entitiesTag);
        tag.put("Player", player.save());
        tag.putUUID("playerUuid", player.getUniqueId());
        tag.putBoolean("gameOver", isGameOver());
        return tag;
    }

    private MapType saveInfo() {
        MapType tag = new MapType();
        tag.putString("name", this.name);
        tag.putLong("seed", this.seed);
        tag.putLong("savedTime", LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC));
        Identifier key = Registries.GAMEMODES.getKey(this.gamemode);
        tag.putString("gamemode", (key == null ? Gamemodes.MODERN.id() : key).toString());
        return tag;
    }

    public BubbleRandomizer getBubbleRandomizer() {
        return bubbleRandomizer;
    }

    @CanIgnoreReturnValue
    public boolean triggerGameOver() {
        if (!gameOverLock.tryLock()) return false;

        if (this.isAlive()) {
            this.setResultScore(Math.round(Objects.requireNonNull(getPlayer()).getScore()));
        }

        this.gameOver = true;
        this.gamemode.onGameOver();
        this.game.showScreen(new GameOverScreen(this.getResultScore()));
        this.save();
        return true;
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

    public void setGlobalBubbleSpeedModifier(float speedModifier) {
        this.globalBubbleSpeedModifier = speedModifier;
    }

    public float getGlobalBubbleSpeedModifier() {
        return globalBubbleFreeze ? 0 : globalBubbleSpeedModifier;
    }

    public void setGlobalBubbleFreeze(boolean b) {
        this.globalBubbleFreeze = b;
    }

    public boolean isGlobalBubbleFreeze() {
        return this.globalBubbleFreeze;
    }

    public boolean isGameplayEventActive(GameplayEvent gameplayEvent) {
        return gameplayEventActive.contains(gameplayEvent);
    }

    private GameplayContext createGameplayContext() {
        return new GameplayContext(Instant.now(), this, this.gamemode, this.gameplayStorage);
    }

    @Deprecated
    public void addGameStateActive(GameplayEvent gameplayEvent) {
        gameplayEventActive.add(gameplayEvent);
    }

    @Deprecated
    public void removeGameStateActive(GameplayEvent gameplayEvent) {
        gameplayEventActive.remove(gameplayEvent);
    }

    public boolean isBloodMoonActive() {
        return this.gameplayStorage.get(BubbleBlaster.NAMESPACE).getBoolean(DataKeys.BLOOD_MOON_ACTIVE, false);
    }

    public GameplayStorage getGameplayStorage() {
        return gameplayStorage;
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
                setGlobalBubbleSpeedModifier((float) bloodMoonValueAnimator.animate());
                if (bloodMoonValueAnimator.isEnded()) {
                    GameplayEvents.BLOOD_MOON_EVENT.activate();
                    this.setCurrentGameEvent(GameplayEvents.BLOOD_MOON_EVENT);
                    gameplayStorage.get(BubbleBlaster.NAMESPACE).putBoolean(DataKeys.BLOOD_MOON_ACTIVE, true);

                    if (loadedGame.getAmbientAudio() != null) {
                        loadedGame.getAmbientAudio().stop();
                    }
                    bloodMoonValueAnimator = null;
                    bloodMoonValueAnimator1 = new ValueAnimator(8d, 1d, 1000d);
                }
            } else if (bloodMoonValueAnimator1 != null) {
                setGlobalBubbleSpeedModifier((float) bloodMoonValueAnimator1.animate());
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

        if (this.isBloodMoonActive()) {
            gameplayStorage.get(BubbleBlaster.NAMESPACE).putBoolean(DataKeys.BLOOD_MOON_ACTIVE, false);
            bloodMoonTriggered = false;
            GameplayEvents.BLOOD_MOON_EVENT.deactivate();
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
    @NotNull
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
    public void spawnEntityFromState(MapType entityData) {
        if (!BubbleBlaster.isOnTickingThread()) {
            BubbleBlaster.invokeTick(() -> loadAndSpawnEntity(entityData));
            return;
        }
        loadAndSpawnEntity(entityData);
    }

    private void loadAndSpawnEntity(MapType tag) {
        String type = tag.getString("Type");
        EntityType<?> entityType = Registries.ENTITIES.getValue(Identifier.parse(type));
        Entity entity = entityType.create(this, tag);
        entity.prepareSpawn(SpawnInformation.fromLoadSpawn(tag));
        entity.load(tag);

        this.entities.add(entity);
    }

    /**
     * Runs the function only every tick given.<br>
     * Note: this isn't reliable off the ticking thread.
     *
     * @param ticks the #th tick to run the function on.
     * @param func the function to run.
     */
    public void onlyTickEvery(long ticks, Runnable func) {
        if (this.ticks % ticks == 0L) {
            func.run();
        }
    }

    /**
     * Naturally spawn an entity.
     *
     * @param entityType type create entity to spawn.
     */
    public void spawn(EntityType<?> entityType, SpawnInformation.SpawnReason reason, long spawnIndex, int retry) {
        BubbleBlaster.invokeTick(() -> {
            Entity entity = entityType.create(this);
            @NotNull Vector2 pos = gamemode.getSpawnLocation(entity, new Identifier(reason.name()), spawnIndex, retry);
            spawn(entity, pos);
        });
    }

    /**
     * Naturally spawn an entity using a specific position.
     *
     * @param entity entity to spawn.
     * @param pos    spawn location.
     */
    public void spawn(Entity entity, Vector2 pos) {
        BubbleBlaster.invokeTick(() -> {
            entity.prepareSpawn(SpawnInformation.fromNaturalSpawn(pos));
            entity.onSpawn(pos, this);
            this.entitiesLock.lock();
            this.entities.add(entity);
            this.entitiesLock.unlock();
        });
    }

    public void spawn(Entity entity) {
        Vector2 pos = entity.getPos();
        entity.prepareSpawn(SpawnInformation.fromNaturalSpawn(pos));
        entity.onSpawn(pos, this);
        this.entitiesLock.lock();
        this.entities.add(entity);
        this.entitiesLock.unlock();
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
     * Tick the environment.<br>\
     */
    @ApiStatus.Internal
    public void tick() {
        if (this.globalBubbleFreeze && this.freezeTicks-- <= 0) {
            this.freezeTicks = 0;
            this.globalBubbleFreeze = false;
        }

        if (this.initialized) {
            entities: {
                if (!this.entitiesLock.tryLock()) break entities;

                // Tick entities
                this.entities.removeIf(Entity::willBeDeleted);
                for (Entity entity : this.entities) {
                    entity.tick(this);
                }

                this.entitiesLock.unlock();
            }

            tickSpawning();
            tickBloodMoon();

            // Tick gameplay events
            gamePlayEvent: {
                if (this.currentGameplayEvent != null) {
                    if (!this.currentGameplayEvent.shouldContinue(this.createGameplayContext())) {
                        this.currentGameplayEvent = null;
                    }

                    break gamePlayEvent;
                }

                this.onlyTickEvery(5, () -> {
                    List<GameplayEvent> choices = RngUtils.choices(Registries.GAMEPLAY_EVENTS.values(), 3);
                    for (GameplayEvent gameplayEvent : choices) {
                        if (gameplayEvent.shouldActivate(this.createGameplayContext())) {
                            this.currentGameplayEvent = gameplayEvent;
                            break;
                        }
                    }
                });
            }

            // Advance ticks
            this.ticks++;
        }
    }

    private void tickSpawning() {
        if (entities.stream().filter(Bubble.class::isInstance).count() < GameSettings.instance().maxBubbles) {
            EntityType<? extends Bubble> entityType = Bubble.getRandomType(this, bubbleRandomizer.getVariantRng());

            Bubble bubble = BubbleSpawnContext.inContext(ticks, 0, () -> entityType.create(this));
            if (bubble.getBubbleType().canSpawn(this)) {
                spawn(bubble, gamemode.getSpawnLocation(bubble, BUBBLE_SPAWN_USAGE, ticks, 0));
            }
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

    }

    public void shutdown() {
        this.shuttingDown = true;

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
        save.createFolders("registries");
    }

    @Nullable
    public Entity getEntityAt(Vec2i pos) {
        for (Entity entity : entities) {
            if (entity.getShape().contains(new Vector2(pos.x, pos.y))) {
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
    public Entity getNearestEntity(Vector2 pos) {
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

    public Entity getNearestEntity(Vector2 pos, EntityType<?> targetType) {
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

    public boolean isSaving() {
        return this.saveLock.isLocked();
    }

    public void dispose() {
        this.gamemode.end();
        this.entities.clear();
    }

    public void annihilate() {
        this.gamemode.end();
        this.entities.clear();
        this.players.clear();
        this.player = null;
    }

    @Override
    public void fillInCrash(CrashLog crashLog) {
        var category = new CrashCategory("Environment", new RuntimeException());
        category.add("Players", players);
        category.add("Entities", entities);
        category.add("Game Save", gameSave);
        category.add("Cur. Gameplay Event", currentGameplayEvent);
        crashLog.addCategory(category);
    }
}
