package com.ultreon.bubbles;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.CpuSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.annotations.Beta;
import com.google.common.base.Suppliers;
import com.google.common.collect.Queues;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.common.GameFolders;
import com.ultreon.bubbles.common.exceptions.FontLoadException;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.debug.DebugRenderer;
import com.ultreon.bubbles.debug.Profiler;
import com.ultreon.bubbles.debug.ThreadSection;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.environment.EnvironmentRenderer;
import com.ultreon.bubbles.event.v1.*;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.*;
import com.ultreon.bubbles.input.GameInput;
import com.ultreon.bubbles.media.SoundInstance;
import com.ultreon.bubbles.media.SoundPlayer;
import com.ultreon.bubbles.mod.loader.GameJar;
import com.ultreon.bubbles.mod.loader.LibraryJar;
import com.ultreon.bubbles.notification.Notification;
import com.ultreon.bubbles.notification.Notifications;
import com.ultreon.bubbles.player.InputController;
import com.ultreon.bubbles.player.PlayerController;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.*;
import com.ultreon.bubbles.render.font.FontInfo;
import com.ultreon.bubbles.render.font.FontStyle;
import com.ultreon.bubbles.render.font.Thickness;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.screen.*;
import com.ultreon.bubbles.render.gui.screen.splash.SplashScreen;
import com.ultreon.bubbles.resources.ResourceFileHandle;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.libs.collections.v0.maps.OrderedHashMap;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.Mth;
import com.ultreon.libs.commons.v0.ProgressMessenger;
import com.ultreon.libs.commons.v0.size.IntSize;
import com.ultreon.libs.commons.v0.util.FileUtils;
import com.ultreon.libs.crash.v0.ApplicationCrash;
import com.ultreon.libs.crash.v0.CrashLog;
import com.ultreon.libs.registries.v0.Registry;
import com.ultreon.libs.resources.v0.Resource;
import com.ultreon.libs.resources.v0.ResourceManager;
import com.ultreon.libs.translations.v0.LanguageManager;
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
import org.lwjgl.system.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.earlygrey.shapedrawer.ShapeDrawer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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

import static org.apache.logging.log4j.MarkerManager.getMarker;
import static org.lwjgl.glfw.GLFW.glfwInit;

/**
 * The Bubble Blaster game main class.
 *
 * @since 0.0.1
 */
@ParametersAreNonnullByDefault
@SuppressWarnings({"ResultOfMethodCallIgnored", "unused", "RedundantSuppression"})
public final class BubbleBlaster extends ApplicationAdapter implements CrashFiller {
    public static final int TPS = 40; // Why tf was this set to 40???
    public static final String NAMESPACE = "bubbleblaster";

    // Logger.
    public static final Logger LOGGER = LoggerFactory.getLogger("Generic");
    public static final Instant BOOT_TIME = Instant.now();

    private static final WatchManager WATCHER = new WatchManager(new ConfigurationScheduler("File Watcher"));
    private static final ImBoolean SHOW_INFO_WINDOW = new ImBoolean(false);
    private static final ImBoolean SHOW_FPS_GRAPH = new ImBoolean(false);
    private static final ImBoolean SHOW_ENTITY_MODIFIER = new ImBoolean(false);
    private static final ImBoolean SHOW_GUI_MODIFIER = new ImBoolean(false);
    // Modes
    private static boolean debugMode;
    private static boolean devMode;
    // Initial game information / types.
    private static ClassLoader classLoader;
    private static SoundPlayer soundPlayer;
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
    private ScreenManager screenManager;
    private RenderSettings renderSettings;
    private DiscordRPC discordRpc;
    // Rendering
    private DebugRenderer debugRenderer;
    private EnvironmentRenderer environmentRenderer;
    // Managers.
    private TextureManager textureManager;
    // Randomizers.
    private final Random random = new Random();

    // Misc
    private final BufferedImage background = null;
    private final Object rpcUpdateLock = new Object();
    private final Ticker ticker = new Ticker();

    // Utility objects.
    public InputController input;

    // Environment
    @Nullable
    public Environment environment;

    // Player entity
    public Player player;
    BufferedImage cachedImage;

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
    private Thread renderingThread;
    private Thread tickingThread;
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
    private final ImBoolean debugGuiOpen = new ImBoolean(false);
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
    private final ImBoolean showDebugUtils = new ImBoolean(FabricLoader.getInstance().isDevelopmentEnvironment());
    private Renderer currentRenderer;
    private Renderer renderer;
    private final Map<UUID, Runnable> afterLoading = new OrderedHashMap<>();
    private long lastTickMs;
    private boolean isTickThreadDead;

    public BubbleBlaster() {
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();

        instance = this;
        notifications = new Notifications();
    }

    public static <T> T invokeAndWait(Supplier<T> func) {
        Thread thread = Thread.currentThread();
        if (isOnRenderingThread()) {
            return func.get();
        }

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
        if (isOnRenderingThread()) {
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
        if (isOnRenderingThread()) {
            func.run();
            return;
        }

        RENDER_CALLS.addLast(renderer -> func.run());
    }

    public static Instant getBootTime() {
        return BOOT_TIME;
    }

    public static void whenLoaded(UUID id, Runnable func) {
        if (!isOnRenderingThread()) {
            invokeAndWait(() -> whenLoaded(id, func));
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

    @Override
    public void create() {
        if (this.renderingThread == null) renderingThread = Thread.currentThread();

        // Pre-init ImGui
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Initialize ImGui
        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.getFonts().addFontDefault();

        long windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init("#version 150");

        // Set game input processor for LibGDX
        GameInput input = new GameInput();
        Gdx.input.setInputProcessor(input);

        // Set HiDpi mode
        HdpiUtils.setMode(HdpiMode.Pixels);

        // Create CPU sprite batch
        var batch = new CpuSpriteBatch();

        // Create a pixmap with a single white pixel
        var singlePxPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        singlePxPixmap.setColor(Color.rgb(0xffffff).toGdx());
        singlePxPixmap.drawPixel(0, 0);
        var singlePxTex = new Texture(singlePxPixmap);
        var pixel = new TextureRegion(singlePxTex);

        // Create shape drawer
        var shapes = new ShapeDrawer(batch, pixel);

        this.textureManager = TextureManager.instance();
        this.debugRenderer = new DebugRenderer(this);
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(true, this.getWidth(), this.getHeight()); // Set up the camera's projection matrix
        this.viewport = new ScreenViewport(camera);

        this.renderer = new Renderer(shapes, camera);

        // Set default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(new GameExceptions());
        Thread.currentThread().setUncaughtExceptionHandler(new GameExceptions());

        // Hook output for logger.
        System.setErr(new RedirectPrintStream(Level.ERROR, LogManager.getLogger("STDERR")));
        System.setOut(new RedirectPrintStream(Level.INFO, LogManager.getLogger("STDOUT")));

        // Assign instance.
        this.window = new GameWindow(new GameWindow.Properties("Bubble Blaster", 1280, 720).close(this::dispose)/*.fullscreen()*/);

        gameFile = BubbleBlaster.class.getProtectionDomain().getCodeSource().getLocation();

        // Prepare for game launch
        this.resourceManager = new ResourceManager("assets");
        this.renderSettings = new RenderSettings();

        // Invoke entry points.
        EntrypointUtils.invoke("main", ModInitializer.class, ModInitializer::onInitialize);

        DebugFormatters.initClass();

        // Prepare for loading.
        this.prepare();

        sansFont = this.loadBitmapFreeTypeFont(Gdx.files.internal("assets/bubbleblaster/fonts/noto_sans/noto_sans_regular.ttf"), 14);

        Bubbles.register();
        AmmoTypes.register();
        Entities.register();
        Fonts.register();
        SoundEvents.register();
        StatusEffects.register();
        Abilities.register();
        GameplayEvents.register();
        Gamemodes.register();
        TextureCollections.register();

        // Load game with loading screen.
        this.load(new ProgressMessenger(this::log, 1000));
        this.screenManager = createScreenManager();

        // Enable Discord RPC
        this.discordRpc = new DiscordRPC();

        setActivity(() -> {
            var activity = new Activity();
            activity.setState("Loading game.");
            return activity;
        });

        LOGGER.info("Discord RPC is initializing!");

        // Set environment renderer
        this.environmentRenderer = new EnvironmentRenderer();

        try {
            URL resource = getClass().getClassLoader().getResource(".resource-root");
            if (resource == null) {
                crash(new Exception("Game resource root not found!"));
                return;
            }
            var resourcePath = Path.of(resource.toURI()).getParent();
            LOGGER.debug("Valid game path: " + resourcePath);

            resourceManager.importPackage(resourcePath);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Set current work directory
        FileUtils.setCwd(GameFolders.DATA_DIR);

        // Logs directory creation.
        GameFolders.LOGS_DIR.mkdirs();

        // Font Name
        fontName = "Chicle Regular";

        // Register events.
        InputEvents.KEY_PRESS.listen(this::onKeyPress);
        InputEvents.MOUSE_CLICK.listen(this::onMouseClick);

        GameEvents.CLIENT_STARTED.factory().onClientStarted(this);

        // Start scene-manager.
        try {
            screenManager.start();
        } catch (Throwable t) {
            var crashLog = new CrashLog("Oops, game crashed!", t);
            crash(t);
        }

        // Request focus
        getGameWindow().requestUserAttention();
        getGameWindow().requestFocus();
    }

    @Override
    public void pause() {
        if (isInGame() && !(getCurrentScreen() instanceof PauseScreen)) {
            showScreen(new PauseScreen());
        }
    }

    @Override
    public void resume() {
        if (isInGame() && getCurrentScreen() instanceof PauseScreen) {
            showScreen(null);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.setToOrtho(true, width, height); // Set up the camera's projection matrix

        var screen = getCurrentScreen();
        if (screen != null) {
            screen.resize(width, height);
        }
    }

    @Override
    public void render() {
        if (Gdx.graphics.getFrameId() == 2) {
            this.firstRender();
        }

        tasks.forEach(Runnable::run);
        tasks.clear();

        camera.update();
        renderer.begin();
        currentRenderer = renderer;

        if (this.isLoaded()) {
            if (Instant.now().isAfter(this.getLastTickTime().plusSeconds(60)) && !this.isTickThreadDead) {
                this.markTickingDead();
                this.notifications.notify(new Notification("Game Ticking", "Game ticking hasn't happened in 60 secs!", "Watchdog", Duration.ofSeconds(5)));
            } else if (this.isTickThreadDead) {
                this.isTickThreadDead = false;
                this.notifications.notify(new Notification("Game Ticking", "Game ticking came back!", "Watchdog", Duration.ofSeconds(5)));
            }
        }

        try {
            GridPoint2 mousePos = GameInput.getPos();
            int mouseX = mousePos.x;
            int mouseY = mousePos.y;

            float deltaTime = Gdx.graphics.getDeltaTime();

            int size = RENDER_CALLS.size();
            for (int counter = 0; counter < size; counter++) {
                RENDER_CALLS.removeFirst().accept(renderer);
            }

            if (isGlitched) {
                glitchRenderer.render(renderer);
            } else {
                var filters = BubbleBlaster.instance.getCurrentFilters();

                profiler.section("renderGame", () -> this.renderGame(renderer, mouseX, mouseY, gameFrameTime));

                if (isDebugMode() || this.debugGuiOpen.get()) {
                    this.debugRenderer.render(this.renderer);
                }

                this.fps = Gdx.graphics.getFramesPerSecond();
            }

            this.notifications.render(this.renderer, mouseX, mouseY, deltaTime);

            if (this.showDebugUtils.get()) {
                this.renderImGui(this.renderer);
            }

            this.manualCrashOverlay.render(this.renderer, mouseX, mouseY, deltaTime);
        } catch (OutOfMemoryError error) {
            this.outOfMemory(error);
        }

        currentRenderer = null;
        renderer.end();
    }

    private void markTickingDead() {
    }

    private void firstRender() {
        window.setVisible(true);
        window.setFullscreen(true);
    }

    private void renderImGui(Renderer renderer) {
        // render 3D scene
        imGuiGlfw.newFrame();

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
                if (ImGui.beginMenu("Debug")) {
                    ImGui.menuItem("Entity Modifier", null, SHOW_ENTITY_MODIFIER, isInGame());
                    ImGui.menuItem("GUI Modifier", null, SHOW_GUI_MODIFIER);
                    ImGui.menuItem("Debug HUD", null, debugGuiOpen);
                    ImGui.endMenu();
                }
                ImGui.endMenuBar();
            }
            ImGui.end();
        }
        if (SHOW_INFO_WINDOW.get()) {
            showInfoWindow();
        }
        if (SHOW_GUI_MODIFIER.get()) {
            showGuiModifier(renderer);
        }
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    private void showGuiModifier(Renderer renderer) {
        Screen currentScreen = getCurrentScreen();
        GuiComponent exactWidgetAt = null;
        if (currentScreen != null) exactWidgetAt = currentScreen.getExactWidgetAt(Gdx.input.getX(), Gdx.input.getY());

        if (exactWidgetAt != null) {
            var bounds = exactWidgetAt.getBounds();
            renderer.setColor(Color.rgb(0xff0000));
            renderer.rectLine(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("Gui Utilities")) {
            ImGui.text("Screen: " + (currentScreen == null ? "null" : currentScreen.getClass().getSimpleName()));
            ImGui.text("Widget: " + (exactWidgetAt == null ? "null" : exactWidgetAt.getClass().getSimpleName()));
        }
        ImGui.end();
    }

    private void showInfoWindow() {
        Screen currentScreen = getCurrentScreen();
        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("Debug Info")) {
            ImGui.button("I'm a Button!");
            ImGui.text("Screen:" + (currentScreen == null ? "null" : currentScreen.getClass().getName()));
        }
        ImGui.end();
    }

    @Override
    @ApiStatus.Internal
    public void dispose() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
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
        ResourceManager.logger = getLogger("ResourceManager");
        Registry.dumpLogger = getLogger("RegistryDump");
        LanguageManager.INSTANCE.logger = getLogger("LanguageManager");

        // Get game-directory.
        final var defaultGameDir = new File(".");
        gameDir = !args.containsKey("gameDir") ? defaultGameDir : new File(args.get("gameDir"));
        debugMode = args.getExtraArgs().contains("--debug");
        devMode = FabricLoader.getInstance().isDevelopmentEnvironment();

        BubbleBlaster.classLoader = getClassLoader();

        String property = System.getProperty("user.home");
        dataDir = new File(getAppData(), "BubbleBlaster/");

        FileUtils.setCwd(dataDir);

        // Boot the game.
        BubbleBlaster.initEngine(BubbleBlaster.debugMode, BubbleBlaster.devMode);
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
        return switch (Platform.get()) {
            case WINDOWS -> System.getenv("APPDATA");
            case LINUX -> "~/.config/";
            case MACOSX -> "~/Library/Application Support/";
        };
    }

    public static void runLater(Runnable runnable) {
        instance.scheduleTask(runnable);
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

    public static boolean isDevMode() {
        return devMode;
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
        return (float) getInstance().getWidth() / 2;
    }

    public static float getMiddleY() {
        return (float) getInstance().getHeight() / 2;
    }

    public static Point2D getMiddlePoint() {
        return new Point2D.Double(getMiddleX(), getMiddleY());
    }

    ///////////////////
    //     Media     //
    ///////////////////
    public static SoundPlayer getAudioPlayer() {
        return soundPlayer;
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
        var currentScreen = getInstance().getCurrentScreen();
        return currentScreen != null && currentScreen.doesPauseGame();
    }

    public static void runOnMainThread(Runnable task) {
        if (instance.isOnTickingThread()) {
            task.run();
        } else {
            runLater(task);
        }
    }

    /**
     * Initialize the game engine.
     *
     * @param debugMode debug mode enables debugging utilities.
     * @param devMode   dev mode enabled development utilities.
     */
    public static void initEngine(boolean debugMode, boolean devMode) {
        BubbleBlaster.debugMode = debugMode;
        BubbleBlaster.devMode = devMode;
    }

    public static URL getJarUrl() {
        return BubbleBlaster.class.getProtectionDomain().getCodeSource().getLocation();
    }

    public static LibraryJar getGameJar() {
        if (jar == null) {
            var jarUrl = getJarUrl();
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
            final var environment = loadedGame.getEnvironment();

            if (keyCode == KeyEvent.VK_F1 && BubbleBlaster.isDevMode()) {
                environment.triggerBloodMoon();
            } else if (keyCode == KeyEvent.VK_F3 && BubbleBlaster.isDevMode()) {
                Objects.requireNonNull(environment.getPlayer()).destroy();
            } else if (keyCode == KeyEvent.VK_F4 && BubbleBlaster.isDevMode()) {
                Objects.requireNonNull(environment.getPlayer()).levelUp();
            } else if (keyCode == KeyEvent.VK_F5 && BubbleBlaster.isDevMode()) {
                Objects.requireNonNull(environment.getPlayer()).addScore(1000);
            } else if (keyCode == KeyEvent.VK_F6 && BubbleBlaster.isDevMode()) {
                var player = loadedGame.getGamemode().getPlayer();

                if (player != null) {
                    Objects.requireNonNull(player).setHealth(player.getMaxHealth());
                }
            }
        }
    }

    private void onMouseClick(int x, int y, int button, int clicks) {
        var loadedGame = this.loadedGame;
        if (isDevMode()) {
            if (loadedGame != null && button == Buttons.LEFT) {
                if (GameInput.isKeyDown(Keys.F1)) {
                    Objects.requireNonNull(loadedGame.getGamemode().getPlayer()).teleport(x, y);
                }
            }
        }
    }

    public URL getGameFile() {
        return gameFile;
    }

    public void updateRPC() {
//        DiscordRPC.discordUpdatePresence(presence);
    }

    public Activity getActivity() {
        return discordRpc.getActivity();
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

        if (screenManager.getCurrentScreen() instanceof OutOfMemoryScreen) {
            crash(error);
            return true;
        }

        if (this.environment != null && !this.environment.isSaving()) {
            try {
                this.environment.save();
            } catch (OutOfMemoryError anotherError) {
                System.gc();
            } catch (Throwable throwable) {
                this.crash(throwable);
            }
        }

        this.environment.annihilate();
        this.environment = null;
        this.environmentRenderer = null;
        this.player = null;
        System.gc();

        this.showScreen(new OutOfMemoryScreen());
        return false;
    }

    private BitmapFont loadFontInternally(Identifier location) {
        return loadBitmapFreeTypeFont(Gdx.files.internal("assets/" + location.location() + "/fonts/" + location.path() + ".ttf"), 14);
    }

    public FontInfo loadFont(Identifier fontId) {
        var identifier = new Identifier(fontId.location(), "fonts/" + fontId.path() + ".ttf");
        Resource resource = resourceManager.getResource(identifier);
        var handle = new ResourceFileHandle(resource);
        if (handle.exists()) {
            try {
                var regularFont = loadBitmapFreeTypeFont(handle);
                var builder = FontInfo.builder(fontId);
                builder.set(Thickness.REGULAR, FontStyle.PLAIN, regularFont);
                builder.set(Thickness.REGULAR, FontStyle.ITALIC, regularFont);
                builder.set(Thickness.BOLD, FontStyle.PLAIN, regularFont);
                builder.set(Thickness.BOLD, FontStyle.ITALIC, regularFont);
                return builder.build();
            } catch (Exception e) {
                throw new FontLoadException(e);
            }
        } else {
            var regularId = new Identifier(fontId.location(), "fonts/" + fontId.path() + "_regular.ttf");
            Resource regularRes = resourceManager.getResource(regularId);
            var regularHandle = new ResourceFileHandle(regularRes);
            if (regularHandle.exists()) {
                try {
                    var regularFont = loadBitmapFreeTypeFont(regularHandle);
                    var builder = FontInfo.builder(fontId);
                    builder.set(Thickness.REGULAR, FontStyle.PLAIN, regularFont);

                    for (var thickness : Thickness.values()) {
                        var thicknessId = new Identifier(fontId.location(), "fonts/" + fontId.path() + "_" + thickness.name().toLowerCase() + ".ttf");
                        Resource thicknessRes = resourceManager.getResource(thicknessId);
                        var thicknessHandle = new ResourceFileHandle(thicknessRes);
                        if (thicknessHandle.exists()) {
                            var thicknessFont = loadBitmapFreeTypeFont(thicknessHandle);
                            this.registerFont(thicknessFont);
                            builder.set(thickness, FontStyle.PLAIN, thicknessFont);

                            for (var style : FontStyle.values()) {
                                if (style == FontStyle.PLAIN) continue;
                                var thicknessStyleId = new Identifier(fontId.location(), "fonts/" + fontId.path() + "_" + thickness.name().toLowerCase() + "_" + style.name().toLowerCase() + ".ttf");
                                Resource thicknessStyleRes = resourceManager.getResource(thicknessStyleId);
                                var thicknessStyleHandle = new ResourceFileHandle(thicknessStyleRes);
                                if (thicknessStyleHandle.exists()) {
                                    var thicknessStyleFont = loadBitmapFreeTypeFont(thicknessStyleHandle);
                                    this.registerFont(thicknessStyleFont);
                                    builder.set(thickness, FontStyle.ITALIC, thicknessStyleFont);
                                }
                            }
                        }
                    }
                    return builder.build();
                } catch (Exception e) {
                    throw new FontLoadException(e);
                }
            } else {
                throw new FontLoadException("Font resource not found: " + identifier);
            }
        }
    }

    BitmapFont loadBitmapFreeTypeFont(FileHandle handle) {
        return loadBitmapFreeTypeFont(handle, 12);
    }

    BitmapFont loadBitmapFreeTypeFont(FileHandle handle, int size) {
        if (!isOnRenderingThread()) {
            BitmapFont bitmapFont = invokeAndWait(() -> loadBitmapFreeTypeFont(handle, size));
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

    BitmapFont loadBitmapFreeTypeFont(Identifier id) {
        return loadBitmapFreeTypeFont(id, 12);
    }

    BitmapFont loadBitmapFreeTypeFont(Identifier id, int size) {
        return loadBitmapFreeTypeFont(new ResourceFileHandle(id.mapPath(path -> "fonts/" + path + ".ttf")), size);
    }

    ////////////////////////
    //     Game flags     //
    ////////////////////////

    public void loadFonts() {
        try {
            this.sansFont = loadBitmapFreeTypeFont(id("noto_sans/noto_sans_regular"), 14);
            this.logoFont = loadBitmapFreeTypeFont(id("chicle"));
            this.pixelFont = loadBitmapFreeTypeFont(id("pixel"));
            this.monospaceFont = loadBitmapFreeTypeFont(id("roboto/roboto_mono_regular"));
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

        this.tickingThread.interrupt();

        final var loadedGame = this.loadedGame;
        if (loadedGame != null) {
            loadedGame.shutdown();
        }

        final var environment = this.environment;
        if (environment != null) {
            environment.shutdown();
        }

        SoundInstance.stopAll();

        try {
            this.tickingThread.join(1000);
            this.discordRpc.join();
        } catch (Exception e) {
            LOGGER.warn("Failed to stop threads:", e);
            this.annihilate();
        }
        checkForExitEvents();
    }

    /**
     * Annihilates the game process.
     * Used when the game can't close normally.
     */
    private void annihilate() {
        Runtime.getRuntime().halt(1);
    }

    public int getScaledWidth() {
        return getWidth();
    }

    public int getScaledHeight() {
        return getHeight();
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
            loadGame(GameSave.fromFile(new File(GameFolders.SAVES_DIR, saveName)));
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
            loadGame(save);
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
     * @param screen the screen to switch to.
     */
    public void showScreen(@Nullable Screen screen) {
        this.screenManager.displayScreen(screen);
    }

    /**
     * @param screen the screen to switch to.
     * @param force  whether to force switching or not.
     */
    public void showScreen(Screen screen, boolean force) {
        this.screenManager.displayScreen(screen, force);
    }

    public @Nullable Screen getCurrentScreen() {
        return this.screenManager.getCurrentScreen();
    }

    /**
     * Load the game environment.
     *
     * @deprecated use the one with the seed instead.
     */
    @Deprecated
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void createGame() {
        createGame(512L);
    }

    /**
     * Load the game environment.
     *
     * @param seed generator seed
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void createGame(long seed) {
        createGame(seed, Gamemodes.MODERN.get());
    }

    /**
     * Create a new saved game.
     *
     * @param seed     generator seed.
     * @param gamemode game mode to use.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void createGame(long seed, Gamemode gamemode) {
        startGame(seed, gamemode, GameSave.fromFile(new File(GameFolders.SAVES_DIR, "save")), true);
    }

    /**
     * Loads the default saved game.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void loadGame() throws IOException {
        loadGame(GameSave.fromFile(new File(GameFolders.SAVES_DIR, "save")));
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
        startGame(seed, gamemode, save, false);
    }

    /**
     * Start the game environment.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void startGame(long seed, Gamemode gamemode, GameSave save, boolean create) {
        // Start loading.
        var screen = new MessengerScreen();

        // Show environment loader screen.
        showScreen(screen);
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

            var environment = this.environment = new Environment(save, gamemode, seed);

            if (create) {
                screen.setDescription("Preparing creation");
                environment.prepareCreation(save);
            } else {
                screen.setDescription("Loading data...");
                gamemode = Gamemode.loadState(save, screen.getMessenger());
            }

            if (create) {
                screen.setDescription("Initializing environment");
                environment.initSave(screen.getMessenger());
                screen.setDescription("Saving initialized save");
                environment.save(save, screen.getMessenger());
            } else {
                environment.load(save, screen.getMessenger());
            }

            var loadedGame = new LoadedGame(save, this.environment);
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
            crash(crashLog.createCrash());
            return;
        }

        BubbleBlaster.getInstance().showScreen(null);
    }

    /**
     * Quit game environment and loaded game.
     */
    public void quitLoadedGame() {
        var loadedGame = getLoadedGame();
        if (loadedGame != null) {
            loadedGame.quit();
        }

        this.loadedGame = null;

        showScreen(new TitleScreen());
    }

    /**
     * Checks if the current thread is the main thread.
     *
     * @return true if the method is called on the main thread.
     */
    public boolean isOnTickingThread() {
        return Thread.currentThread() == instance.tickingThread;
    }

    /**
     * Checks if the current thread is the rendering thread.
     *
     * @return true if the method is called on the rendering thread.
     */
    public static boolean isOnRenderingThread() {
        return Thread.currentThread().getId() == instance.renderingThread.getId();
    }

    /**
     * Checks if the game environment is active.
     * Note that this will return true even if the game is paused.
     *
     * @return true if environment is active, even if game is paused.
     */
    public boolean isInGame() {
        return getLoadedGame() != null && environment != null;
    }

    /**
     * Checks if the game environment isn't running. Aka, is in menus like the title screen.
     *
     * @return true if in main menus.
     */
    public boolean isInMainMenus() {
        return getLoadedGame() == null && environment == null;
    }

    //////////////////////
    //     Managers     //
    //////////////////////
    @NotNull
    @ApiStatus.Internal
    public ScreenManager getScreenManager() {
        return screenManager;
    }

    @NotNull
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public TextureManager getTextureManager() {
        return textureManager;
    }

    /////////////////////////
    //     Loaded Game     //
    /////////////////////////
    public @Nullable LoadedGame getLoadedGame() {
        return loadedGame;
    }

    public @Nullable Environment getEnvironment() {
        return environment;
    }

    public @Nullable GameSave getCurrentSave() {
        var loadedGame = getLoadedGame();
        if (loadedGame != null) {
            return loadedGame.getGameSave();
        }
        return null;
    }

    ///////////////////
    //     Fonts     //
    ///////////////////
    public BitmapFont getSansFont() {
        return sansFont;
    }

    public BitmapFont getMonospaceFont() {
        return monospaceFont;
    }

    public BitmapFont getPixelFont() {
        return pixelFont;
    }

    public BitmapFont getLogoFont() {
        return logoFont;
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
        return fontName;
    }

    ///////////////////////
    //     Rendering     //
    ///////////////////////
    @NotNull
    public RenderSettings getRenderSettings() {
        return renderSettings;
    }

    public EnvironmentRenderer getEnvironmentRenderer() {
        return environmentRenderer;
    }

    ////////////////////
    //     Bounds     //
    ////////////////////
    public Rectangle getGameBounds() {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }

    ////////////////////////
    //     Game flags     //
    ////////////////////////
    public boolean isRunning() {
        return running;
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
        isGlitched = true;
    }

    /**
     * Creates a player.
     *
     * @return the created player.
     */
    private Player createPlayer() {
        if (environment != null) {
            return new Player(environment);
        } else {
            throw new IllegalStateException("Creating a player while environment is not loaded.");
        }
    }

    /**
     * Creates an instance create the screen manager.
     *
     * @return the created screen manager.
     */
    private ScreenManager createScreenManager() {
        return ScreenManager.create(new SplashScreen(), this);
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

        initialGameTick();

        try {
            while (running) {
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
                            gameTick();
                        } catch (Throwable t) {
                            var crashLog = new CrashLog("Game being ticked.", t);
                            crash(crashLog.createCrash());
                        }
                    }

                    this.lastTickMs = System.currentTimeMillis();

                    if (tickTime >= 1000.0d) {
                        currentTps = ticksPassed;
                        ticksPassed = 0;
                        tickTime = 0;
                    }
                } catch (OutOfMemoryError error) {
                    if (this.outOfMemory(error)) return;
                }
            }
        } catch (Throwable t) {
            var crashLog = new CrashLog("Running game loop.", t);
            crash(crashLog.createCrash());
        }
    }

    /**
     *
     */
    private void gameTick() {
        if (this.playerController != null) {
            this.playerController.tick();
        }

        if (this.isGlitched) {
            this.glitchRenderer.tick();
            return;
        }

        final var env = this.environment;
        final var player = this.player;
        if (env != null && !isPaused()) {
            final var gamemode = env.getGamemode();
            if (gamemode != null && player != null && player.getLevel() > 255)
                BubbleBlaster.getInstance().glitch();

            env.tick();
        }

        final var screen = this.getCurrentScreen();
        if (screen != null)
            screen.tick();

        if (player != null && GameInput.isKeyDown(Keys.SPACE))
            player.shoot();

        if (this.ticker.advance() == 40) {
            this.ticker.reset();
            if (this.isLoaded())
                tickRichPresence(player);
        }

        BubbleBlaster.ticks++;

        // Call tick event.
        if (isLoaded() && (screen == null || !screen.doesPauseGame()))
            TickEvents.TICK_GAME.factory().onTickGame(this);
    }

    private void tickRichPresence(@Nullable Player player) {
        if (this.isInMainMenus())
            setActivity(() -> {
                var activity = new Activity();
                activity.setState("In the menus");
                return activity;
            });
        else if (this.isInGame())
            setActivity(() -> {
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
            setActivity(() -> {
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
        // Call to game environment rendering.
        // Get field and set local variable. For multithreaded null-safety.
        @Nullable Screen screen = screenManager.getCurrentScreen();
        @Nullable Environment environment = this.environment;
        @Nullable EnvironmentRenderer environmentRenderer = this.environmentRenderer;

        // Post event before rendering the game.
        RenderEvents.RENDER_GAME_BEFORE.factory().onRenderGameBefore(this, renderer, frameTime);

        // Render environment.
        profiler.section("Render Environment", () -> {
            if (environment != null && environmentRenderer != null) {
                RenderEvents.RENDER_ENVIRONMENT_BEFORE.factory().onRenderEnvironmentBefore(environment, environmentRenderer, renderer);
                environmentRenderer.render(renderer);
                RenderEvents.RENDER_ENVIRONMENT_AFTER.factory().onRenderEnvironmentAfter(environment, environmentRenderer, renderer);
            }
        });

        // Render screen.
        profiler.section("Render Screen", () -> {
            if (screen != null) {
                RenderEvents.RENDER_SCREEN_BEFORE.factory().onRenderScreenBefore(screen, renderer);
                screen.render(this, renderer, mouseX, mouseY, frameTime);
                RenderEvents.RENDER_SCREEN_AFTER.factory().onRenderScreenAfter(screen, renderer);
            }
            if (environment != null && environmentRenderer != null) {
                renderer.fillEffect(0, 0, BubbleBlaster.getInstance().getWidth(), 3);
            }
        });

        // Post render.
        profiler.section("Post Render", () -> postRender(renderer));

        // Post event after rendering the game.
        RenderEvents.RENDER_GAME_AFTER.factory().onRenderGameAfter(this, renderer, frameTime);
    }

    private void postRender(Renderer renderer) {
        if (this.fadeIn) {
            final var timeDiff = System.currentTimeMillis() - this.fadeInStart;
            if (timeDiff <= this.fadeInDuration) {
                var clamp = (int) Mth.clamp(255 * (1f - ((float) timeDiff) / this.fadeInDuration), 0, 255);
                var color = Color.rgba(0, 0, 0, clamp);
                GuiComponent.fill(renderer, 0, 0, getWidth(), getHeight(), color);
            } else {
                this.fadeIn = false;
            }
        }
    }

    /**
     * Loads the game environment.
     */
    private void loadEnvironment() {
        preparePlayer();
    }

    /**
     * Starts loading the game.
     */
    public void startLoading() {
        getGameWindow().init();
    }

    /**
     * @return get the default font.
     */
    public BitmapFont getFont() {
        return getSansFont();
    }

    public boolean hasScreenOpen() {
        return screenManager.getCurrentScreen() != null;
    }

    public SoundInstance getSound(Identifier identifier) {
        var resource = resourceManager.getResource(identifier);

        return null;
    }

    public synchronized SoundInstance playSound(Identifier identifier) {
        try {
            var sound = new SoundInstance(identifier);
            sound.play();
            return sound;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized SoundInstance playSound(Identifier identifier, float volume) {
        try {
            var sound = new SoundInstance(identifier);
            sound.setVolume(volume);
            sound.play();
            return sound;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleTask(Runnable task) {
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
    public List<BufferedImageOp> getCurrentFilters() {
        return new ArrayList<>();
    }

    public void loadPlayEnvironment() {
        var player = createPlayer();
        this.input = player;
        this.playerController = new PlayerController(this.input);
        this.player = player;

        loadEnvironment();
    }

    /////////////////////
    //     Getters     //
    /////////////////////
    public float getGameFrameTime() {
        return gameFrameTime;
    }

    public boolean isAntialiasEnabled() {
        return true;
    }

    public boolean isTextAntialiasEnabled() {
        return isAntialiasEnabled();
    }

    public GameWindow getGameWindow() {
        return this.window;
    }

    @Deprecated(forRemoval = true)
    public ImageObserver getObserver() {
        return null;
    }

    public Rectangle getBounds() {
        return new Rectangle(0, 0, window.getWidth(), window.getHeight());
    }

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    public int getFps() {
        return fps;
    }

    public int getCurrentTps() {
        return currentTps;
    }

    public Instant getLastTickTime() {
        return Instant.ofEpochMilli(this.lastTickMs);
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public boolean isLoading() {
        return !loaded;
    }

    public boolean isStopping() {
        return stopping;
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
            crashLog.defaultSave();
            LOGGER.error(crashLog.toString());
        }

        instance.annihilate();
    }

    @EnsuresNonNull({"ticking"})
    @SuppressWarnings("Convert2Lambda")
    void windowLoaded() {
        this.running = true;

        this.tickingThread = new Thread(BubbleBlaster.this::ticking, "Ticker");
        this.tickingThread.start();

        this.garbageCollector = new GarbageCollector();

        BubbleBlaster.getLogger().info("Game threads started!");
    }

    public void crash(Throwable t) {
        var crashLog = new CrashLog("Unknown source", t);
        crash(crashLog.createCrash());
    }

    public void setup() {
        LanguageManager.INSTANCE.register(new Locale("en"), "english");
        LanguageManager.INSTANCE.register(new Locale("nl"), "dutch");
        LanguageManager.INSTANCE.register(new Locale("fy"), "frisian");
        LanguageManager.INSTANCE.register(new Locale("de"), "german");
        LanguageManager.INSTANCE.register(new Locale("el"), "greek");
        LanguageManager.INSTANCE.register(new Locale("it"), "italian");
        LanguageManager.INSTANCE.register(new Locale("fr"), "french");
        LanguageManager.INSTANCE.register(new Locale("es"), "spanish");
        LanguageManager.INSTANCE.register(new Locale("pt"), "portuguese");
        LanguageManager.INSTANCE.register(new Locale("pl"), "polish");
        LanguageManager.INSTANCE.register(new Locale("hu"), "hungarian");
        LanguageManager.INSTANCE.register(new Locale("fi"), "finnish");
        LanguageManager.INSTANCE.register(new Locale("sv"), "swedish");
        LanguageManager.INSTANCE.register(new Locale("da"), "danish");
        LanguageManager.INSTANCE.register(new Locale("ro"), "romanian");
        LanguageManager.INSTANCE.register(new Locale("af"), "african");
        LanguageManager.INSTANCE.register(new Locale("zu"), "zulu");
        LanguageManager.INSTANCE.register(new Locale("mi"), "maori");
        LanguageManager.INSTANCE.register(new Locale("uk"), "ukrainian");
        LanguageManager.INSTANCE.register(new Locale("ur"), "urdu");
        LanguageManager.INSTANCE.register(new Locale("hi"), "hindi");
        LanguageManager.INSTANCE.register(new Locale("sa"), "sanskrit");
        LanguageManager.INSTANCE.register(new Locale("tr"), "turkish");
        LanguageManager.INSTANCE.register(new Locale("ar"), "arabic");
        LanguageManager.INSTANCE.register(new Locale("he"), "hebrew");
        LanguageManager.INSTANCE.register(new Locale("ko"), "korean");
        LanguageManager.INSTANCE.register(new Locale("ru"), "russian");
        LanguageManager.INSTANCE.register(new Locale("zh"), "chinese");
        LanguageManager.INSTANCE.register(new Locale("ja"), "japanese");

        var locales = LanguageManager.INSTANCE.getLocales();
        for (var locale : locales) {
            LanguageManager.INSTANCE.load(locale, new Identifier(LanguageManager.INSTANCE.getLanguageID(locale)), BubbleBlaster.getInstance().getResourceManager());
        }
    }

    public void finalizeSetup() {

    }

    public void resize(IntSize size) {
        screenManager.resize(size);
    }

    public void fadeIn(float time) {
        fadeIn = true;
        fadeInStart = System.currentTimeMillis();
        fadeInDuration = time;
    }

    @CanIgnoreReturnValue
    public boolean registerFont(BitmapFont font) {
//        return graphicEnv.registerFont(font);
        return true;
    }

    public void finish() {
        this.glitchRenderer = new GlitchRenderer(this);
        showScreen(new TitleScreen());
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
        if (this.environment == null)
            throw new IllegalStateException("Environment isn't loaded.");

        this.environment.save();
        this.quitLoadedGame();
    }

    public GlobalSaveData getGlobalData() {
        return globalSaveData;
    }

    public BitmapFont getBitmapFont() {
        return new BitmapFont();
    }

    public boolean isFocused() {
        return focused;
    }

    public Renderer getRenderer() {
        return currentRenderer;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean keyPress(int keycode) {
        Screen currentScreen = this.getCurrentScreen();
        Environment environment = this.environment;

        if (keycode == Keys.F12) {
            debugGuiOpen.set(!debugGuiOpen.get());
            LOGGER.debug("Toggling debug gui");
            return true;
        } else if (keycode == Keys.F2) {
            Screenshot screenshot = Screenshot.take();
            notifications.notify(new Notification("Screenshot saved!", screenshot.fileHandle().name(), "SCREENSHOT MANAGER"));
            return true;
        } else if (environment != null) {
            if (keyPressEnv(keycode, environment)) return true;
        }

        if (currentScreen != null) {
            return currentScreen.keyPress(keycode);
        } else if (keycode == Keys.ESCAPE && environment != null && environment.isAlive()) {
            BubbleBlaster.getInstance().showScreen(new PauseScreen());
            return true;
        }
        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    private boolean keyPressEnv(int keycode, Environment environment) {
        Player player = this.player;
        boolean shouldTriggerDevCommand = debugMode || devMode;

        if (this.isInGame() && player != null) {
            if (keycode == Keys.UP) player.forward(true);
            if (keycode == Keys.DOWN) player.backward(true);
            if (keycode == Keys.LEFT) player.left(true);
            if (keycode == Keys.RIGHT) player.right(true);
        }

        if (keycode == Keys.F10 && (shouldTriggerDevCommand)) {
            environment.triggerBloodMoon();
            return true;
        } else if (keycode == Keys.SLASH && !hasScreenOpen()) {
            BubbleBlaster.getInstance().showScreen(new CommandScreen());
            return true;
        }

        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean keyRelease(int keycode) {
        @Nullable Screen currentScreen = this.getCurrentScreen();
        if (currentScreen != null) return currentScreen.keyRelease(keycode);

        if (this.isInGame() && player != null) {
            if (keycode == Keys.UP) player.forward(false);
            if (keycode == Keys.DOWN) player.backward(false);
            if (keycode == Keys.LEFT) player.left(false);
            if (keycode == Keys.RIGHT) player.right(false);
        }

        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean charType(char character) {
        @Nullable Screen currentScreen = this.getCurrentScreen();
        if (currentScreen != null) return currentScreen.charType(character);

        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean mousePress(int screenX, int screenY, int pointer, int button) {
        @Nullable Screen currentScreen = this.getCurrentScreen();
        if (currentScreen != null) return currentScreen.mousePress(screenX, screenY, button);

        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean mouseRelease(int screenX, int screenY, int pointer, int button) {
        @Nullable Screen currentScreen = this.getCurrentScreen();
        if (currentScreen != null) return currentScreen.mouseRelease(screenX, screenY, button);

        return false;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean mouseDragged(int oldX, int oldY, int newX, int newY, int pointer, int button) {
        Screen currentScreen = this.getCurrentScreen();
        if (currentScreen != null) currentScreen.mouseDrag(oldX, oldY, newX, newY, button);

        return true;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean mouseMove(int screenX, int screenY) {
        @Nullable Screen currentScreen = this.getCurrentScreen();
        if (currentScreen != null) currentScreen.mouseMove(screenX, screenY);

        return true;
    }

    @ApiStatus.Internal
    @CanIgnoreReturnValue
    public boolean mouseWheel(int x, int y, float amountX, float amountY) {
        Screen currentScreen = this.getCurrentScreen();
        if (currentScreen != null) return currentScreen.mouseWheel(x, y, amountY);

        return false;
    }

    @Override
    public void fillInCrash(CrashLog crashLog) {
        Environment environment = this.environment;
        if (environment != null) {
            environment.fillInCrash(crashLog);
        }

        Screen currentScreen = this.getCurrentScreen();
        if (currentScreen != null) {
            currentScreen.fillInCrash(crashLog);
        }
    }

    protected static class BootOptions {

        public BootOptions() {

        }

        public BootOptions tps(int tps) {
            return this;
        }
    }
}
