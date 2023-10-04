package com.ultreon.bubbles.world;

import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.CrashFiller;
import com.ultreon.bubbles.LoadedGame;
import com.ultreon.bubbles.audio.MusicEvent;
import com.ultreon.bubbles.bubble.BubbleSpawnContext;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.common.gamestate.GameplayContext;
import com.ultreon.bubbles.common.gamestate.GameplayEvent;
import com.ultreon.bubbles.common.random.BubbleRandomizer;
import com.ultreon.bubbles.common.random.GameRandom;
import com.ultreon.bubbles.common.random.Rng;
import com.ultreon.bubbles.data.DataKeys;
import com.ultreon.bubbles.entity.Bubble;
import com.ultreon.bubbles.entity.Entity;
import com.ultreon.bubbles.entity.LivingEntity;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.entity.damage.EntityDamageSource;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.entity.spawning.SpawnInformation;
import com.ultreon.bubbles.entity.spawning.SpawnUsage;
import com.ultreon.bubbles.entity.types.EntityType;
import com.ultreon.bubbles.event.v1.EntityEvents;
import com.ultreon.bubbles.event.v1.PlayerEvents;
import com.ultreon.bubbles.event.v1.TickEvents;
import com.ultreon.bubbles.event.v1.WorldEvents;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.gameplay.GameplayStorage;
import com.ultreon.bubbles.init.Gamemodes;
import com.ultreon.bubbles.init.GameplayEvents;
import com.ultreon.bubbles.notification.Notification;
import com.ultreon.bubbles.random.JavaRandom;
import com.ultreon.bubbles.random.RandomSource;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.ValueAnimator;
import com.ultreon.bubbles.render.gui.hud.HudType;
import com.ultreon.bubbles.render.gui.screen.GameOverScreen;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.util.CollectionsUtils;
import com.ultreon.bubbles.util.RngUtils;
import com.ultreon.data.types.ListType;
import com.ultreon.data.types.LongType;
import com.ultreon.data.types.MapType;
import com.ultreon.data.types.StringType;
import com.ultreon.libs.commons.v0.DummyMessenger;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.Messenger;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import com.ultreon.libs.crash.v0.CrashCategory;
import com.ultreon.libs.crash.v0.CrashLog;
import com.ultreon.libs.registries.v0.Registry;
import com.ultreon.libs.text.v1.TextObject;
import it.unimi.dsi.fastutil.longs.Long2ReferenceArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMaps;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.dataflow.qual.Pure;
import org.jetbrains.annotations.ApiStatus;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import static com.ultreon.bubbles.BubbleBlaster.TPS;

public final class World implements CrashFiller, Closeable {
    private static final long MINIMUM_ENTITY_ID = 0;
    private final List<Player> players = new CopyOnWriteArrayList<>();
    private Gamemode gamemode;
    private final long seed;
    private GameplayEvent currentGameplayEvent;

    // Flags.
    private volatile boolean gameOver = false;
    private boolean bubblesFrozen = false;
    private boolean bloodMoonTriggered;

    // Enums.
    private Difficulty difficulty;

    // State difficulties.
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

    private final Difficulty.ModifierMap difficultyModifiers = new Difficulty.ModifierMap();

    private final BubbleRandomizer bubbleRng;
    private boolean initialized;
    private Player player;

    // Locks
    private final ReentrantLock gameOverLock = new ReentrantLock();
    private final ReentrantLock entitiesLock = new ReentrantLock();
    private final Long2ReferenceMap<Entity> entitiesById = Long2ReferenceMaps.synchronize(new Long2ReferenceArrayMap<>());
    private final Map<UUID, Entity> entitiesByUuid = new ConcurrentHashMap<>();

    // Game
    private final BubbleBlaster game = BubbleBlaster.getInstance();
    private String name = "UNKNOWN WORLD";
    private int bubblesFrozenTicks;
    boolean shuttingDown;
    private GameplayStorage gameplayStorage = new GameplayStorage();
    public final ReentrantLock saveLock = new ReentrantLock(true);
    private long entitySeedIdx;
    private Instant gameOverTime = null;
    private long nextEntityId = 0;
    private final MusicEvent music = null;

    /// Constructors.
    public World(GameSave save, Gamemode gamemode, Difficulty difficulty, int seed) {
        this(save, gamemode, difficulty, (long) seed);
    }

    public World(GameSave save, Gamemode gamemode, Difficulty difficulty, long seed) {
        this.gamemode = gamemode;
        this.difficulty = difficulty;
        GameRandom random = new GameRandom(seed);
        this.bubbleRng = this.gamemode.createBubbleRandomizer();
        this.seed = seed;
        this.gameSave = save;

        this.bloodMoonRng = new Rng(random, 69, 0);
    }

    public void firstInit(Messenger messenger) {
        WorldEvents.WORLD_STARTING.factory().onWorldStarting(this);

        int maxBubbles = GameSettings.instance().maxBubbles;

        try {
            // Spawn bubbles
            messenger.send("Spawning bubbles...");

            if (!this.gamemode.preSpawn()) {
                this.firstInit(messenger, maxBubbles);
            }

            this.gamemode.onPostSpawn();


            // Spawn player
            messenger.send("Spawning player...");
            Vector2 pos = new Vector2(this.game.getScaledWidth() / 4f, BubbleBlaster.getInstance().getHeight() / 2f);
            this.game.laodPlayerIntoWorld();
            this.spawn(this.game.player, SpawnInformation.playerSpawn(pos, this, new JavaRandom()));
        } catch (Exception e) {
            CrashLog crashLog = new CrashLog("Could not initialize world.", e);

            BubbleBlaster.crash(crashLog.createCrash());
            return;
        }

        this.gamemode.onFirstInit(this, messenger);
        this.player = this.game.player;
        this.initialized = true;

        GameplayEvents.BLOOD_MOON_EVENT.resetNext();

        WorldEvents.WORLD_STARTED.factory().onWorldStarted(this);
    }

    private void firstInit(Messenger messenger, int maxBubbles) {
        if (this.gamemode.firstInit(messenger, maxBubbles)) return;

        for (int i = 0; i < maxBubbles; i++) {
            int retry = 0;


            long idx = this.entitySeedIdx++;
            RandomSource random = new JavaRandom(this.getSeed() ^ idx).nextRandom(RngUtils.hash(retry));
            var pos = new Vector2(random.nextInt(0, BubbleBlaster.getInstance().getWidth()), random.nextInt(0, BubbleBlaster.getInstance().getWidth()));
            var bubble = new Bubble(this, Bubble.getRandomVariant(this, random));
            this.spawn(bubble, SpawnInformation.naturalSpawn(pos, random, SpawnUsage.BUBBLE_INIT_SPAWN, retry, this));

            messenger.send("Spawning bubble " + i + "/" + maxBubbles);
        }
    }

    public void load(GameSave save, Messenger messenger) throws IOException {
        WorldEvents.WORLD_STARTING.factory().onWorldStarting(this);

        this.name = save.getInfo().getName();

        this.loadWorld(save.load("world", true));
        this.gamemode.onLoad(this, save, messenger);

        this.gameplayStorage = new GameplayStorage(save.load("gameplay"));

        this.initialized = true;

        WorldEvents.WORLD_STARTED.factory().onWorldStarted(this);
    }

    public void save() {
        try {
            this.save(this.gameSave, new DummyMessenger());
        } catch (IOException e) {
            BubbleBlaster.getLogger().error("Error occurred when saving the game: ", e);
        }
    }

    public boolean save(GameSave save, Messenger messenger) throws IOException {
        if (!this.saveLock.tryLock()) return false;
        if (WorldEvents.WORLD_SAVING.factory().onWorldSaving(this, save, messenger).isCanceled()) return false;

        // Gamemode implementation for saving data.
        this.gamemode.onSave(this, save, messenger);

        // Save world data.
        this.dumpRegistries(save, messenger);
        this.dumpPlayers(save, messenger);
        save.dump("world", this.saveWorld(), true);
        save.dump("info", this.saveInfo(), true);
        save.dump("gameplay", this.gameplayStorage.save());

        this.saveLock.unlock();
        WorldEvents.WORLD_SAVED.factory().onWorldSaved(this, save, messenger);
        return true;
    }

    private void dumpPlayers(GameSave save, Messenger messenger) throws IOException {
        messenger.send("Saving players...");
        for (Player p : this.players) {
            this.dumpPlayer(save, p);
        }
    }

    private void dumpPlayer(GameSave save, Player player) throws IOException {
        MapType data = player.save();
        UUID uniqueId = player.getUuid();
        save.dump("players/" + uniqueId, data, true);
    }

    private void loadPlayer(GameSave save, UUID uuid) throws IOException {
        MapType data = save.load("Players/" + uuid, true);
        Player player = new Player(this);
        player.load(data);
        this.players.add(player);

        this.addEntity(player);
    }

    private void addEntity(Entity entity) {
        Preconditions.checkNotNull(entity, "Entity should not be null");
        this.entitiesLock.lock();
        this.addEntityUnlocked(entity);
        this.entitiesLock.unlock();
    }

    private void addEntityUnlocked(Entity entity) {
        if (entity.getId() < MINIMUM_ENTITY_ID)
            entity.setId(this.nextId());

        UUID uuid = UUID.randomUUID();
        while (this.entitiesByUuid.containsKey(uuid))
            uuid = UUID.randomUUID();

        entity.setUuid(uuid);

        this.entitiesById.put(entity.getId(), entity);
        this.entitiesByUuid.put(entity.getUuid(), entity);
    }

    private void dumpRegistries(GameSave save, Messenger messenger) throws IOException {
        messenger.send("Dumping registries.");
        for (Registry<?> registry : Registry.getRegistries()) {
            this.dumpRegistryData(save, registry);
        }
    }

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

    private void loadWorld(MapType data) {
        ListType<MapType> entitiesTag = data.getList("Entities");
        for (MapType entityTag : entitiesTag) {
            Entity entity = Entity.loadFully(this, entityTag);
            if (entity != null) this.addEntity(entity);
        }

        this.player = (Player) Entity.loadFully(this, data.getMap("Player"));
        this.gameOver = data.getBoolean("gameOver", false);
        if (data.<LongType>contains("gameOverTime")) {
            this.gameOverTime = Instant.ofEpochSecond(data.getLong("gameOverTime"));
        }
        this.gamemode = Registries.GAMEMODES.getValue(Identifier.parse(data.getString("gamemode")));
        this.entitySeedIdx = data.getLong("entitySeedIdx");
        this.nextEntityId = data.getLong("nextEntityId");
    }

    private MapType saveWorld() {
        MapType data = new MapType();
        ListType<MapType> entitiesData = new ListType<>();
        for (Entity entity : this.entitiesById.values()) {
            entitiesData.add(entity.save());
        }
        data.put("Entities", entitiesData);
        data.put("Player", this.player.save());
        UUID uuid = this.player.getUuid();
        if (uuid != null) {
            data.putUUID("playerUuid", uuid);
        }
        data.putBoolean("gameOver", this.gameOver);
        if (this.gameOverTime != null) data.putLong("gameOverTime", this.gameOverTime.toEpochMilli());
        data.putLong("entitySeedIdx", this.entitySeedIdx);
        data.putLong("nextEntityId", this.nextEntityId);
        return data;
    }

    private MapType saveInfo() {
        MapType tag = new MapType();
        tag.putString("name", this.name);
        tag.putLong("seed", this.seed);
        tag.putLong("savedTime", LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC));
        Identifier key = Registries.GAMEMODES.getKey(this.gamemode);
        tag.putString("gamemode", (key == null ? Gamemodes.NORMAL.id() : key).toString());
        return tag;
    }

    public BubbleRandomizer getBubbleRandomizer() {
        return this.bubbleRng;
    }

    @CanIgnoreReturnValue
    public boolean triggerGameOver() {
        return this.triggerGameOver(GameOverScreen.TITLE);
    }

    @CanIgnoreReturnValue
    public boolean triggerGameOver(TextObject title) {
        if (!this.gameOverLock.tryLock()) return false;

        if (this.isAlive()) {
            this.setResultScore(Math.round(Objects.requireNonNull(this.getPlayer()).getScore()));
        }

        this.gameOverTime = Instant.now();
        this.gameOver = true;
        this.gamemode.onGameOver();

        PlayerEvents.GAME_OVER.factory().onGameOver(this, this.player, this.gameOverTime);

        this.game.showScreen(new GameOverScreen(this.getResultScore(), title));
        this.save();
        return true;
    }

    public float getLocalDifficulty() {
        Difficulty diff = this.getDifficulty();

        float value = this.difficultyModifiers.modify(diff);;
        if (!BubbleBlasterConfig.DIFFICULTY_EFFECT_TYPE.get().isLocal()) {
            return value;
        }

        this.stateDifficultyModifier = CollectionsUtils.max(new ArrayList<>(this.stateDifficultyModifiers.values()), 1f);
        if (this.getPlayer() == null) return value * this.stateDifficultyModifier;

        int i = (this.getPlayer().getLevel() - 1) * 5 + 1;
        return i * value * this.stateDifficultyModifier;
    }

    public float getStateDifficultyModifier() {
        return this.stateDifficultyModifier;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setGlobalBubbleSpeedModifier(float speedModifier) {
        this.globalBubbleSpeedModifier = speedModifier;
    }

    public float getGlobalBubbleSpeedModifier() {
        return this.bubblesFrozen ? 0 : this.globalBubbleSpeedModifier;
    }

    @Deprecated
    public void setBubblesFrozen(boolean b) {
        this.bubblesFrozen = b;
    }

    public boolean isBubblesFrozen() {
        return this.bubblesFrozen;
    }

    public boolean isGameplayEventActive(GameplayEvent gameplayEvent) {
        return this.gameplayEventActive.contains(gameplayEvent);
    }

    private GameplayContext createGameplayContext() {
        return new GameplayContext(Instant.now(), this, this.gamemode, this.gameplayStorage);
    }

    @Deprecated
    public void addGameStateActive(GameplayEvent gameplayEvent) {
        this.gameplayEventActive.add(gameplayEvent);
    }

    @Deprecated
    public void removeGameStateActive(GameplayEvent gameplayEvent) {
        this.gameplayEventActive.remove(gameplayEvent);
    }

    public boolean isBloodMoonActive() {
        return this.gameplayStorage.get(BubbleBlaster.NAMESPACE).getBoolean(DataKeys.BLOOD_MOON_ACTIVE, false);
    }

    public GameplayStorage getGameplayStorage() {
        return this.gameplayStorage;
    }

    public void tickBloodMoon() {
        LoadedGame loadedGame = BubbleBlaster.getInstance().getLoadedGame();
        if (loadedGame == null) {
            return;
        }

        if (!this.bloodMoonTriggered) {
            if (this.nextBloodMoonCheck == 0) {
                this.nextBloodMoonCheck = System.currentTimeMillis() + 10000;
            }

            if (this.nextBloodMoonCheck < System.currentTimeMillis()) {
                if (this.bloodMoonRng.getNumber(0, 720, this.getTicks()) == 0) {
                    this.triggerBloodMoon();
                } else {
                    this.nextBloodMoonCheck = System.currentTimeMillis() + 10000;
                }
            }
        } else {
            if (this.bloodMoonValueAnimator != null) {
                this.setGlobalBubbleSpeedModifier((float) this.bloodMoonValueAnimator.animate());
                if (this.bloodMoonValueAnimator.isEnded()) {
                    GameplayEvents.BLOOD_MOON_EVENT.activate();
                    this.setCurrentGameEvent(GameplayEvents.BLOOD_MOON_EVENT);
                    this.gameplayStorage.get(BubbleBlaster.NAMESPACE).putBoolean(DataKeys.BLOOD_MOON_ACTIVE, true);

                    if (this.music != null) {
                        this.music.stop();
                    }
                    this.bloodMoonValueAnimator = null;
                    this.bloodMoonValueAnimator1 = new ValueAnimator(8d, 1d, 1000d);
                }
            } else if (this.bloodMoonValueAnimator1 != null) {
                this.setGlobalBubbleSpeedModifier((float) this.bloodMoonValueAnimator1.animate());
                if (this.bloodMoonValueAnimator1.isEnded()) {
                    this.bloodMoonValueAnimator1 = null;
                }
            } else {
                this.setGlobalBubbleSpeedModifier(1);
            }
        }
    }

    public void triggerBloodMoon() {
        if (!this.bloodMoonTriggered) {
            BubbleBlaster.getLogger().info("Triggered blood moon.");
            this.bloodMoonTriggered = true;
            this.bloodMoonValueAnimator = new ValueAnimator(1d, 8d, 10000d);
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
            this.gameplayStorage.get(BubbleBlaster.NAMESPACE).putBoolean(DataKeys.BLOOD_MOON_ACTIVE, false);
            this.bloodMoonTriggered = false;
            GameplayEvents.BLOOD_MOON_EVENT.deactivate();
            BubbleBlaster.getInstance().gameplayMusic.next();
        }

        BubbleBlaster.getInstance().getRenderSettings().resetAntialiasing();
    }

    public long getResultScore() {
        return this.resultScore;
    }

    public void setResultScore(long resultScore) {
        this.resultScore = resultScore;
    }

    @IntRange(from = 0)
    public long getTicks() {
        return this.ticks;
    }

    /**
     * Get a Random Bubble
     * Gets a random bubble from the bubble system.
     *
     * @return The bubble type.
     * @see BubbleSystem#random(RandomSource, World)
     */
    @NonNull
    public BubbleType getRandomBubble(RandomSource random) {
        var bubbleType = this.gamemode.randomBubble(random, this);
        if (bubbleType != null)
            return bubbleType;

        bubbleType = BubbleSystem.random(random, this);

        int retries = 0;
        while (bubbleType == null) {
            bubbleType = BubbleSystem.random(random, this);
            if (++retries == 5)
                return this.gamemode.getDefaultBubble();
        }

        boolean canSpawn = bubbleType.canSpawn(this);

        if (canSpawn) return bubbleType;
        return this.gamemode.getDefaultBubble();
    }

    public void attack(Entity target, double damage, EntityDamageSource damageSource) {
        if (target instanceof LivingEntity e) {
            e.damage(damage, damageSource);
        }
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public Difficulty.ModifierMap getDifficultyModifiers() {
        return this.difficultyModifiers;
    }

    public void setStateDifficultyModifier(GameplayEvent gameplayEvent, float modifier) {
        this.stateDifficultyModifiers.put(gameplayEvent, modifier);
    }

    public void removeStateDifficultyModifier(GameplayEvent gameplayEvent) {
        this.stateDifficultyModifiers.remove(gameplayEvent);
    }

    public Object getStateDifficultyModifier(GameplayEvent gameplayEvent) {
        return this.stateDifficultyModifiers.get(gameplayEvent);
    }

    /**
     * Get all entities currently spawned.
     *
     * @return all the entities.
     */
    public Collection<Entity> getEntities() {
        return Collections.unmodifiableCollection(this.entitiesById.values());
    }

    /**
     * Spawn entity from loading.
     *
     * @param entityData the data create the entity to spawn.
     */
    public void spawnEntityFromState(MapType entityData) {
        if (!BubbleBlaster.isOnTickingThread()) {
            BubbleBlaster.invokeTick(() -> this.loadAndSpawnEntity(entityData));
            return;
        }
        this.loadAndSpawnEntity(entityData);
    }

    private void loadAndSpawnEntity(MapType data) {
        String type = data.getString("Type");
        EntityType<?> entityType = Registries.ENTITIES.getValue(Identifier.parse(type));
        Entity entity = entityType.create(this, data);
        entity.preSpawn(SpawnInformation.loadingSpawn(data));
        entity.load(data);

        this.addEntity(entity);
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
     * Spawn an entity into the world.
     */
    public void spawn(Entity entity, SpawnInformation information) {
        BubbleBlaster.invokeTick(() -> {
            if (EntityEvents.SPAWN.factory().onSpawn(entity, information).isCanceled()) {
                return;
            }

            // Prepare entity with spawn information,
            entity.preSpawn(information);

            var pos = entity.getPos();
            entity.onSpawn(pos, this);

            this.addEntity(entity);
        });
    }

    private long nextId() {
        return this.nextEntityId++;
    }

    public <T extends Entity> T spawn(EntityType<T> type, SpawnInformation information) {
        T entity = type.create(this, information.getData());
        this.spawn(entity, information);
        return entity;
    }

    /**
     * @return the game type bound to this world.
     */
    public Gamemode getGamemode() {
        return this.gamemode;
    }

    /**
     * Check if the world is initialized.
     *
     * @return true if initialized.
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Tick the world.<br>\
     */
    @ApiStatus.Internal
    public void tick() {
        if (this.bubblesFrozen && this.bubblesFrozenTicks-- <= 0) {
            this.bubblesFrozenTicks = 0;
            this.bubblesFrozen = false;
        }

        if (this.initialized) {
            entities: {
                if (!this.entitiesLock.tryLock()) break entities;

                // Tick entities
                List<Entity> toRemove = new ArrayList<>();
                for (Entity entity : this.entitiesById.values()) {
                    if (entity.willBeDeleted()) {
                        toRemove.add(entity);
                        continue;
                    }

                    if (entity instanceof Player p) TickEvents.PRE_TICK_PLAYER.factory().onTickPlayer(p);
                    TickEvents.PRE_TICK_ENTITY.factory().onTickEntity(entity);
                    entity.tick(this);
                    TickEvents.POST_TICK_ENTITY.factory().onTickEntity(entity);
                    if (entity instanceof Player p) TickEvents.POST_TICK_PLAYER.factory().onTickPlayer(p);
                }

                for (Entity entity : toRemove) {
                    this.entitiesById.remove(entity.getId());
                    this.entitiesByUuid.remove(entity.getUuid());
                }

                this.entitiesLock.unlock();
            }

            this.tickSpawning();
            this.tickBloodMoon();

            // Tick gameplay events
            gamePlayEvent: {
                if (this.currentGameplayEvent != null) {
                    if (!this.currentGameplayEvent.shouldContinue(this.createGameplayContext())) {
                        if (!WorldEvents.GAMEPLAY_EVENT_DEACTIVATED.factory().onGameplayEventDeactivated(this, this.currentGameplayEvent).isCanceled()) {
                            this.currentGameplayEvent.end(this);
                            this.currentGameplayEvent = null;
                        }
                    }

                    GameplayEvent gameplayEvent = this.currentGameplayEvent;
                    if (gameplayEvent != null) {
                        gameplayEvent.tick();
                    }

                    break gamePlayEvent;
                }

                this.onlyTickEvery(5, () -> {
                    List<GameplayEvent> choices = RngUtils.choices(Registries.GAMEPLAY_EVENTS.values(), 3);
                    for (GameplayEvent gameplayEvent : choices) {
                        if (gameplayEvent.shouldActivate(this.createGameplayContext())) {
                            if (!WorldEvents.GAMEPLAY_EVENT_TRIGGERED.factory().onGameplayEventTriggered(this, gameplayEvent).isCanceled()) {
                                this.currentGameplayEvent = gameplayEvent;
                                this.currentGameplayEvent.begin(this);

                                break;
                            }
                        }
                    }
                });
            }

            // Advance ticks
            this.ticks++;
        }
    }

    private void tickSpawning() {
        if (this.entitiesById.values().stream().filter(Bubble.class::isInstance).count() < GameSettings.instance().maxBubbles) {
            long idx = this.entitySeedIdx++;
            RandomSource random = new JavaRandom(this.seed ^ idx);
            var variant = Bubble.getRandomVariant(this, random.nextRandom());

            final int retry = 0;
            BubbleSpawnContext.inContext(random, retry, () -> {
                Bubble bubble = new Bubble(this, variant);
                this.spawn(bubble, SpawnInformation.naturalSpawn(null, random, SpawnUsage.BUBBLE_SPAWN, retry, this));
                return bubble;
            });
        }
    }

    public void gameOver(Player player) {
        synchronized (this.entitiesLock) {
            this.entitiesById.remove(player.getId());
        }
    }

    public void joinPlayer(Player player) {
        this.entitiesLock.lock();
        this.players.add(player);
        this.addEntityUnlocked(player);
        this.entitiesLock.unlock();
    }

    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> bubbleEntityClass) {
        synchronized (this.entitiesLock) {
            return this.entitiesById.values().stream()
                    .filter(entity -> bubbleEntityClass.isAssignableFrom(entity.getClass()))
                    .map(bubbleEntityClass::cast).toList();
        }
    }

    public GameplayEvent getCurrentGameEvent() {
        return this.currentGameplayEvent;
    }

    public void close() {
        this.shuttingDown = true;

        WorldEvents.WORLD_STOPPING.factory().onWorldStopping(this);

        synchronized (this.entitiesLock) {
            for (Entity entity : this.entitiesById.values()) {
                entity.delete();
            }
        }

        WorldEvents.WORLD_STOPPED.factory().onWorldStopped(this);
    }

    public void setCurrentGameEvent(GameplayEvent currentGameplayEvent) {
        this.currentGameplayEvent = currentGameplayEvent;
    }

    public long getSeed() {
        return this.seed;
    }

    @EnsuresNonNullIf(expression = "getGameOverTime()", result = true)
    public @Pure boolean isGameOver() {
        return this.gameOver;
    }

    public boolean isAlive() {
        return !this.gameOver;
    }

    public Player getPlayer() {
        return this.player;
    }

    public BubbleBlaster game() {
        return this.game;
    }

    public GameSave getGameSave() {
        return this.gameSave;
    }

    public void prepareCreation(GameSave save) throws IOException {
        save.createFolders("registries");
    }

    @Nullable
    public Entity getEntityAt(Vec2i pos) {
        for (Entity entity : this.entitiesById.values()) {
            if (entity.getShape().contains(new Vector2(pos.x, pos.y))) {
                return entity;
            }
        }

        return null;
    }

    public void onLevelUp(Player player, int to) {
        if (player == this.player) {
            this.gamemode.onLevelUp(player, to);
            HudType.getCurrent().onLevelUp(to);
        }
    }

    @Nullable
    public Entity getNearestEntity(Vector2 pos) {
        double distance = Double.MAX_VALUE;
        Entity nearest = null;
        for (Entity entity : this.entitiesById.values()) {
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
        for (Entity entity : this.entitiesById.values()) {
            if (!entity.getType().equals(targetType)) continue;
            double cur = entity.distanceTo(pos);
            if (cur < distance) {
                distance = cur;
                nearest = entity;
            }
        }
        return nearest;
    }

    public void freezeBubblesSecs(int seconds) {
        this.freezeBubbles(seconds * TPS);
    }

    public void freezeBubbles(int ticks) {
        this.bubblesFrozenTicks = ticks;
        this.bubblesFrozen = true;
    }

    public boolean isSaving() {
        return this.saveLock.isLocked();
    }

    public void dispose() {
        this.gamemode.end();
        this.entitiesById.clear();
    }

    public void annihilate() {
        this.gamemode.end();
        this.entitiesById.clear();
        this.players.clear();
        this.player = null;
    }

    @Override
    public void fillInCrash(CrashLog crashLog) {
        var category = new CrashCategory("World", new RuntimeException());
        category.add("Players", this.players);
        category.add("Entities", this.entitiesById);
        category.add("Game Save", this.gameSave);
        category.add("Cur. Gameplay Event", this.currentGameplayEvent);
        crashLog.addCategory(category);
    }

    @Nullable
    public Instant getGameOverTime() {
        return this.gameOverTime;
    }

    public MusicEvent getMusic() {
        return this.music;
    }
}
