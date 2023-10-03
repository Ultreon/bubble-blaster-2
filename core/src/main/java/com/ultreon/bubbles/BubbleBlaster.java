package com.ultreon.bubbles;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.effects.VfxEffect;
import com.google.common.annotations.Beta;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.audio.MusicEvent;
import com.ultreon.bubbles.audio.MusicSystem;
import com.ultreon.bubbles.audio.SoundInstance;
import com.ultreon.bubbles.common.Difficulty;
import com.ultreon.bubbles.common.GameFolders;
import com.ultreon.bubbles.common.exceptions.FontLoadException;
import com.ultreon.bubbles.common.exceptions.ResourceNotFoundException;
import com.ultreon.bubbles.data.DataKeys;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.debug.DebugRenderer;
import com.ultreon.bubbles.debug.Profiler;
import com.ultreon.bubbles.debug.ThreadSection;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.event.v1.*;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.*;
import com.ultreon.bubbles.input.GameInput;
import com.ultreon.bubbles.mod.loader.GameJar;
import com.ultreon.bubbles.mod.loader.LibraryJar;
import com.ultreon.bubbles.notification.Notification;
import com.ultreon.bubbles.notification.Notifications;
import com.ultreon.bubbles.player.InputController;
import com.ultreon.bubbles.player.PlayerController;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.*;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.screen.*;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.util.RandomValueSource;
import com.ultreon.bubbles.world.World;
import com.ultreon.bubbles.world.WorldRenderer;
import com.ultreon.gameprovider.bubbles.OS;
import com.ultreon.libs.collections.v0.maps.OrderedHashMap;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.Mth;
import com.ultreon.libs.commons.v0.ProgressMessenger;
import com.ultreon.libs.commons.v0.util.FileUtils;
import com.ultreon.libs.commons.v0.util.IllegalCallerException;
import com.ultreon.libs.crash.v0.ApplicationCrash;
import com.ultreon.libs.crash.v0.CrashLog;
import com.ultreon.libs.events.v1.ValueEventResult;
import com.ultreon.libs.registries.v0.Registry;
import com.ultreon.libs.resources.v0.ResourceManager;
import com.ultreon.libs.translations.v1.LanguageManager;
import de.jcm.discordgamesdk.activity.Activity;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTabBarFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.FormattedException;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;
import net.fabricmc.loader.impl.util.Arguments;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationScheduler;
import org.apache.logging.log4j.core.util.WatchManager;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.NotThreadSafe;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.glfwInit;

/**
 * The Bubble Blaster game main class.
 *
 * @since 0.0.1
 */
@ParametersAreNonnullByDefault
@SuppressWarnings({"ResultOfMethodCallIgnored", "unused", "RedundantSuppression"})
public final class BubbleBlaster extends ApplicationAdapter implements CrashFiller {
    public static final int TPS = 40;
    public static final String NAMESPACE = "bubbleblaster";

    // Logger.
    public static final Logger LOGGER = LoggerFactory.getLogger("Generic");
    public static final Instant BOOT_TIME = Instant.now();
    public static final Random RANDOM = new Random();

    private static final WatchManager WATCHER = new WatchManager(new ConfigurationScheduler("File Watcher"));
    private static final ImBoolean SHOW_INFO_WINDOW = new ImBoolean(false);
    private static final ImBoolean SHOW_FPS_GRAPH = new ImBoolean(false);
    private static final ImBoolean SHOW_ENTITY_EDITOR = new ImBoolean(false);
    private static final ImBoolean SHOW_GUI_MODIFIER = new ImBoolean(false);
    private static final ImBoolean DEBUG_GUI_OPEN = new ImBoolean(false);
    private static final ImBoolean SHOW_COLLISIONS_SHAPES = new ImBoolean(false);
    private static final ImBoolean SHOW_DEBUG_UTILS = new ImBoolean(FabricLoader.getInstance().isDevelopmentEnvironment());
    // Modes
    private static boolean debugMode;
    // Initial game information / types.
    private static ClassLoader classLoader;
    private static File gameDir = null;
    // Number values.
    private static long ticks = 0L;
    // Instance
    private static BubbleBlaster instance;
    private static boolean hasRendered;
    private static LibraryJar jar;
    private static final Supplier<ModContainer> FABRIC_LOADER_CONTAINER = Suppliers.memoize(() -> FabricLoader.getInstance().getModContainer("fabricloader").orElseThrow());
    private static final Supplier<ModMetadata> FABRIC_LOADER_META = Suppliers.memoize(() -> FABRIC_LOADER_CONTAINER.get().getMetadata());
    private static final Supplier<Version> FABRIC_LOADER_VERSION = Suppliers.memoize(() -> FABRIC_LOADER_META.get().getVersion());
    private static final Supplier<ModContainer> GAME_CONTAINER = Suppliers.memoize(() -> FabricLoader.getInstance().getModContainer(NAMESPACE).orElseThrow());
    private static final Supplier<ModMetadata> GAME_META = Suppliers.memoize(() -> GAME_CONTAINER.get().getMetadata());
    private static final Supplier<Version> GAME_VERSION = Suppliers.memoize(() -> GAME_META.get().getVersion());

    private static final Lock RENDER_CALL_LOCK = new ReentrantLock(true);
    private static final Deque<Consumer<Renderer>> RENDER_CALLS = Queues.synchronizedDeque(Queues.newArrayDeque());
    private static File dataDir;

    // Public Game Components
    public final Notifications notifications;
    public final Profiler profiler = new Profiler();

    // ImGui Implementations
    private final ImGuiImplGlfw imGuiGlfw;
    private final ImGuiImplGl3 imGuiGl3;

    // Tasks
    final List<Runnable> tasks = new CopyOnWriteArrayList<>();

    // Fonts.
    private BitmapFont sansFont;
    private String fontName;

    private URL gameFile;

    public final ScheduledExecutorService schedulerService = Executors.newScheduledThreadPool(Math.max(Runtime.getRuntime().availableProcessors() / 4, 2));
    private ResourceManager resourceManager;
    private GameWindow window;
    private Screen screen;
    private RenderSettings renderSettings;
    private DiscordRPC discordRpc;
    // Rendering
    private DebugRenderer debugRenderer;
    private WorldRenderer worldRenderer;
    // Managers.
    private TextureManager textureManager;

    // Misc
    private final Texture background = null;
    private final Object rpcUpdateLock = new Object();
    private final Ticker ticker = new Ticker();

    // Utility objects.
    public InputController input;

    // World
    @Nullable
    public World world;

    // Player entity
    public Player player;
    Texture cachedTexture;

    // Values
    @IntRange(from = 0) int fps;
    private int currentTps;
    private PlayerController playerController;

    // Game states.
    private boolean loaded;
    private static boolean crashed;

    // Running value.
    private volatile boolean running = false;

    // Threads
    private static Thread renderingThread;
    private static Thread tickingThread;
    private GarbageCollector garbageCollector;
    float gameFrameTime;
    private boolean stopping;
    private BitmapFont monospaceFont;
    private BitmapFont pixelFont;
    private BitmapFont logoFont;

    // Loaded game.
    @Nullable
    private LoadedGame loadedGame;
    private final ManualCrashOverlay manualCrashOverlay = new ManualCrashOverlay();
    private GlitchRenderer glitchRenderer = null;
    private boolean isGlitched = false;
    private Supplier<Activity> activity;
    private volatile boolean rpcUpdated;
    private final Map<Thread, ThreadSection> lastProfile = new HashMap<>();
    private float fadeInDuration = Float.NEGATIVE_INFINITY;
    private boolean fadeIn = false;
    private long fadeInStart = 0L;
    private final boolean firstFrame = true;
    private final GlobalSaveData globalSaveData = GlobalSaveData.instance();
    private OrthographicCamera camera;
    private Viewport viewport;
    private boolean focused;
    private int transX;
    private int transY;
    private Renderer currentRenderer;
    private Renderer renderer;
    private final Map<UUID, Runnable> afterLoading = new OrderedHashMap<>();
    private long lastTickMs;
    private boolean isTickThreadDead;
    private SpriteBatch batch;
    private final List<MusicEvent> menuMusicList = Lists.newArrayList(MusicEvents.SUBMARINE);
    private final List<MusicEvent> gameplayMusicList = Lists.newArrayList(MusicEvents.SUBMARINE);
    public final MusicSystem gameplayMusic = new MusicSystem(RandomValueSource.random(3.0, 6.0), RandomValueSource.random(40.0, 80.0), this.gameplayMusicList);
    public final MusicSystem menuMusic = new MusicSystem(RandomValueSource.random(1.0, 3.0), RandomValueSource.random(5.0, 10.0), this.menuMusicList);
    private ShapeRenderer shapes;

    public BubbleBlaster() {
        if (instance != null) {
            throw new Error("The game should not open twice");
        }

        this.imGuiGlfw = new ImGuiImplGlfw();
        this.imGuiGl3 = new ImGuiImplGl3();

        instance = this;
        this.notifications = new Notifications();
    }

    public static <T> T invokeAndWait(Supplier<T> func) {
        Thread thread = Thread.currentThread();
        if (BubbleBlaster.isOnRenderingThread()) return func.get();

        AtomicReference<T> ref = new AtomicReference<>();
        RENDER_CALLS.addLast(renderer -> {
            ref.set(func.get());
            LockSupport.unpark(thread);
        });
        LockSupport.park();
        return ref.get();
    }

    public static void invokeAndWait(Runnable func) {
        Thread thread = Thread.currentThread();
        if (BubbleBlaster.isOnRenderingThread()) {
            func.run();
            return;
        }

        RENDER_CALLS.addLast(renderer -> {
            func.run();
            LockSupport.unpark(thread);
        });
        LockSupport.park();
    }

    public static void invoke(Runnable func) {
        Thread thread = Thread.currentThread();
        if (BubbleBlaster.isOnRenderingThread()) {
            func.run();
            return;
        }

        RENDER_CALLS.addLast(renderer -> func.run());
    }

    public static <T> T invokeTickAndWait(Supplier<T> func) {
        Thread thread = Thread.currentThread();
        if (BubbleBlaster.isOnTickingThread()) return func.get();

        AtomicReference<T> ref = new AtomicReference<>();
        instance.scheduleTick(() -> {
            ref.set(func.get());
            LockSupport.unpark(thread);
        });
        LockSupport.park();
        return ref.get();
    }

    public static void invokeTickAndWait(Runnable func) {
        Thread thread = Thread.currentThread();
        if (BubbleBlaster.isOnTickingThread()) {
            func.run();
            return;
        }

        instance.scheduleTick(() -> {
            func.run();
            LockSupport.unpark(thread);
        });
        LockSupport.park();
    }

    public static void invokeTick(Runnable task) {
        if (BubbleBlaster.isOnTickingThread()) task.run();
        else instance.scheduleTick(task);
    }

    public static Instant getBootTime() {
        return BOOT_TIME;
    }

    public static void whenLoaded(UUID id, Runnable func) {
        if (!BubbleBlaster.isOnRenderingThread()) {
            BubbleBlaster.invokeAndWait(() -> BubbleBlaster.whenLoaded(id, func));
            return;
        }

        if (instance.isLoaded()) {
            func.run();
            return;
        }

        if (!instance.afterLoading.containsKey(id)) {
            instance.afterLoading.put(id, func);
        }
    }

    public static FileHandle data(String path) {
        return new FileHandle(dataDir).child(path);
    }

    public static File getDataDir() {
        return dataDir;
    }

    public static FileHandle resource(Identifier id) {
        return Gdx.files.internal("assets/" + id.location() + "/" + id.path());
    }

    @NotThreadSafe
    public static Sound newSound(Identifier resourceId) {
        if (!BubbleBlaster.isOnRenderingThread())
            throw new IllegalCallerException("Creating a sound object should be on the rendering thread!");

        FileHandle resource = BubbleBlaster.resource(resourceId);
        if (!resource.exists()) throw new ResourceNotFoundException(resourceId);

        return Gdx.audio.newSound(resource);
    }

    @NotThreadSafe
    public static Music newMusic(Identifier resourceId) {
        if (!BubbleBlaster.isOnRenderingThread())
            throw new IllegalCallerException("Creating a music object should be on the rendering thread.");

        FileHandle resource = BubbleBlaster.resource(resourceId);
        if (!resource.exists()) throw new ResourceNotFoundException(resourceId);

        return Gdx.audio.newMusic(resource);
    }

    @Override
    public void create() {
        if (renderingThread == null) renderingThread = Thread.currentThread();

        HdpiUtils.setMode(HdpiMode.Pixels);

        // Pre-init ImGui
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Initialize ImGui
        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.getFonts().addFontDefault();

        long windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();

        this.imGuiGlfw.init(windowHandle, true);
        this.imGuiGl3.init("#version 150");

        // Set game input processor for LibGDX
        GameInput input = new GameInput();
        Gdx.input.setInputProcessor(input);

        // Set HiDpi mode
        HdpiUtils.setMode(HdpiMode.Pixels);

        this.textureManager = TextureManager.instance();
        this.debugRenderer = new DebugRenderer(this);
        this.camera = new OrthographicCamera();
        this.viewport = new ScreenViewport(this.camera);

        this.batch = new SpriteBatch();
        this.shapes = new ShapeRenderer();

        this.shapes.setAutoShapeType(true);
        this.renderer = new Renderer(this.shapes, this.batch, this.camera);

        // Set default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(new GameExceptions());
        Thread.currentThread().setUncaughtExceptionHandler(new GameExceptions());

        // Hook output for logger.
        System.setErr(new RedirectPrintStream(Level.ERROR, LogManager.getLogger("STDERR")));
        System.setOut(new RedirectPrintStream(Level.INFO, LogManager.getLogger("STDOUT")));

        // Assign instance.
        this.window = new GameWindow(new GameWindow.Properties("Bubble Blaster", 1280, 720).close(this::dispose)/*.fullscreen()*/);

        this.gameFile = BubbleBlaster.class.getProtectionDomain().getCodeSource().getLocation();

        // Prepare for game launch
        this.resourceManager = new ResourceManager("assets");
        this.renderSettings = new RenderSettings();

        // Invoke entry points.
        EntrypointUtils.invoke("main", ModInitializer.class, ModInitializer::onInitialize);

        DebugFormatters.initClass();

        // Prepare for loading.
        this.prepare();

        this.sansFont = this.loadFont(Gdx.files.internal("assets/bubbleblaster/fonts/noto_sans/noto_sans_regular.ttf"), 14);

        BubbleTypes.register();
        AmmoTypes.register();
        Entities.register();
        Fonts.register();
        SoundEvents.register();
        MusicEvents.register();
        HudTypes.register();
        StatusEffects.register();
        Abilities.register();
        GameplayEvents.register();
        Gamemodes.register();
        TextureCollections.register();

        // Load game with loading screen.
        this.load(new ProgressMessenger(this::log, 1000));

        // Enable Discord RPC
        this.discordRpc = new DiscordRPC();

        BubbleBlaster.setActivity(() -> {
            var activity = new Activity();
            activity.setState("Loading game.");
            return activity;
        });

        LOGGER.info("Discord RPC is initializing!");

        // Set world renderer
        this.worldRenderer = new WorldRenderer();

        try {
            URL resource = this.getClass().getClassLoader().getResource(".resource-root");
            if (resource == null) {
                this.crash(new Exception("Game resource root not found!"));
                return;
            }

            Path filePath = BubbleBlaster.getGamePath(resource);

            LOGGER.debug("Valid game path: " + filePath);

            this.resourceManager.importPackage(filePath);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Set current work directory
        FileUtils.setCwd(GameFolders.DATA_DIR);

        // Logs directory creation.
        GameFolders.LOGS_DIR.mkdirs();

        // Font Name
        this.fontName = "Chicle Regular";

        // Register events.
        InputEvents.KEY_PRESS.listen(this::onKeyPress);
        InputEvents.MOUSE_CLICK.listen(this::onMouseClick);

        GameEvents.CLIENT_STARTED.factory().onClientStarted(this);

        // Start scene-manager.
        try {
            this.showScreen(new SplashScreen(), true);
        } catch (Throwable t) {
            var crashLog = new CrashLog("Oops, game crashed!", t);
            this.crash(t);
        }

        if (!this.window.isFocused()) {
            this.window.requestUserAttention();
        }
    }

    private static Path getGamePath(URL resource) throws IOException, URISyntaxException {
        String protocol = resource.getProtocol();
        String host = resource.getHost();
        String file = resource.getFile();

        Path filePath;

        switch (protocol) {
            case "jar" -> {
                JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();
                URL fileUrl = jarURLConnection.getJarFileURL();
                if (!fileUrl.getProtocol().equals("file")) {
                    throw new Error("Jar file isn't local.");
                }
                filePath = Path.of(fileUrl.toURI());
            }
            case "file" -> filePath = Path.of(resource.toURI()).getParent();
            default -> throw new Error("Illegal protocol: " + resource.getProtocol());
        }
        return filePath;
    }

    @Override
    public void pause() {
        if (this.isInGame() && !(this.getCurrentScreen() instanceof PauseScreen)) {
            this.showScreen(new PauseScreen());
            this.gameplayMusic.pause();
        }
    }

    @Override
    public void resume() {
        if (this.isInGame() && this.getCurrentScreen() instanceof PauseScreen) {
            this.showScreen(null);
            this.gameplayMusic.play();
        }
    }

    @Override
    public void resize(int width, int height) {
        this.camera.setToOrtho(true, width, height); // Set up the camera's projection matrix
        this.renderer.resize(width, height);
        this.viewport.update(width, height);

        GlitchRenderer glitch = this.glitchRenderer;
        if (glitch != null)
            glitch.resize(width, height);

        var screen = this.screen;
        if (screen != null)
            screen.resize(width, height);
    }

    private void drawMasks() {
        /* Clear our depth buffer info from previous frame. */
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        /* Set the depth function to LESS. */
        Gdx.gl.glDepthFunc(GL20.GL_LESS);

        /* Enable depth writing. */
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        /* Disable RGBA color writing. */
        Gdx.gl.glColorMask(false, false, false, false);

        /* Render mask elements. */
        this.shapes.set(ShapeRenderer.ShapeType.Filled);
        this.shapes.circle(100, 200, 100);
        this.shapes.triangle(0, 0, 100, 100, 200, 0);
        this.shapes.flush();
    }
    private void drawMasked() {
        /* Enable RGBA color writing. */
        Gdx.gl.glColorMask(true, true, true, true);

        /* Set the depth function to EQUAL. */
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

        /* Render masked elements. */
        this.shapes.setColor(Color.RED.toGdx());
        this.shapes.circle(100, 100, 100);
        this.shapes.flush();
    }
    private void drawContours() {
        /* Disable depth writing. */
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

        /* The circle and triangle masks. */
        this.batch.setColor(Color.CYAN.toGdx());
        this.shapes.circle(100, 200, 100);
        this.shapes.triangle(0, 0, 100, 100, 200, 0);

        /* The masked circle. */
        this.batch.setColor(Color.GREEN.toGdx());
        this.shapes.circle(100, 100, 100);
    }
    @Override
    public void render() {
        try {
            if (Gdx.graphics.getFrameId() == 2) {
                BubbleBlasterConfig.CONFIG.reload();
                this.window.setFullscreen(BubbleBlasterConfig.FULLSCREEN.get());
                this.firstRender();
            }

            this.tasks.forEach(Runnable::run);
            this.tasks.clear();

            this.camera.update();

            this.renderer.begin();
            this.currentRenderer = this.renderer;

            if (this.isLoaded()) {
                if (Instant.now().isAfter(this.getLastTickTime().plusSeconds(60)) && !this.isTickThreadDead) {
                    this.markTickingDead();
                    this.notifications.notify(
                            Notification.builder("Game Ticking", "Game ticking hasn't happened in 60 secs!")
                                    .subText("Watchdog")
                                    .duration(Duration.ofSeconds(5))
                                    .build()
                    );
                } else if (this.isTickThreadDead) {
                    this.isTickThreadDead = false;
                    this.notifications.notify(
                            Notification.builder("Game Ticking", "Game ticking came back!")
                                    .subText("Watchdog")
                                    .duration(Duration.ofSeconds(5))
                                    .build()
                    );
                }
            }

            try {
                GridPoint2 mousePos = GameInput.getPosGrid();
                int mouseX = mousePos.x;
                int mouseY = mousePos.y;

                float deltaTime = Gdx.graphics.getDeltaTime();

                int size = RENDER_CALLS.size();
                for (int counter = 0; counter < size; counter++) {
                    RENDER_CALLS.removeFirst().accept(this.renderer);
                }

                var filters = BubbleBlaster.instance.getCurrentFilters();

                this.profiler.section("renderGame", () -> this.renderGame(this.renderer, mouseX, mouseY, this.gameFrameTime));

                if (BubbleBlaster.isDebugMode() || DEBUG_GUI_OPEN.get()) {
                    this.debugRenderer.render(this.renderer);
                }

                this.fps = Gdx.graphics.getFramesPerSecond();
                this.notifications.render(this.renderer, mouseX, mouseY, deltaTime);

                if (SHOW_DEBUG_UTILS.get()) {
                    this.renderImGui(this.renderer);
                }

                if (this.isGlitched && BubbleBlasterConfig.ENABLE_ANNOYING_EASTER_EGGS.get()) {
                    this.glitchRenderer.render(this.renderer);
                } else {
                    this.manualCrashOverlay.render(this.renderer, mouseX, mouseY, deltaTime);
                }
            } catch (OutOfMemoryError error) {
                this.outOfMemory(error);
            }

            this.currentRenderer = null;
            this.renderer.end();
        } catch (Exception e) {
            this.crash(e);
        }
    }

    private void markTickingDead() {
    }

    private void firstRender() {
        this.window.setVisible(true);
        this.window.setFullscreen(BubbleBlasterConfig.FULLSCREEN.get());
    }

    private void renderImGui(Renderer renderer) {
        // render 3D scene
        this.imGuiGlfw.newFrame();

        int tabBarFlags = ImGuiTabBarFlags.AutoSelectNewTabs | ImGuiTabBarFlags.Reorderable | ImGuiTabBarFlags.FittingPolicyResizeDown | ImGuiTabBarFlags.NoCloseWithMiddleMouseButton;

        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(Gdx.graphics.getWidth(), 18);
        ImGui.setNextWindowCollapsed(true);

        if (ImGui.begin("BB DebugUtils", ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.AlwaysAutoResize |
                ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.MenuBar |
                ImGuiInputTextFlags.AllowTabInput)) {
            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu("View")) {
                    ImGui.menuItem("Show Info Window", null, SHOW_INFO_WINDOW);
                    ImGui.menuItem("Show FPS Graph", null, SHOW_FPS_GRAPH);
                    ImGui.endMenu();
                }
                if (ImGui.beginMenu("Entity")) {
                    ImGui.menuItem("Show Collisions Shapes", null, SHOW_COLLISIONS_SHAPES);
                    ImGui.menuItem("Entity Editor", null, SHOW_ENTITY_EDITOR, this.isInGame());
                    ImGui.endMenu();
                }
                if (ImGui.beginMenu("GUI")) {
                    ImGui.menuItem("Show Debug Overlay", null, DEBUG_GUI_OPEN);
                    ImGui.menuItem("GUI Modifier", null, SHOW_GUI_MODIFIER);
                    ImGui.endMenu();
                }
                ImGui.endMenuBar();
            }
            ImGui.end();
        }
        if (SHOW_INFO_WINDOW.get()) {
            this.showInfoWindow();
        }
        if (SHOW_GUI_MODIFIER.get()) {
            this.showGuiModifier(renderer);
        }
        ImGui.render();
        this.imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    private void showGuiModifier(Renderer renderer) {
        Screen screen = this.getCurrentScreen();
        GuiComponent exactWidgetAt = null;
        if (screen != null) exactWidgetAt = screen.getExactWidgetAt(Gdx.input.getX(), Gdx.input.getY());

        if (exactWidgetAt != null) {
            var bounds = exactWidgetAt.getBounds();
            renderer.box(bounds.x, bounds.y, bounds.width, bounds.height, Color.RED);
        }

        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("Gui Utilities")) {
            ImGui.text("Screen: " + (screen == null ? "null" : screen.getClass().getSimpleName()));
            ImGui.text("Widget: " + (exactWidgetAt == null ? "null" : exactWidgetAt.getClass().getSimpleName()));
        }
        ImGui.end();
    }

    private void showInfoWindow() {
        Screen screen = this.getCurrentScreen();
        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("Debug Info")) {
            ImGui.button("I'm a Button!");
            ImGui.text("Screen:" + (screen == null ? "null" : screen.getClass().getName()));
        }
        ImGui.end();
    }

    @Override
    @ApiStatus.Internal
    public void dispose() {
        this.imGuiGl3.dispose();
        this.imGuiGlfw.dispose();
        ImGui.destroyContext();

        instance.onClose();
        if (crashed) {
            System.exit(1);
        }
        System.exit(0);
    }

    public static Map<Thread, ThreadSection> getLastProfile() {
        return instance.lastProfile;
    }

    public static WatchManager getWatcher() {
        return WATCHER;
    }

    public static boolean hasRendered() {
        return hasRendered;
    }

    public static BubbleBlaster getInstance() {
        return instance;
    }

    public static Identifier id(String path) {
        return new Identifier(NAMESPACE, path);
    }

    /**
     * Launch method.
     * Contains argument parsing.
     *
     * @return the bubble blaster instance.
     */
    public static BubbleBlaster launch(Arguments args) {
        System.setProperty("log4j2.formatMsgNoLookups", "true"); // Fix CVE-2021-44228 exploit.
        Identifier.setDefaultNamespace(NAMESPACE);
        ResourceManager.logger = BubbleBlaster.getLogger("ResourceManager");
        Registry.dumpLogger = BubbleBlaster.getLogger("RegistryDump");
        LanguageManager.INSTANCE.logger = BubbleBlaster.getLogger("LanguageManager");

        // Get game-directory.
        final var defaultGameDir = new File(".");
        gameDir = !args.containsKey("gameDir") ? defaultGameDir : new File(args.get("gameDir"));
        debugMode = args.getExtraArgs().contains("--debug");

        BubbleBlaster.classLoader = BubbleBlaster.getClassLoader();

        String property = System.getProperty("user.home");

        Path path;
        if (OS.isWindows()) path = Path.of(System.getenv("APPDATA"), "BubbleBlaster");
        else if (OS.isMacintosh()) path = Path.of(System.getProperty("user.home"), "Library/Application Support/BubbleBlaster");
        else if (OS.isLinux()) path = Path.of(System.getProperty("user.home"), ".config/BubbleBlaster");
        else throw new FormattedException("Unsupported Platform", "Platform unsupported: " + System.getProperty("os.name"));

        dataDir = path.toFile();

        FileUtils.setCwd(dataDir);

        return new BubbleBlaster();
    }

    private static com.ultreon.libs.commons.v0.Logger getLogger(String name) {
        Logger logger = LoggerFactory.getLogger(name);
        return (level, message, t) -> {
            switch (level) {
                case ERROR -> logger.error(message, t);
                case WARN -> logger.warn(message, t);
                case INFO -> logger.info(message, t);
                case DEBUG -> logger.debug(message, t);
            }
        };
    }

    private static String getAppData() {
        return null;
    }

    /////////////////////////
    //     Game values     //
    /////////////////////////
    public static long getTicks() {
        return ticks;
    }

    ///////////////////////////
    //     Value options     //
    ///////////////////////////
    public static File getGameDir() {
        return gameDir;
    }

    ///////////////////////////
    //     Startup Modes     //
    ///////////////////////////
    public static boolean isDebugMode() {
        return debugMode;
    }

    @Deprecated
    public static boolean isDevEnv() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    ///////////////////////////
    //     Class Loaders     //
    ///////////////////////////
    public static ClassLoader getClassLoader() {
        return classLoader;
    }

    /////////////////////////////
    //     Middle position     //
    /////////////////////////////
    public static float getMiddleX() {
        return (float) BubbleBlaster.getInstance().getWidth() / 2;
    }

    public static float getMiddleY() {
        return (float) BubbleBlaster.getInstance().getHeight() / 2;
    }

    public static Vector2 getMiddlePoint() {
        return new Vector2(BubbleBlaster.getMiddleX(), BubbleBlaster.getMiddleY());
    }

    /////////////////////
    //     Loggers     //
    /////////////////////
    public static Logger getLogger() {
        return LOGGER;
    }

    /////////////////////////////////////
    //     Reduce ticks to seconds     //
    /////////////////////////////////////
    public static byte reduceTicks2Secs(byte value, byte seconds) {
        return (byte) ((double) value / ((double) TPS * seconds));
    }

    public static short reduceTicks2Secs(short value, short seconds) {
        return (short) ((double) value / ((double) TPS * seconds));
    }

    public static int reduceTicks2Secs(int value, int seconds) {
        return (int) ((double) value / ((double) TPS * seconds));
    }

    public static long reduceTicks2Secs(long value, long seconds) {
        return (long) ((double) value / ((double) TPS * seconds));
    }

    public static float reduceTicks2Secs(float value, float seconds) {
        return (float) ((double) value / ((double) TPS * seconds));
    }

    public static double reduceTicks2Secs(double value, double seconds) {
        return value / ((double) TPS * seconds);
    }

    /**
     * Check if the game is paused.
     *
     * @return true if paused.
     * @see Screen#doesPauseGame()
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isPaused() {
        if (instance.world == null || instance.loadedGame == null || !instance.loaded) return true;

        var screen = instance.getCurrentScreen();
        return screen != null && screen.doesPauseGame();
    }

    public static URL getJarUrl() {
        return BubbleBlaster.class.getProtectionDomain().getCodeSource().getLocation();
    }

    public static LibraryJar getGameJar() {
        if (jar == null) {
            var jarUrl = BubbleBlaster.getJarUrl();
            if  (!Objects.equals(jarUrl.getProtocol(), "libraryjar")) {
                jar = new GameJar(jarUrl);
            } else {
                jar = new LibraryJar(jarUrl);
            }
        }
        return jar;
    }

    public static Version getFabricLoaderVersion() {
        return FABRIC_LOADER_VERSION.get();
    }

    public static Version getGameVersion() {
        return GAME_VERSION.get();
    }

    private void onKeyPress(int keyCode, boolean holding) {
        final var loadedGame = this.loadedGame;

        if (loadedGame != null) {
            final var world = loadedGame.getWorld();

            Player player = world.getPlayer();
            if (this.canExecuteDevCommands()) {
                this.executeDevCommand(keyCode, world, player, loadedGame);
            }
        }

        if (this.canExecuteDevCommands() && keyCode == Keys.F8 && !holding) {
            this.renderer.triggerScissorLog();
        }
    }

    private void executeDevCommand(int keyCode, World world, Player player, LoadedGame loadedGame) {
        switch (keyCode) {
            case Keys.F1 -> {
                LOGGER.warn("Triggering developer command: TRIGGER_BLOOD_MOON");
                world.getGameplayStorage().get(NAMESPACE).putBoolean(DataKeys.BLOOD_MOON_ACTIVE, true);
            }
            case Keys.F3 -> {
                LOGGER.warn("Triggering developer command: SELF_DESTRUCT");
                player.destroy();
            }
            case Keys.F4 -> {
                LOGGER.warn("Triggering developer command: LEVEL_UP");
                player.levelUp();
            }
            case Keys.F5 -> {
                LOGGER.warn("Triggering developer command: SCORE_1000");
                player.awardScore(1000);
            }
            case Keys.F6 -> {
                LOGGER.warn("Triggering developer command: RESET_HP");
                DevCommands.resetHealth(loadedGame);
            }
            case Keys.F7 -> {
                LOGGER.warn("Triggering developer command: GLITCH");
                this.isGlitched = true;
            }
        }
    }

    private void onMouseClick(int x, int y, int button, int clicks) {
        var loadedGame = this.loadedGame;
        if (this.canExecuteDevCommands()) {
            if (loadedGame != null && button == Buttons.LEFT) {
                if (GameInput.isKeyDown(Keys.F1)) {
                    Objects.requireNonNull(loadedGame.getGamemode().getPlayer()).teleport(x, y);
                }
            }
        }
    }

    public URL getGameFile() {
        return this.gameFile;
    }

    public Activity getActivity() {
        return this.discordRpc.getActivity();
    }

    public static void setActivity(Supplier<Activity> activity) {
        instance.discordRpc.setActivity(activity);
    }

    private void initialGameTick() {

    }

    private void initDiscord() {

    }

    private boolean outOfMemory(OutOfMemoryError error) {
        System.gc();

        if (this.screen instanceof OutOfMemoryScreen) {
            this.crash(error);
            return true;
        }

        if (this.world != null && !this.world.isSaving()) {
            try {
                this.world.save();
            } catch (OutOfMemoryError anotherError) {
                System.gc();
            } catch (Throwable throwable) {
                this.crash(throwable);
            }
        }

        this.world.annihilate();
        this.world = null;
        this.worldRenderer = null;
        this.player = null;
        System.gc();

        this.showScreen(new OutOfMemoryScreen());
        return false;
    }

    private BitmapFont loadFontInternally(Identifier location) {
        return this.loadFont(Gdx.files.internal("assets/" + location.location() + "/fonts/" + location.path() + ".ttf"), 14);
    }

     public BitmapFont loadFont(FileHandle handle, int size) {
        if (!BubbleBlaster.isOnRenderingThread()) {
            BitmapFont bitmapFont = BubbleBlaster.invokeAndWait(() -> this.loadFont(handle, size));
            if (bitmapFont == null) throw new FontLoadException("Render call failed");
            return bitmapFont;
        }
        var generator = new FreeTypeFontGenerator(handle);
        var parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.flip = true;
        parameter.size = size;
        var font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        return font;
    }

     public BitmapFont loadFont(Identifier id) {
        return this.loadFont(id, 12);
    }

     public BitmapFont loadFont(Identifier id, int size) {
        return this.loadFont(BubbleBlaster.resource(id.mapPath(path -> "fonts/" + path + ".ttf")), size);
    }

    ////////////////////////
    //     Game flags     //
    ////////////////////////

    public void loadFonts() {
        try {
            this.sansFont = this.loadFont(BubbleBlaster.id("noto_sans/noto_sans_regular"), 14);
            this.logoFont = this.loadFont(BubbleBlaster.id("chicle"));
            this.pixelFont = this.loadFont(BubbleBlaster.id("pixel"));
            this.monospaceFont = this.loadFont(BubbleBlaster.id("roboto/roboto_mono_regular"));
        } catch (FontLoadException e) {
            this.crash(e);
        }
    }

    public void gcThread() {
    }

    /**
     * Stops game-thread.
     */
    @RequiresNonNull({"ticking", "rendering", "gcThread"})
    public synchronized void stop() {
    }

    public void onClose() {
        // Shut-down game.
        LOGGER.info("Shutting down Bubble Blaster");

        this.discordRpc.stop();
        this.garbageCollector.shutdown();

        tickingThread.interrupt();

        final var loadedGame = this.loadedGame;
        if (loadedGame != null) {
            loadedGame.end();
        }

        final var world = this.world;
        if (world != null) {
            world.close();
        }

        SoundInstance.stopAll();

        try {
            tickingThread.join(1000);
            this.discordRpc.join();
        } catch (Exception e) {
            LOGGER.warn("Failed to stop threads:", e);
            BubbleBlaster.annihilate();
        }
        this.checkForExitEvents();
    }

    /**
     * Annihilates the game process.
     * Used when the game can't close normally.
     */
    private static void annihilate() {
        Runtime.getRuntime().halt(1);
    }

    public int getScaledWidth() {
        return this.getWidth();
    }

    public int getScaledHeight() {
        return this.getHeight();
    }

    private void checkForExitEvents() {
        LifecycleEvents.GAME_EXIT.factory().onExit(this);
    }

    /**
     * Todo: implement save loading.
     *
     * @param saveName ...
     */
    @SuppressWarnings("EmptyMethod")
    @Deprecated
    public void loadSave(String saveName) {
        try {
            this.loadGame(GameSave.fromFile(new File(GameFolders.SAVES_DIR, saveName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Todo: implement save loading.
     *
     * @param save save to load
     */
    @Deprecated
    public void loadSave(GameSave save) {
        try {
            this.loadGame(save);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Todo: implement save creation and loading..
     *
     * @param saveName ...
     */
    @Beta
    @SuppressWarnings("EmptyMethod")
    public void createAndLoadSave(String saveName) {

    }

    /**
     * Display a new scene.
     *
     * @param scene the scene to display
     * @return if changing the scene was successful.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean showScreen(@Nullable Screen scene) {
        return this.showScreen(scene, false);
    }

    /**
     * Display a new scene.
     *
     * @param screen the scene to display
     * @return if changing the scene was successful.
     */
    public boolean showScreen(@Nullable Screen screen, boolean force) {
        Screen oldScreen = this.screen;
        if (oldScreen instanceof InternalScreen internalScreen) {
            if (oldScreen.close(screen)) return false;
        } else if (!force && oldScreen != null && (oldScreen.close(screen) || ScreenEvents.CLOSE.factory().onClose(oldScreen).isCanceled())) {
            return false;
        } else if (force && oldScreen != null) {
            ScreenEvents.FORCE_CLOSE.factory().onForceClose(this.screen);
        }
        Screen newScreen = screen;
        if (newScreen == null && this.isInMainMenus())
            newScreen = new TitleScreen();

        if (force) {
            ScreenEvents.FORCE_OPEN.factory().onOpen(newScreen);
        } else {
            ValueEventResult<Screen> result = ScreenEvents.OPEN.factory().onOpen(newScreen);
            newScreen = result.isInterrupted() ? result.getValue() : newScreen;
            if (result.isCanceled()) return false;
        }

        if (newScreen == null && this.isInMainMenus())
            newScreen = new TitleScreen();

        this.getGameWindow().setCursor(newScreen != null ? newScreen.getDefaultCursor() : this.getDefaultCursor());

        if (newScreen != null) {
            ScreenEvents.INIT.factory().onInit(newScreen);
            newScreen.init(this.getWidth(), this.getHeight());
        } else if (BubbleBlasterConfig.DEBUG_LOG_SCREENS.get()) {
            LOGGER.debug("Showing <<NO-SCENE>>");
        }
        this.screen = newScreen;
        return true;
    }

    @Nullable
    public Screen getCurrentScreen() {
        return this.screen;
    }

    /**
     * Load the world.
     *
     * @deprecated use the one with the seed instead.
     */
    @Deprecated
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void createGame() {
        this.createGame(512L);
    }

    /**
     * Load the world.
     *
     * @param seed generator seed
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void createGame(long seed) {
        this.createGame(seed, Gamemodes.NORMAL.get());
    }

    /**
     * Create a new saved game.
     *
     * @param seed     generator seed.
     * @param gamemode game mode to use.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void createGame(long seed, Gamemode gamemode) {
        this.createGame(seed, gamemode, Difficulty.NORMAL);
    }

    /**
     * Create a new saved game.
     *
     * @param seed     generator seed.
     * @param gamemode game mode to use.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void createGame(long seed, Gamemode gamemode, Difficulty difficulty) {
        this.startGame(seed, gamemode, difficulty, GameSave.fromFile(new File(GameFolders.SAVES_DIR, "save")), true);
    }

    /**
     * Loads the default saved game.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void loadGame() throws IOException {
        this.loadGame(GameSave.fromFile(new File(GameFolders.SAVES_DIR, "save")));
    }

    /**
     * Load a saved game.
     *
     * @param save the game save to load.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void loadGame(GameSave save) throws IOException {
        var seed = save.getSeed();
        var gamemode = save.getGamemode();
        var difficulty = save.getDifficulty();
        this.startGame(seed, gamemode, difficulty, save, false);
    }

    /**
     * Start the world.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void startGame(long seed, Gamemode gamemode, Difficulty difficulty, GameSave save, boolean create) {
        // Start loading.
        var screen = new MessengerScreen();

        // Show world loader screen.
        this.showScreen(screen);
        try {
            var directory = save.getDirectory();
            if (create && directory.exists()) {
                org.apache.commons.io.FileUtils.deleteDirectory(directory);
            }

            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Creating save folder failed.");
                }
            }

            var world = this.world = new World(save, gamemode, difficulty, seed);

            if (create) {
                screen.setDescription("Preparing creation");
                world.prepareCreation(save);
            } else {
                screen.setDescription("Loading data...");
                gamemode = Gamemode.loadState(save, screen.getMessenger());
            }

            if (create) {
                screen.setDescription("Initializing world");
                world.firstInit(screen.getMessenger());
                screen.setDescription("Saving initialized save");
                world.save(save, screen.getMessenger());
            } else {
                world.load(save, screen.getMessenger());
            }

            var loadedGame = new LoadedGame(save, this.world);
            loadedGame.start();

            this.loadedGame = loadedGame;
        } catch (Throwable t) {
            LOGGER.error("Bubble Blaster failed to launch", t);
            var crashLog = new CrashLog("Game save being loaded", t);
            crashLog.add("Save Directory", save.getDirectory());
            crashLog.add("Current Description", screen.getDescription());
            crashLog.add("Create Flag", create);
            crashLog.add("Seed", seed);
            crashLog.add("Gamemode", Registries.GAMEMODES.getKey(gamemode));
            BubbleBlaster.crash(crashLog.createCrash());
            return;
        }

        this.menuMusic.stop();

        BubbleBlaster.getInstance().showScreen(null);

        this.gameplayMusic.play();
    }

    /**
     * Quit world and loaded game.
     */
    public void quitLoadedGame() {
        var loadedGame = this.getLoadedGame();
        if (loadedGame != null) {
            loadedGame.end();
        }

        this.loadedGame = null;

        this.showScreen(new TitleScreen());
    }

    /**
     * Checks if the current thread is the main thread.
     *
     * @return true if the method is called on the main thread.
     */
    public static boolean isOnTickingThread() {
        return Thread.currentThread() == tickingThread;
    }

    /**
     * Checks if the current thread is the rendering thread.
     *
     * @return true if the method is called on the rendering thread.
     */
    public static boolean isOnRenderingThread() {
        return Thread.currentThread().getId() == renderingThread.getId();
    }

    /**
     * Checks if the world is active.
     * Note that this will return true even if the game is paused.
     *
     * @return true if world is active, even if game is paused.
     */
    public boolean isInGame() {
        return this.getLoadedGame() != null && this.world != null;
    }

    /**
     * Checks if the world isn't running. Aka, is in menus like the title screen.
     *
     * @return true if in main menus.
     */
    public boolean isInMainMenus() {
        return this.getLoadedGame() == null && this.world == null;
    }

    @NotNull
    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    /////////////////////////
    //     Loaded Game     //
    /////////////////////////
    public @Nullable LoadedGame getLoadedGame() {
        return this.loadedGame;
    }

    public @Nullable World getWorld() {
        return this.world;
    }

    public @Nullable GameSave getCurrentSave() {
        var loadedGame = this.getLoadedGame();
        if (loadedGame != null) {
            return loadedGame.getGameSave();
        }
        return null;
    }

    ///////////////////
    //     Fonts     //
    ///////////////////
    public BitmapFont getSansFont() {
        return this.sansFont;
    }

    public BitmapFont getMonospaceFont() {
        return this.monospaceFont;
    }

    public BitmapFont getPixelFont() {
        return this.pixelFont;
    }

    public BitmapFont getLogoFont() {
        return this.logoFont;
    }

    public Cursor.SystemCursor getTextCursor() {
        return Cursor.SystemCursor.Ibeam;
    }

    public Cursor.SystemCursor getPointerCursor() {
        return Cursor.SystemCursor.Arrow;
    }

    public Cursor.SystemCursor getDefaultCursor() {
        return Cursor.SystemCursor.Arrow;
    }

    public String getFontName() {
        return this.fontName;
    }

    ///////////////////////
    //     Rendering     //
    ///////////////////////
    @NotNull
    public RenderSettings getRenderSettings() {
        return this.renderSettings;
    }

    public WorldRenderer getWorldRenderer() {
        return this.worldRenderer;
    }

    ////////////////////
    //     Bounds     //
    ////////////////////
    public Rectangle getGameBounds() {
        return new Rectangle(0, 0, this.getWidth(), this.getHeight());
    }

    ////////////////////////
    //     Game flags     //
    ////////////////////////
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Loads the game.
     *
     * @param mainProgress main loading progress.
     */
    private void load(ProgressMessenger mainProgress) {

    }

    /**
     * Prepares the game.
     */
    private void prepare() {

    }

    /**
     * Prepares the player.
     */
    private void preparePlayer() {

    }

    /**
     * Active game glitch.
     */
    public void glitch() {
        this.isGlitched = true;
    }

    /**
     * Creates a player.
     *
     * @return the created player.
     */
    private Player createPlayer() {
        if (this.world != null) {
            return new Player(this.world);
        } else {
            throw new IllegalStateException("Creating a player while world is not loaded.");
        }
    }

    /**
     * @return the amount create main loading steps.
     */
    private int getMainLoadingSteps() {
        return 0;
    }

    @SuppressWarnings("DuplicatedCode")
    private void ticking() {
        var tickCap = 1000.0 / (double) TPS;
        var tickTime = 0d;
        var gameFrameTime = 0d;
        var ticksPassed = 0;

        double time = System.currentTimeMillis();

        this.initialGameTick();

        try {
            while (this.running) {
                try {
                    var canTick = false;

                    double time2 = System.currentTimeMillis();
                    var passed = time2 - time;
                    gameFrameTime += passed;
                    tickTime += passed;

                    time = time2;

                    while (gameFrameTime >= tickCap) {
                        gameFrameTime -= tickCap;

                        canTick = true;
                    }

                    if (canTick) {
                        ticksPassed++;
                        try {
                            this.gameTick();
                        } catch (Throwable t) {
                            var crashLog = new CrashLog("Game being ticked.", t);
                            BubbleBlaster.crash(crashLog.createCrash());
                        }
                    }

                    this.lastTickMs = System.currentTimeMillis();

                    if (tickTime >= 1000.0d) {
                        this.currentTps = ticksPassed;
                        ticksPassed = 0;
                        tickTime = 0;
                    }
                } catch (OutOfMemoryError error) {
                    if (this.outOfMemory(error)) return;
                }
            }
        } catch (Throwable t) {
            var crashLog = new CrashLog("Running game loop.", t);
            BubbleBlaster.crash(crashLog.createCrash());
        }
    }

    /**
     *
     */
    private void gameTick() {
        if (this.playerController != null) {
            this.playerController.tick();
        }

        if (this.isGlitched && BubbleBlasterConfig.ENABLE_ANNOYING_EASTER_EGGS.get()) {
            this.glitchRenderer.tick();
            return;
        }

        if (this.isLoaded()) {
            TickEvents.PRE_TICK_GAME.factory().onTickGame(this);
        }

        final var world = this.world;
        final var loadedGame = this.loadedGame;
        final var player = this.player;
        if (world != null && loadedGame != null && !BubbleBlaster.isPaused()) {
            final var gamemode = world.getGamemode();
            if (gamemode != null && player != null && player.getLevel() > 255)
                BubbleBlaster.getInstance().glitch();

            TickEvents.PRE_TICK_WORLD.factory().onTickWorld(world);
            world.tick();
            TickEvents.POST_TICK_WORLD.factory().onTickWorld(world);
            TickEvents.PRE_TICK_LOADED_GAME.factory().onTickLoadedGame(loadedGame);
            loadedGame.tick();
            TickEvents.POST_TICK_LOADED_GAME.factory().onTickLoadedGame(loadedGame);
        }

        final var screen = this.getCurrentScreen();
        if (screen != null)
            screen.tick();

        if (this.ticker.advance() == 40) {
            this.ticker.reset();
            if (this.isLoaded())
                this.tickRichPresence(player);
        }

        BubbleBlaster.ticks++;

        // Call tick event.
        if (this.isLoaded()) {
            TickEvents.POST_TICK_GAME.factory().onTickGame(this);
        }
    }

    private void tickRichPresence(@Nullable Player player) {
        if (this.isInMainMenus())
            BubbleBlaster.setActivity(() -> {
                var activity = new Activity();
                activity.setState("In the menus");
                return activity;
            });
        else if (this.isInGame())
            BubbleBlaster.setActivity(() -> {
                var activity = new Activity();
                activity.setState("In-Game");
                if (player != null) {
                    var score = player.getScore();
                    activity.setDetails("Score: " + (int) score);
                } else {
                    activity.setDetails("?? ERROR ??");
                }
                return activity;
            });
        else
            BubbleBlaster.setActivity(() -> {
                var activity = new Activity();
                activity.setState("Is nowhere to be found");
                return activity;
            });
    }

    private void onFirstFrame() {
        this.window.finalSetup();
    }

    /**
     * @param renderer the renderer to render the game with.
     * @param mouseX current mouse X position.
     * @param mouseY current mouse Y position.
     */
    private void renderGame(Renderer renderer, int mouseX, int mouseY, float frameTime) {
        // Call to game world renderering.
        // Get field and set local variable. For multithreaded null-safety.
        @Nullable Screen screen = this.getCurrentScreen();
        @Nullable World world = this.world;
        @Nullable WorldRenderer worldRenderer = this.worldRenderer;

        // Post event before rendering the game.
        RenderEvents.RENDER_GAME_BEFORE.factory().onRenderGameBefore(this, renderer, frameTime);

        // Handle music
        if (!this.gameplayMusic.isPaused()) {
            this.gameplayMusic.update(Gdx.graphics.getDeltaTime());
        } else if (!this.menuMusic.isPaused()) {
            this.menuMusic.update(Gdx.graphics.getDeltaTime());
        }

        // Render world.
        this.profiler.section("Render World", () -> {
            if (world != null && worldRenderer != null) {
                boolean enabledBlur = false;
                if (screen != null) enabledBlur = renderer.enableBlur(15);
                RenderEvents.RENDER_WORLD_BEFORE.factory().onRenderWorldBefore(world, worldRenderer, renderer);
                worldRenderer.render(renderer, mouseX, mouseY, frameTime);
                RenderEvents.RENDER_WORLD_AFTER.factory().onRenderWorldAfter(world, worldRenderer, renderer);
                if (enabledBlur) renderer.disableBlur();
            }
        });

        // Render screen.
        this.profiler.section("Render Screen", () -> {
            if (screen != null) {
                RenderEvents.RENDER_SCREEN_BEFORE.factory().onRenderScreenBefore(screen, renderer);
                screen.render(this, renderer, mouseX, mouseY, frameTime);
                RenderEvents.RENDER_SCREEN_AFTER.factory().onRenderScreenAfter(screen, renderer);
            }
            if (world != null && worldRenderer != null) {
                renderer.fillEffect(0, 0, BubbleBlaster.getInstance().getWidth(), 3);
            }
        });

        // Post render.
        this.profiler.section("Post Render", () -> this.postRender(renderer));

        // Post event after rendering the game.
        RenderEvents.RENDER_GAME_AFTER.factory().onRenderGameAfter(this, renderer, frameTime);
    }

    private void postRender(Renderer renderer) {
        if (this.fadeIn) {
            final var timeDiff = System.currentTimeMillis() - this.fadeInStart;
            if (timeDiff <= this.fadeInDuration) {
                int clamp = Mth.clamp((int) (255 * (1f - timeDiff / this.fadeInDuration)), 0, 255);
                renderer.fill(0, 0, this.getWidth(), this.getHeight(), Color.BLACK.withAlpha(clamp));
            } else {
                this.fadeIn = false;
            }
        }
    }

    /**
     * Starts loading the game.
     */
    public void startLoading() {
        this.getGameWindow().init();
    }

    /**
     * @return get the default font.
     */
    public BitmapFont getFont() {
        return this.getSansFont();
    }

    public boolean hasScreenOpen() {
        return this.screen != null;
    }

    public void scheduleTick(Runnable task) {
        this.tasks.add(task);
    }

    private void log(String text) {
        // Todo: implement
    }

    /**
     * This method should be overridden when filters are used in the game.<br>
     * Should be overridden for enabling / disabling render filters.
     *
     * @return list create buffered image operations.
     */
    @NotNull
    public List<VfxEffect> getCurrentFilters() {
        return new ArrayList<>();
    }

    public void laodPlayerIntoWorld() {
        var player = this.createPlayer();
        this.input = player;
        this.playerController = new PlayerController(this.input);
        this.player = player;

        this.preparePlayer();
    }

    /////////////////////
    //     Getters     //
    /////////////////////
    public float getGameFrameTime() {
        return this.gameFrameTime;
    }

    public boolean isAntialiasEnabled() {
        return true;
    }

    public boolean isTextAntialiasEnabled() {
        return this.isAntialiasEnabled();
    }

    public GameWindow getGameWindow() {
        return this.window;
    }

    public Rectangle getBounds() {
        return new Rectangle(0, 0, this.window.getWidth(), this.window.getHeight());
    }

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    public int getFps() {
        return this.fps;
    }

    public int getCurrentTps() {
        return this.currentTps;
    }

    public Instant getLastTickTime() {
        return Instant.ofEpochMilli(this.lastTickMs);
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean isLoading() {
        return !this.loaded;
    }

    public boolean isStopping() {
        return this.stopping;
    }

    /**
     * Clean shutdown create the game. By setting the running flag to false, and stop flag to true.
     */
    public void shutdown() {
        this.stopping = true;
        this.running = false;
        this.window.dispose();
    }

    /**
     * Handler for game crash.
     *
     * @param crash the game crash.
     */
    public static void crash(@NotNull ApplicationCrash crash) {
        var crashLog = crash.getCrashLog();

        crashed = true;

        var overridden = false;
        try {
            GameEvents.CRASH.factory().onCrash(crash);
        } catch (Throwable t) {
            if (t instanceof VirtualMachineError e) throw e;
            LOGGER.error("Error occurred in 3rd party crash handling:", t);
        }

        if (!overridden) {
            File file = new File(dataDir,"game-crashes");
            if (!file.exists()) {
                try {
                    Files.createDirectories(file.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            crashLog.writeToFile(new File(file, crashLog.getDefaultFileName()));
            LOGGER.error(crashLog.toString());
        }

        BubbleBlaster.annihilate();
    }

    @EnsuresNonNull({"ticking"})
    @SuppressWarnings("Convert2Lambda")
    void windowLoaded() {
        this.running = true;

        tickingThread = new Thread(BubbleBlaster.this::ticking, "Ticker");
        tickingThread.start();

        this.garbageCollector = new GarbageCollector();

        BubbleBlaster.getLogger().info("Game threads started!");
    }

    public void crash(Throwable t) {
        var crashLog = new CrashLog("Unknown source", t);
        BubbleBlaster.crash(crashLog.createCrash());
    }

    public void finalizeSetup() {

    }

    public void fadeIn(float time) {
        this.fadeIn = true;
        this.fadeInStart = System.currentTimeMillis();
        this.fadeInDuration = time;
    }

    public void finish() {
        this.glitchRenderer = new GlitchRenderer(this);
        this.showScreen(new TitleScreen());
        this.loaded = true;

        this.afterLoading.values().forEach(Runnable::run);
        this.afterLoading.clear();
    }

    public long serializeSeed(String text) {
        var seedNr = Long.getLong(text);
        if (seedNr != null) {
            return seedNr;
        }

        long h = 0;
        long length = text.length() >> 1;
        for (var c : text.toCharArray()) {
            h = 31L * h + c;
        }
        return h;
    }

    public void saveAndQuit() {
        if (this.world == null)
            throw new IllegalStateException("World isn't loaded.");

        this.world.save();
        this.quitLoadedGame();
    }

    public GlobalSaveData getGlobalData() {
        return this.globalSaveData;
    }

    public BitmapFont getBitmapFont() {
        return new BitmapFont();
    }

    public boolean isFocused() {
        return this.focused;
    }

    public Renderer getRenderer() {
        return this.currentRenderer;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean keyPress(int keycode) {
        Screen screen = this.getCurrentScreen();
        World world = this.world;

        if (keycode == Keys.F12) {
            DEBUG_GUI_OPEN.set(!DEBUG_GUI_OPEN.get());
            return true;
        } else if (keycode == Keys.F11 && this.world == null) {
            this.window.toggleFullscreen();
            return true;
        } else if (keycode == Keys.F2) {
            Screenshot screenshot = Screenshot.take();
            this.notifications.notify(
                    Notification.builder("Screenshot saved!", screenshot.fileHandle().name())
                            .subText("Screenshot Manager")
                            .build());
            return true;
        } else if (world != null) {
            if (this.keyPressEnv(world, keycode)) return true;
        }

        if (screen != null) {
            return screen.keyPress(keycode);
        } else if (keycode == Keys.ESCAPE && world != null && world.isAlive()) {
            BubbleBlaster.getInstance().showScreen(new PauseScreen());
            return true;
        }
        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    private boolean keyPressEnv(World world, int keycode) {
        Player player = this.player;
        boolean isDev = this.canExecuteDevCommands();

        if (this.isInGame() && player != null) {
            if (keycode == Keys.UP) player.forward(true);
            if (keycode == Keys.DOWN) player.backward(true);
            if (keycode == Keys.LEFT) player.rotateLeft(true);
            if (keycode == Keys.RIGHT) player.rotateRight(true);

            if (keycode == Keys.SPACE) player.shoot();
            if (keycode == Keys.CONTROL_LEFT) player.boost();
        }

        if (isDev && keycode == Keys.F10) {
            world.triggerBloodMoon();
            return true;
        } else if (keycode == Keys.SLASH && !this.hasScreenOpen()) {
            BubbleBlaster.getInstance().showScreen(new CommandScreen());
            return true;
        }

        return false;
    }

    private boolean canExecuteDevCommands() {
        return BubbleBlaster.debugMode || FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean keyRelease(int keycode) {
        @Nullable Screen screen = this.getCurrentScreen();
        if (screen != null) return screen.keyRelease(keycode);

        if (this.isInGame() && this.player != null) {
            if (keycode == Keys.UP) this.player.forward(false);
            if (keycode == Keys.DOWN) this.player.backward(false);
            if (keycode == Keys.LEFT) this.player.rotateLeft(false);
            if (keycode == Keys.RIGHT) this.player.rotateRight(false);
        }

        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean charType(char character) {
        @Nullable Screen screen = this.getCurrentScreen();
        if (screen != null) return screen.charType(character);

        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean mousePress(int screenX, int screenY, int pointer, int button) {
        @Nullable Screen screen = this.getCurrentScreen();
        if (screen != null) return screen.mousePress(screenX, screenY, button);

        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean mouseRelease(int screenX, int screenY, int pointer, int button) {
        @Nullable Screen screen = this.getCurrentScreen();
        if (screen != null) return screen.mouseRelease(screenX, screenY, button);

        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean mouseDragged(int oldX, int oldY, int newX, int newY, int pointer, int button) {
        Screen screen = this.getCurrentScreen();
        if (screen != null) screen.mouseDrag(oldX, oldY, newX, newY, button);

        return true;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean mouseMove(int screenX, int screenY) {
        @Nullable Screen screen = this.getCurrentScreen();
        if (screen != null) screen.mouseMove(screenX, screenY);

        return true;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean mouseWheel(int x, int y, float amountX, float amountY) {
        Screen screen = this.getCurrentScreen();
        if (screen != null) return screen.mouseWheel(x, y, amountY);

        return false;
    }

    @Override
    public void fillInCrash(CrashLog crashLog) {
        World world = this.world;
        if (world != null) {
            world.fillInCrash(crashLog);
        }

        Screen screen = this.getCurrentScreen();
        if (screen != null) {
            screen.fillInCrash(crashLog);
        }
    }

    public boolean isCollisionShapesShown() {
        return SHOW_COLLISIONS_SHAPES.get();
    }

    protected static class BootOptions {

        public BootOptions() {

        }

        public BootOptions tps(int tps) {
            return this;
        }
    }
}
