package com.ultreon.bubbles.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.annotations.Beta;
import com.google.common.base.Suppliers;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.common.References;
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
import com.ultreon.bubbles.media.MP3Player;
import com.ultreon.bubbles.media.SoundInstance;
import com.ultreon.bubbles.media.SoundPlayer;
import com.ultreon.bubbles.mod.loader.GameJar;
import com.ultreon.bubbles.mod.loader.LibraryJar;
import com.ultreon.bubbles.mod.loader.ScannerResult;
import com.ultreon.bubbles.player.InputController;
import com.ultreon.bubbles.player.PlayerController;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.*;
import com.ultreon.bubbles.render.font.FontInfo;
import com.ultreon.bubbles.render.font.FontStyle;
import com.ultreon.bubbles.render.font.SystemFont;
import com.ultreon.bubbles.render.font.Thickness;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.screen.*;
import com.ultreon.bubbles.render.gui.screen.splash.SplashScreen;
import com.ultreon.bubbles.resources.ResourceFileHandle;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.util.helpers.Mth;
import com.ultreon.bubbles.vector.size.IntSize;
import com.ultreon.commons.time.TimeProcessor;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.ProgressMessenger;
import com.ultreon.libs.commons.v0.util.FileUtils;
import com.ultreon.libs.crash.v0.ApplicationCrash;
import com.ultreon.libs.crash.v0.CrashLog;
import com.ultreon.libs.registries.v0.Registry;
import com.ultreon.libs.resources.v0.Resource;
import com.ultreon.libs.resources.v0.ResourceManager;
import com.ultreon.libs.translations.v0.LanguageManager;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
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
import org.checkerframework.common.value.qual.IntVal;
import org.fusesource.jansi.AnsiConsole;
import org.jdesktop.swingx.util.OS;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.earlygrey.shapedrawer.ShapeDrawer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import static com.ultreon.bubbles.core.input.KeyboardInput.Map.*;
import static org.apache.logging.log4j.MarkerManager.getMarker;

/**
 * The Bubble Blaster game main class.
 *
 * @since 0.0.1
 */
@ParametersAreNonnullByDefault
@SuppressWarnings({"ResultOfMethodCallIgnored", "unused", "RedundantSuppression"})
public final class BubbleBlaster extends ApplicationAdapter {
    public static final int TPS = 40;
    public static final String NAMESPACE = "bubbles";
    // Logger.
    private static final Logger logger = LoggerFactory.getLogger("Generic");
    private static final WatchManager watcher = new WatchManager(new ConfigurationScheduler("File Watcher"));
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
    public final Profiler profiler = new Profiler();
    private URL gameFile;
    @IntVal(20)
    private final int tps = 20;
    private ResourceManager resourceManager;
    private GameWindow window;
    private ScreenManager screenManager;
    private RenderSettings renderSettings;
    // Tasks
    final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    private Thread rpcThread;
    // Fonts.
    private SystemFont sansFont;
    // Font names.
    private String fontName;
    // Rendering
    private DebugRenderer debugRenderer;
    private EnvironmentRenderer environmentRenderer;
    private final GraphicsEnvironment graphicEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
    // Managers.
    private TextureManager textureManager;
    // Randomizers.
    private final Random random = new Random();
    // Misc
    private final BufferedImage background = null;
    private final Object rpcUpdateLock = new Object();
    private final Ticker ticker = new Ticker();
//    private final ScannerResult scanResults;
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
    private volatile boolean running = false;
    // Running value.
    // Threads
    private Thread renderingThread;
    private Thread tickingThread;
    private GarbageCollector garbageCollector;
    float gameFrameTime;
    private boolean stopping;
    private SystemFont monospaceFont;
    private SystemFont pixelFont;
    private SystemFont logoFont;
    // Loaded game.
    @Nullable
    private LoadedGame loadedGame;
    private boolean debugGuiOpen = false;
    private GlitchRenderer glitchRenderer = null;
    private boolean isGlitched = false;
    private Supplier<Activity> activity;
    private volatile boolean rpcUpdated;
    private final Map<Thread, ThreadSection> lastProfile = new HashMap<>();
    private float fadeInDuration = Float.NEGATIVE_INFINITY;
    private boolean fadeIn = false;
    private long fadeInStart = 0L;
    private boolean firstFrame = true;
    private final GlobalSaveData globalSaveData = GlobalSaveData.instance();
    private SpriteBatch batch;
    private ShapeDrawer shapes;
    private OrthographicCamera camera;
    private Viewport viewport;
    private boolean focused;
    private int transX;
    private int transY;

    /**
     * Class constructor for Bubble Blaster.
     *
     * @see LoadScreen
     */
    public BubbleBlaster() throws IOException {
    }

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        Pixmap singlePxPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        singlePxPixmap.setColor(Color.rgb(0xffffff).toGdx());
        singlePxPixmap.drawPixel(0, 0);
        com.badlogic.gdx.graphics.Texture singlePxTex = new com.badlogic.gdx.graphics.Texture(singlePxPixmap);
        TextureRegion pixel = new TextureRegion(singlePxTex);
        this.shapes = new ShapeDrawer(batch, pixel);

        this.textureManager = TextureManager.instance();
        this.debugRenderer = new DebugRenderer(this);
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, getWidth(), getHeight()); // Set up the camera's projection matrix
        this.viewport = new ScreenViewport(camera);

        this.transX = Gdx.graphics.getWidth() / 2;
        this.transY = Gdx.graphics.getHeight() / 2;
        camera.translate(transX, transY);

        // Set default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(new GameExceptions());
        Thread.currentThread().setUncaughtExceptionHandler(new GameExceptions());

        // Hook output for logger.
        System.setErr(new RedirectPrintStream(Level.ERROR, LogManager.getLogger("STD"), getMarker("Output")));
        System.setOut(new RedirectPrintStream(Level.INFO, LogManager.getLogger("STD"), getMarker("Error")));

        // Assign instance.
        instance = this;

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

        sansFont = new SystemFont("Helvetica");

        Bubbles.register();
        AmmoTypes.register();
        Entities.register();
        Fonts.register();
        Sounds.register();
        StatusEffects.register();
        Abilities.register();
        GameplayEvents.register();
        Gamemodes.register();
        TextureCollections.register();

        // Load game with loading screen.
        this.load(new ProgressMessenger(this::log, 1000));
        this.screenManager = createScreenManager();
        BubbleBlaster.instance = this;

        setActivity(() -> {
            var activity = new Activity();
            activity.setState("Loading game.");
            return activity;
        });

        rpcThread = new Thread(() -> {
            try {
                rpc();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        rpcThread.start();

        logger.info("Discord RPC is initializing!");

        environmentRenderer = new EnvironmentRenderer();

        List<Path> paths = FabricLoader.getInstance().getModContainer(NAMESPACE).orElseThrow().getOrigin().getPaths();
        for (Path path : paths) {
            try {
                resourceManager.importPackage(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Assign instance.
        for (var thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getName().equals("JavaFX Application Thread")) {
                thread.setName("Application Thread");
            }
        }

        // Add ansi color compatibility in console.
        AnsiConsole.systemInstall();
        FileUtils.setCwd(References.GAME_DIR);

        // Logs directory creation.
        References.LOGS_DIR.mkdirs();

        // Font Name
        fontName = "Chicle Regular";

        // Register events.
        InputEvents.KEY_PRESS.listen(this::onKeyPress);
        InputEvents.KEY_RELEASE.listen(this::onKeyRelease);
        InputEvents.MOUSE_CLICK.listen(this::onMouseClick);

        // Register Game Font.
        var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        GameEvents.CLIENT_STARTED.factory().onClientStarted(this);

        // Start scene-manager.
        try {
            screenManager.start();
        } catch (Throwable t) {
            var crashLog = new CrashLog("Oops, game crashed!", t);
            crash(t);
        }

        // Request focus
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
        camera.setToOrtho(false, width, height); // Set up the camera's projection matrix
        this.camera.translate(-this.transX, -this.transY);
        this.transX = (int) (viewport.getWorldWidth() / 2);
        this.transY = (int) (viewport.getWorldHeight() / 2);
        this.camera.translate(this.transX, this.transY);
    }

    @Override
    public void render() {
        super.render();

        Gdx.gl20.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_BLEND);

        tasks.forEach(Runnable::run);
        tasks.clear();

        camera.update();
        Matrix4 matrix = camera.combined;
        batch.setProjectionMatrix(matrix);
        batch.begin();
        var renderer = new Renderer(shapes, new MatrixStack(matrix));

        if (isGlitched) {
            glitchRenderer.render(renderer);
        } else {
            var filters = BubbleBlaster.instance.getCurrentFilters();

            profiler.section("renderGame", () -> this.render(renderer, gameFrameTime));

            if (isDebugMode() || debugGuiOpen) {
                debugRenderer.render(renderer);
            }

            this.fps = Gdx.graphics.getFramesPerSecond();
        }
        batch.end();
        Gdx.gl20.glDisable(GL20.GL_BLEND);
    }

    public static Map<Thread, ThreadSection> getLastProfile() {
        return instance.lastProfile;
    }

    public static WatchManager getWatcher() {
        return watcher;
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
    public static BubbleBlaster launch(Arguments args) throws IOException {
        System.setProperty("log4j2.formatMsgNoLookups", "true"); // Fix CVE-2021-44228 exploit.
        Identifier.setDefaultNamespace("bubbles");
        ResourceManager.logger = getLogger("ResourceManager");
        Registry.dumpLogger = getLogger("RegistryDump");
        LanguageManager.INSTANCE.logger = getLogger("LanguageManager");

        // Get game-directory.
        final var defaultGameDir = new File(".");
        gameDir = !args.containsKey("gameDir") ? defaultGameDir : new File(args.get("gameDir"));
        debugMode = args.getExtraArgs().contains("--debug");
        devMode = FabricLoader.getInstance().isDevelopmentEnvironment();

        BubbleBlaster.classLoader = getClassLoader();

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
        if (OS.isWindows()) {
            return System.getenv("APPDATA");
        } else if (OS.isLinux()) {
            return "~/.config/";
        } else if (OS.isMacOSX()) {
            return "~/Library/Application Support/";
        } else {
            throw new UnsupportedOperationException("Unsupported Operating System");
        }
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
    public static double getMiddleX() {
        return (double) getInstance().getWidth() / 2;
    }

    public static double getMiddleY() {
        return (double) getInstance().getHeight() / 2;
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
        return logger;
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

            if (!holding) {
                if (keyCode == KeyEvent.VK_SLASH && !hasScreenOpen()) {
                    BubbleBlaster.getInstance().showScreen(new CommandScreen());
                }
            }

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

        if (player != null && !holding) {
            if (keyCode == KEY_UP) player.forward(true);
            if (keyCode == KEY_DOWN) player.backward(true);
            if (keyCode == KEY_LEFT) player.left(true);
            if (keyCode == KEY_RIGHT) player.right(true);
        }
    }

    private void onKeyRelease(int keyCode) {
        if (player != null) {
            if (keyCode == KEY_UP) player.forward(true);
            if (keyCode == KEY_DOWN) player.backward(true);
            if (keyCode == KEY_LEFT) player.left(true);
            if (keyCode == KEY_RIGHT) player.right(true);
        }
    }

    private void onMouseClick(int x, int y, int button, int clicks) {
        var loadedGame = this.loadedGame;
        if (isDevMode()) {
            if (loadedGame != null && button == 1) {
                if (GameInput.isKeyDown(Input.Keys.F1)) {
                    Objects.requireNonNull(loadedGame.getGamemode().getPlayer()).teleport(x, y);
                }
            }
        }
    }

    public URL getGameFile() {
        return gameFile;
    }

    @SuppressWarnings("BusyWait")
    private void rpc() throws IOException {
        try {
            var discordLibrary = new DownloadDiscordSDK().download();
            if (discordLibrary == null) {
                System.err.println("Error downloading Discord SDK.");
                return;
            }
            // Initialize the Core
            Core.init(discordLibrary);
        } catch (Throwable t) {
            t.printStackTrace();
            return;
        }

        // Set parameters for the Core
        try (var params = new CreateParams()) {
            params.setClientID(933147296311427144L);
            params.setFlags(CreateParams.getDefaultFlags());

            // Create the Core
            try (var core = new Core(params)) {
                // Create the Activity
                try (var activity = new Activity()) {
                    activity.setDetails("Developer mode");
                    activity.setState("RPC Testing");

                    // Setting a start time causes an "elapsed" field to appear
                    activity.timestamps().setStart(Instant.now());

                    // Make a "cool" image show up
                    activity.assets().setLargeImage("icon");

                    // Finally, update the current activity to our activity
                    core.activityManager().updateActivity(activity);
                }

                // Run callbacks forever
                while (true) {
                    core.runCallbacks();
                    synchronized (rpcUpdateLock) {
                        if (rpcUpdated) {
                            rpcUpdated = false;
                            core.activityManager().updateActivity(activity.get());
                        }
                    }
                    try {
                        // Sleep a bit to save CPU
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        try {
                            core.close();
                        } catch (Exception ex) {
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }

    public void updateRPC() {
//        DiscordRPC.discordUpdatePresence(presence);
    }

    public Supplier<Activity> getActivity() {
        return activity;
    }

    public void setActivity(Supplier<Activity> activity) {
        synchronized (rpcUpdateLock) {
            this.activity = () -> {
                var ret = activity.get();
                var state = ret.getState();
                Gdx.graphics.setTitle("Bubble Blaster - " + getGameVersion().getFriendlyString() + " - " + state);
                ret.assets().setLargeImage("icon");
                return ret;
            };
            rpcUpdated = true;
        }
    }

    private void initialGameTick() {

    }

    private void initDiscord() {

    }

    @SuppressWarnings("DuplicatedCode")
    private void tickThread() {
        var tickCap = 1f / (double) getTps();

        var time = TimeProcessor.now();
        double unprocessed = 0;

        try {
            while (running) {
                var canTick = false;

                var time2 = TimeProcessor.now();
                var passed = time2 - time;
                unprocessed += passed;

                time = time2;

                while (unprocessed >= tickCap) {
                    unprocessed -= tickCap;

                    canTick = true;
                }

                if (canTick) {
                    try {
                        internalTick();
                    } catch (Throwable t) {
                        var crashLog = new CrashLog("Game being ticked.", t);
                        crash(crashLog.createCrash());
                    }
                }
            }
        } catch (Throwable t) {
            var crashLog = new CrashLog("Running game loop.", t);
            crash(crashLog.createCrash());
        }
    }

    private BitmapFont loadFontInternally(Identifier location) {
        return loadBitmapFont(Gdx.files.internal("/assets/" + location.location() + "/fonts/" + location.path() + ".ttf"));
    }

    public FontInfo loadFont(Identifier fontId) {
        var identifier = new Identifier(fontId.location(), "fonts/" + fontId.path() + ".ttf");
        Resource resource = resourceManager.getResource(identifier);
        var handle = new ResourceFileHandle(resource);
        if (handle.exists()) {
            try {
                var regularFont = loadBitmapFont(handle);
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
                    var regularFont = loadBitmapFont(regularHandle);
                    var builder = FontInfo.builder(fontId);
                    builder.set(Thickness.REGULAR, FontStyle.PLAIN, regularFont);

                    for (var thickness : Thickness.values()) {
                        var thicknessId = new Identifier(fontId.location(), "fonts/" + fontId.path() + "_" + thickness.name().toLowerCase() + ".ttf");
                        Resource thicknessRes = resourceManager.getResource(thicknessId);
                        var thicknessHandle = new ResourceFileHandle(thicknessRes);
                        if (thicknessHandle.exists()) {
                            var thicknessFont = loadBitmapFont(thicknessHandle);
                            this.registerFont(thicknessFont);
                            builder.set(thickness, FontStyle.PLAIN, thicknessFont);

                            for (var style : FontStyle.values()) {
                                if (style == FontStyle.PLAIN) continue;
                                var thicknessStyleId = new Identifier(fontId.location(), "fonts/" + fontId.path() + "_" + thickness.name().toLowerCase() + "_" + style.name().toLowerCase() + ".ttf");
                                Resource thicknessStyleRes = resourceManager.getResource(thicknessStyleId);
                                var thicknessStyleHandle = new ResourceFileHandle(thicknessStyleRes);
                                if (thicknessStyleHandle.exists()) {
                                    var thicknessStyleFont = loadBitmapFont(thicknessStyleHandle);
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

    private BitmapFont loadBitmapFont(FileHandle handle) {
        var generator = new FreeTypeFontGenerator(handle);
        var parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        var font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        return font;
    }

    ////////////////////////
    //     Game flags     //
    ////////////////////////

    public void loadFonts() {
        try {
            this.sansFont = new SystemFont(loadFont(id("noto_sans/noto_sans")));
            this.sansFont.alternative("zh", loadFont(id("noto_sans/noto_sans_zh")));
            this.sansFont.alternative("ja", loadFont(id("noto_sans/noto_sans_ja")));
            this.sansFont.alternative("ko", loadFont(id("noto_sans/noto_sans_ko")));
            this.sansFont.alternative("ur", loadFont(id("noto_nastaliq/noto_nastaliq_urdu")));
            this.logoFont = new SystemFont(loadFont(id("chicle")));
            this.pixelFont = new SystemFont(loadFont(id("pixel")));
            this.monospaceFont = new SystemFont(loadFont(id("roboto/roboto_mono")));
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
        logger.info("Shutting down Bubble Blaster");

        this.tickingThread.interrupt();
        this.garbageCollector.interrupt();
        this.rpcThread.interrupt();

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
            this.tickingThread.join();
            this.garbageCollector.join();
            this.rpcThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkForExitEvents();
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
            loadGame(GameSave.fromFile(new File(References.SAVES_DIR, saveName)));
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
        createGame(seed, Gamemodes.MODERN);
    }

    /**
     * Create a new saved game.
     *
     * @param seed     generator seed.
     * @param gamemode game mode to use.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void createGame(long seed, Gamemode gamemode) {
        startGame(seed, gamemode, GameSave.fromFile(new File(References.SAVES_DIR, "save")), true);
    }

    /**
     * Loads the default saved game.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void loadGame() throws IOException {
        loadGame(GameSave.fromFile(new File(References.SAVES_DIR, "save")));
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
            t.printStackTrace();
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
        return Thread.currentThread() == tickingThread;
    }

    /**
     * Checks if the current thread is the rendering thread.
     *
     * @return true if the method is called on the rendering thread.
     */
    public boolean isOnRenderingThread() {
        return Thread.currentThread() == renderingThread;
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
    public SystemFont getSansFont() {
        return sansFont;
    }

    public SystemFont getMonospaceFont() {
        return monospaceFont;
    }

    public SystemFont getPixelFont() {
        return pixelFont;
    }

    public SystemFont getLogoFont() {
        return logoFont;
    }

    /////////////////////
    //     Cursors     //
    /////////////////////
    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Cursor getBlankCursor() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Cursor getTextCursor() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Cursor getPointerCursor() {
        return null;
    }

    @Deprecated(forRemoval = true, since = "0.1.0-alpha.1")
    public Cursor getDefaultCursor() {
        return null;
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
        var tickCap = 1000.0 / (double) tps;
        var tickTime = 0d;
        var gameFrameTime = 0d;
        var ticksPassed = 0;

        double time = System.currentTimeMillis();

        initialGameTick();

        try {
            while (running) {
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
                        internalTick();
                    } catch (Throwable t) {
                        var crashLog = new CrashLog("Game being ticked.", t);
                        crash(crashLog.createCrash());
                    }
                }

                if (tickTime >= 1000.0d) {
                    currentTps = ticksPassed;
                    ticksPassed = 0;
                    tickTime = 0;
                }

                Thread.sleep(8);
            }
        } catch (InterruptedException e) {
            logger.info("Ticking interrupted.");
        } catch (Throwable t) {
            var crashLog = new CrashLog("Running game loop.", t);
            crash(crashLog.createCrash());
        }
    }

    /**
     *
     */
    private void internalTick() {
        @Nullable Screen currentScreen = screenManager.getCurrentScreen();

        if (playerController != null) {
            playerController.tick();
        }

        this.tick();

        // Call tick event.
        if (isLoaded() && (currentScreen == null || !currentScreen.doesPauseGame())) {
            TickEvents.TICK_GAME.factory().onTickGame(this);
        }
    }

    private void onFirstFrame() {
        this.window.finalSetup();
    }

    /**
     * @param renderer the renderer to render the game with.
     */
    private void render(Renderer renderer, float frameTime) {
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
                System.out.println("screen.getClass().getName() = " + screen.getClass().getName());
                RenderEvents.RENDER_SCREEN_BEFORE.factory().onRenderScreenBefore(screen, renderer);
                screen.render(this, renderer, frameTime);
                RenderEvents.RENDER_SCREEN_AFTER.factory().onRenderScreenAfter(screen, renderer);
            }
//            if (environment != null && environmentRenderer != null) {
//                renderer.fillEffect(0, 0, BubbleBlaster.getInstance().getWidth(), 3);
//            }
        });

        // Post render.
        profiler.section("Post Render", () -> postRender(renderer));

        // Post event after rendering the game.
        RenderEvents.RENDER_GAME_AFTER.factory().onRenderGameAfter(this, renderer, frameTime);
    }

    private void postRender(Renderer renderer) {
        if (fadeIn) {
            final var timeDiff = System.currentTimeMillis() - fadeInStart;
            if (timeDiff <= fadeInDuration) {
                var clamp = (int) Mth.clamp(255 * (1f - ((float) timeDiff) / fadeInDuration), 0, 255);
                var color = Color.rgba(0, 0, 0, clamp);
                GuiComponent.fill(renderer, 0, 0, getWidth(), getHeight(), color);
            }
        }
    }

    /**
     * Ticks the game.
     */
    private void tick() {
        if (isGlitched) {
            glitchRenderer.tick();
            return;
        }

        final var env = this.environment;
        final var player = this.player;
        if (env != null && !isPaused()) {
            final var gamemode = env.getGamemode();
            if (gamemode != null) {
                if (player != null) {
                    if (player.getLevel() > 255) {
                        BubbleBlaster.getInstance().glitch();
                    }
                }

            }
            env.tick();
        }

        final var screen = this.getCurrentScreen();
        if (screen != null) {
            screen.tick();
        }

        if (player != null) {
            if (GameInput.isKeyDown(Input.Keys.SPACE)) {
                player.shoot();
            }
        }

        if (ticker.advance() == 40) {
            ticker.reset();
            if (LoadScreen.isDone()) {
                if (isInMainMenus()) {
                    setActivity(() -> {
                        var activity = new Activity();
                        activity.setState("In the menus");
                        return activity;
                    });
                } else if (isInGame()) {
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
                } else {
                    setActivity(() -> {
                        var activity = new Activity();
                        activity.setState("Is nowhere to be found");
                        return activity;
                    });
                }
            }
        }

        BubbleBlaster.ticks++;
    }

    public void keyPress(int keyCode) {
        if (keyCode == KEY_F12) {
            debugGuiOpen = !debugGuiOpen;
            logger.debug("Toggling debug gui");
        } else if (keyCode == KEY_F10 && (debugMode || devMode)) {
            var env = environment;
            if (env != null) {
                env.triggerBloodMoon();
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
    public SystemFont getFont() {
        return getSansFont();
    }

    public FontMetrics getFontMetrics(Font font) {
        return null;
    }

    public boolean hasScreenOpen() {
        return screenManager.getCurrentScreen() != null;
    }

    public SoundInstance getSound(Identifier identifier) {
        var resource = resourceManager.getResource(identifier);

        return null;
    }

    public synchronized MP3Player playSound(Identifier identifier) {
        // The wrapper thread is unnecessary, unless it blocks on the
        // Clip finishing; see comments.
        //        new Thread(() -> {
        try {
            var identifier1 = identifier.mapPath(path -> "audio/" + path + ".mp3");
            System.out.println("identifier1 = " + identifier1);
            var input = resourceManager.getResource(identifier1);
            System.out.println(input);
            var player = new MP3Player(identifier + "/" + UUID.randomUUID().toString().replaceAll("-", ""), Objects.requireNonNull(input).loadOrOpenStream());
            player.play();
            return player;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        }).start();
//        return null;
    }

    public synchronized SoundInstance playSound(Identifier identifier, double volume) {
        try {
            var sound = new SoundInstance(identifier, identifier + "/" + UUID.randomUUID().toString().replaceAll("-", ""));
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

    public boolean isLoaded() {
        return this.loaded;
    }

    private void markLoaded() {
        this.loaded = true;
    }

    public int getTps() {
        return tps;
    }

    @Deprecated
    public ScannerResult getScanResults() {
        return new ScannerResult(new HashMap<>());
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

    @ApiStatus.Internal
    public void dispose() {
        instance.onClose();
        if (crashed) {
            System.exit(1);
        }
        System.exit(0);
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
            t.printStackTrace();
        }

        if (!overridden) {
            crashLog.defaultSave();
            logger.error(crashLog.toString());
        }

        instance.shutdown();
    }

    @EnsuresNonNull({"ticking"})
    @SuppressWarnings("Convert2Lambda")
    void windowLoaded() {
        this.running = true;

        this.tickingThread = new Thread(BubbleBlaster.this::ticking, "Ticker");
        this.tickingThread.setDaemon(false);
        this.tickingThread.start();

        this.garbageCollector = new GarbageCollector(this);
        this.garbageCollector.setDaemon(false);
        this.garbageCollector.start();

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
        glitchRenderer = new GlitchRenderer(this);
        showScreen(new TitleScreen());
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

    protected static class BootOptions {
        private int tps = 20;

        public BootOptions() {

        }

        public BootOptions tps(int tps) {
            this.tps = tps;
            return this;
        }
    }
}
