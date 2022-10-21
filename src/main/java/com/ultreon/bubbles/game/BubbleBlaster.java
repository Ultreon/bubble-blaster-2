package com.ultreon.bubbles.game;

import com.google.common.annotations.Beta;
import com.ultreon.bubbles.bubble.BubbleType;
import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.common.References;
import com.ultreon.bubbles.common.exceptions.ResourceNotFoundException;
import com.ultreon.bubbles.common.text.translation.LanguageManager;
import com.ultreon.bubbles.debug.DebugRenderer;
import com.ultreon.bubbles.debug.Profiler;
import com.ultreon.bubbles.entity.player.Player;
import com.ultreon.bubbles.environment.Environment;
import com.ultreon.bubbles.environment.EnvironmentRenderer;
import com.ultreon.bubbles.event.v2.GameEvents;
import com.ultreon.bubbles.event.v2.InputEvents;
import com.ultreon.bubbles.event.v2.RenderEvents;
import com.ultreon.bubbles.event.v2.TickEvents;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.*;
import com.ultreon.bubbles.input.KeyInput;
import com.ultreon.bubbles.media.MP3Player;
import com.ultreon.bubbles.media.Sound;
import com.ultreon.bubbles.media.SoundPlayer;
import com.ultreon.bubbles.mod.loader.Scanner;
import com.ultreon.bubbles.mod.loader.ScannerResult;
import com.ultreon.bubbles.player.InputController;
import com.ultreon.bubbles.player.PlayerController;
import com.ultreon.bubbles.registry.Registers;
import com.ultreon.bubbles.render.*;
import com.ultreon.bubbles.render.screen.*;
import com.ultreon.bubbles.render.screen.gui.GuiElement;
import com.ultreon.bubbles.render.screen.splash.SplashScreen;
import com.ultreon.bubbles.resources.Resource;
import com.ultreon.bubbles.resources.ResourceManager;
import com.ultreon.bubbles.save.GameSave;
import com.ultreon.bubbles.util.helpers.MathHelper;
import com.ultreon.bubbles.vector.size.IntSize;
import com.ultreon.commons.crash.ApplicationCrash;
import com.ultreon.commons.crash.CrashLog;
import com.ultreon.commons.lang.Messenger;
import com.ultreon.commons.lang.ProgressMessenger;
import com.ultreon.commons.time.TimeProcessor;
import com.ultreon.commons.util.FileUtils;
import com.ultreon.dev.DevClassPath;
import com.ultreon.dev.GameDevMain;
import com.ultreon.preloader.PreClassLoader;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import org.apache.logging.log4j.core.config.ConfigurationScheduler;
import org.apache.logging.log4j.core.util.WatchManager;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.checkerframework.common.value.qual.IntRange;
import org.checkerframework.common.value.qual.IntVal;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import static com.ultreon.bubbles.input.KeyInput.Map.*;

/**
 * The Bubble Blaster game main class.
 *
 * @since 0.0.1-indev1
 */
@ParametersAreNonnullByDefault
@SuppressWarnings({"ResultOfMethodCallIgnored", "unused", "RedundantSuppression"})
public final class BubbleBlaster {
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
    public final Profiler profiler = new Profiler();
    private final File gameFile;
    @IntVal(20)
    private final int tps;
    @NonNull
    private final ResourceManager resourceManager;
    @NonNull
    private final GameWindow window;
    @NonNull
    private final ScreenManager screenManager;
    @NonNull
    private final RenderSettings renderSettings;
    // Tasks
    private final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    private final Thread rpcThread;
    // Fonts.
    private final Font sansFont;
    // Cursors
    private final Cursor blankCursor;
    private final Cursor defaultCursor;
    private final Cursor pointerCursor;
    private final Cursor textCursor;
    // Font names.
    private final String fontName;
    // Rendering
    private final DebugRenderer debugRenderer = new DebugRenderer(this);
    private final EnvironmentRenderer environmentRenderer;
    // Managers.
    private final TextureManager textureManager = TextureManager.instance();
    // Randomizers.
    private final Random random = new Random();
    // Misc
    private final BufferedImage background = null;
    private final Object rpcUpdateLock = new Object();
    private final Ticker ticker = new Ticker();
    private final ScannerResult scanResults;
    // Utility objects.
    public InputController input;
    // Environment
    @Nullable
    public Environment environment;
    // Player entity
    public Player player;
    BufferedImage cachedImage;
    // Values
    @IntRange(from = 0)
    private int fps;
    private int currentTps;
    private PlayerController playerController;
    // Game states.
    private boolean loaded;
    private boolean crashed;
    private volatile boolean running = false;
    // Running value.
    // Threads
    private Thread renderingThread;
    private Thread tickingThread;
    private GarbageCollector garbageCollector;
    private float gameFrameTime;
    private boolean stopping;
    private Font monospaceFont;
    private Font pixelFont;
    private Font gameFont;
    // Loaded game.
    @Nullable
    private LoadedGame loadedGame;
    private boolean debugGuiOpen = false;
    private GlitchRenderer glitchRenderer = null;
    private boolean isGlitched = false;
    private Supplier<Activity> activity;
    private volatile boolean rpcUpdated;
    private Map<String, Long> lastProfile = new HashMap<>();
    private float fadeInDuration = Float.NEGATIVE_INFINITY;
    private boolean fadeIn = false;
    private long fadeInStart = 0L;

    /**
     * Class constructor for Bubble Blaster.
     *
     * @see LoadScreen
     */
    public BubbleBlaster() throws IOException {
        GameWindow.Properties windowProperties = new GameWindow.Properties("Bubble Blaster", 1280, 720).fullscreen();
        BubbleBlaster.BootOptions bootOptions = new BootOptions().tps(TPS);
        // Set default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(new GameExceptions(this));

        // Assign instance.
        instance = this;

        URL location = BubbleBlaster.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            gameFile = new File(location.toURI());
        } catch (URISyntaxException ignored) {
            throw new RuntimeException("Game file not found!");
        }

        Scanner scanner = new Scanner(true, List.of(getGameFile()), getClassLoader());
        scanResults = scanner.scan();

        // Set game properties.
        this.tps = bootOptions.tps;

        // Prepare for game launch
        this.resourceManager = new ResourceManager();
        this.renderSettings = new RenderSettings();

        // Setup game window.
        this.window = new GameWindow(windowProperties);

        // Prepare for loading.
        this.prepare();

        Bubbles.register();
        AmmoTypes.register();
        Entities.register();
        Effects.register();
        Abilities.register();
        GameplayEvents.register();
        Gamemodes.register();
        TextureCollections.register();

        // Load game with loading screen.
        this.load(new ProgressMessenger(new Messenger(this::log), 1000));
        this.screenManager = createScreenManager();
        BubbleBlaster.instance = this;

        setActivity(() -> {
            Activity activity = new Activity();
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

        DevClassPath classPath = GameDevMain.getClassPath();
        try {
            File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            if (file.isDirectory()) {
                resourceManager.importResources(new File(file, "../../../resources/main"));
            } else {
                resourceManager.importResources(file);
            }
        } catch (Exception e) {
            if (classPath != null)
                classPath.values().forEach(list -> list.forEach(file -> resourceManager.importResources(new File(file))));
            else
                throw new FileNotFoundException("Can't find bubble blaster game executable.");
        }


        // Assign instance.

        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getName().equals("JavaFX Application Thread")) {
                thread.setName("Application Thread");
            }
        }

        // Add ansi color compatibility in console.
        AnsiConsole.systemInstall();
        FileUtils.setCwd(References.GAME_DIR);

        // Transparent 16 x 16 pixel cursor image.
        BufferedImage nulCurImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                nulCurImg, new Point(0, 0), "blank cursor");

        // Transparent 16 x 16 pixel cursor image.
        BufferedImage defCurImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Polygon defCurPly = new Polygon(new int[]{0, 10, 5, 0}, new int[]{0, 12, 12, 16}, 4);

        Graphics2D defCurGfx = defCurImg.createGraphics();
        defCurGfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        defCurGfx.setColor(Color.black);
        defCurGfx.fillPolygon(defCurPly);
        defCurGfx.setColor(Color.white);
        defCurGfx.drawPolygon(defCurPly);
        defCurGfx.dispose();

        // Create a new blank cursor.
        defaultCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                defCurImg, new Point(1, 1), "default cursor");

        // Transparent 16 x 16 pixel cursor image.
        BufferedImage pntCurImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Polygon pntCurPly = new Polygon(new int[]{10, 20, 15, 10}, new int[]{10, 22, 22, 26}, 4);

        Graphics2D pntCurGfx = pntCurImg.createGraphics();
        pntCurGfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pntCurGfx.setColor(Color.white);
        pntCurGfx.drawOval(0, 0, 20, 20);
        pntCurGfx.setColor(Color.white);
        pntCurGfx.drawOval(2, 2, 16, 16);
        pntCurGfx.setColor(Color.black);
        pntCurGfx.fillPolygon(pntCurPly);
        pntCurGfx.setColor(Color.white);
        pntCurGfx.drawPolygon(pntCurPly);
        pntCurGfx.setColor(Color.black);
        pntCurGfx.drawOval(1, 1, 18, 18);
        pntCurGfx.dispose();

        // Create a new blank cursor.
        pointerCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                pntCurImg, new Point(11, 11), "pointer cursor");

        // Transparent 16 x 16 pixel cursor image.
        BufferedImage txtCurImg = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D txtCurGfx = txtCurImg.createGraphics();
        txtCurGfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        txtCurGfx.setColor(Color.white);
        txtCurGfx.drawLine(0, 1, 0, 24);
        txtCurGfx.setColor(Color.white);
        txtCurGfx.drawLine(1, 0, 1, 25);
        txtCurGfx.setColor(Color.white);
        txtCurGfx.drawLine(2, 1, 2, 24);
        txtCurGfx.setColor(Color.black);
        txtCurGfx.drawLine(1, 1, 1, 24);
        txtCurGfx.dispose();

        // Create a new blank cursor.
        textCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                txtCurImg, new Point(1, 12), "text cursor");

        // Hook output for logger.
//        System.setErr(new PrintStream(new CustomOutputStream("STDERR", Level.ERROR), true));
//        System.setOut(new PrintStream(new CustomOutputStream("STDOUT", Level.INFO), true));

        // Logs directory creation.
        References.LOGS_DIR.mkdirs();

        // Font Name
        fontName = "Chicle Regular";

        sansFont = loadFontInternally(id("arial_unicode"));

        // Register events.
        GameEvents.COLLECT_TEXTURES.listen(this::onCollectTextures);
        InputEvents.KEY_PRESS.listen(this::onKeyPress);
        InputEvents.KEY_RELEASE.listen(this::onKeyRelease);
        InputEvents.MOUSE_CLICK.listen(this::onMouseClick);

        // Register Game Font.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // Start scene-manager.
        try {
            Objects.requireNonNull(this.getScreenManager()).start();
        } catch (Throwable t) {
            CrashLog crashLog = new CrashLog("Oops, game crashed!", t);
            crash(t);
        }

        // Request focus
        getGameWindow().requestFocus();
    }

    public static Map<String, Long> getLastProfile() {
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
        return new Identifier(path, NAMESPACE);
    }

    /**
     * Launch method.
     * Contains argument parsing.
     */
    public static void main(String[] args, PreClassLoader classLoader) throws IOException {
        System.setProperty("log4j2.formatMsgNoLookups", "true"); // Fix CVE-2021-44228 exploit.
        // Get game-directory.
        for (String arg : args) {
            if (arg.startsWith("gameDir=")) {
                BubbleBlaster.gameDir = new File(arg.substring(8));
            }
            if (arg.equals("--debug")) {
                BubbleBlaster.debugMode = true;
            }
            if (arg.equals("--dev")) {
                BubbleBlaster.devMode = true;
            }
        }

        // Check if game-dir is assigned, if not the game-dir is not specified in the arguments.
        if (getGameDir() == null) {
            System.err.println("Game Directory is not specified!");
            System.exit(1);
        }

        BubbleBlaster.classLoader = classLoader;

        // Boot the game.
        BubbleBlaster.initEngine(BubbleBlaster.debugMode, BubbleBlaster.devMode);
        new BubbleBlaster();
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
        Screen currentScreen = getInstance().getCurrentScreen();
        return currentScreen != null && currentScreen.doesPauseGame();
    }

    /**
     * Game Events getter.
     *
     * @return The game event manager.
     */
    @Deprecated
    public static com.ultreon.bubbles.event.v1.bus.GameEvents getEventBus() {
        return com.ultreon.bubbles.event.v1.bus.GameEvents.get();
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

    private void onKeyPress(int keyCode, int scanCode, int modifiers, boolean holding) {
        final LoadedGame loadedGame = this.loadedGame;

        if (loadedGame != null) {
            final Environment environment = loadedGame.getEnvironment();

            if (!holding) {
                if (keyCode == KeyEvent.VK_SLASH && hasScreenOpen()) {
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
                Player player = loadedGame.getGamemode().getPlayer();

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

    private void onKeyRelease(int keyCode, int scanCode, int modifiers) {
        if (player != null) {
            if (keyCode == KEY_UP) player.forward(true);
            if (keyCode == KEY_DOWN) player.backward(true);
            if (keyCode == KEY_LEFT) player.left(true);
            if (keyCode == KEY_RIGHT) player.right(true);
        }
    }

    private void onMouseClick(int x, int y, int button, int clicks) {
        LoadedGame loadedGame = this.loadedGame;
        if (isDevMode()) {
            if (loadedGame != null && button == 1) {
                if (KeyInput.isDown(KEY_F1)) {
                    Objects.requireNonNull(loadedGame.getGamemode().getPlayer()).teleport(x, y);
                }
            }
        }
    }

    /**
     * Event handler for collecting textures.
     *
     * @param collection the texture collection being collected.
     */
    public void onCollectTextures(TextureCollection collection) {
        if (collection == TextureCollections.BUBBLE_TEXTURES.get()) {
            Collection<BubbleType> bubbles = Registers.BUBBLES.values();
            LoadScreen loadScreen = LoadScreen.get();

            if (loadScreen == null) {
                throw new IllegalStateException("Load scene is not available.");
            }
            for (BubbleType bubble : bubbles) {
                for (int i = 0; i <= bubble.getMaxRadius(); i++) {
                    TextureCollection.Index identifier = new TextureCollection.Index(bubble.id().location(), bubble.id().path() + "/" + i);
                    final int finalI = i;
                    collection.set(identifier, new ITexture() {
                        @Override
                        public void render(Renderer renderer) {
                            EnvironmentRenderer.drawBubble(renderer, 0, 0, finalI, bubble.colors);
                        }

                        @Override
                        public int width() {
                            return finalI + bubble.getColors().length * 2;
                        }

                        @Override
                        public int height() {
                            return finalI + bubble.getColors().length * 2;
                        }
                    });
                }
            }
        }
    }

    public File getGameFile() {
        return gameFile;
    }

    @SuppressWarnings("BusyWait")
    private void rpc() throws IOException {
        try {
            File discordLibrary = DownloadDiscordSDK.download();
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
        try (CreateParams params = new CreateParams()) {
            params.setClientID(933147296311427144L);
            params.setFlags(CreateParams.getDefaultFlags());

            // Create the Core
            try (Core core = new Core(params)) {
                // Create the Activity
                try (Activity activity = new Activity()) {
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
                Activity ret = activity.get();
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
        double tickCap = 1f / (double) getTps();

        double time = TimeProcessor.now();
        double unprocessed = 0;

        try {
            while (running) {
                boolean canTick = false;

                double time2 = TimeProcessor.now();
                double passed = time2 - time;
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
                        CrashLog crashLog = new CrashLog("Game being ticked.", t);
                        crash(crashLog.createCrash());
                    }
                }
            }
        } catch (Throwable t) {
            CrashLog crashLog = new CrashLog("Running game loop.", t);
            crash(crashLog.createCrash());
        }

        close();
    }

    private Font loadFontInternally(Identifier location) {
        try (InputStream inputStream = getClass().getResourceAsStream("/assets/" + location.location() + "/fonts/" + location.path() + ".ttf")) {
            if (inputStream == null) {
                throw new ResourceNotFoundException("Font resource " + location + " doesn't exists.");
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            Hydro.get().registerFont(font);
            return font;
        } catch (IOException | FontFormatException e) {
            throw new IOError(e);
        }
    }

    private Font loadFont(Identifier font) throws FontFormatException {
        Identifier identifier = new Identifier("fonts/" + font.path() + ".ttf", font.location());
        Resource resource = resourceManager.getResource(new Identifier("fonts/" + font.path() + ".ttf", font.location()));
        if (resource != null) {
            return resource.loadFont();
        } else {
            throw new NullPointerException("Resource is null: " + identifier);
        }
    }

    ////////////////////////
    //     Game flags     //
    ////////////////////////

    public void loadFonts() {
        try {
            gameFont = loadFont(id("chicle"));
            pixelFont = loadFont(id("pixel"));
            monospaceFont = loadFont(id("dejavu/dejavu_sans_mono"));
        } catch (FontFormatException | NullPointerException e) {
            if (e instanceof NullPointerException) {
                System.err.println("Couldn't load fonts.");
            }
            e.printStackTrace();
        }
    }

    public void gcThread() {
    }

    /**
     * Stops game-thread.
     */
    @RequiresNonNull({"ticking", "rendering", "gcThread"})
    public synchronized void stop() {
        try {
            renderingThread.join();
            tickingThread.join();
            garbageCollector.join();
            running = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClose() {
        // Shut-down game.
        logger.info("Shutting down Bubble Blaster");
        rpcThread.interrupt();

        try {
            rpcThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
        GameEvents.GAME_EXIT.factory().onExit(this);
    }

    /**
     * Todo: implement save loading.
     *
     * @param saveName ...
     */
    @SuppressWarnings("EmptyMethod")
    public void loadSave(String saveName) {
        createGame(new Random().nextLong());
    }

    /**
     * Todo: implement save loading.
     *
     * @param save save to load
     */
    public void loadSave(GameSave save) {
        createGame(new Random().nextLong());
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
        createGame(seed, Gamemodes.CLASSIC.get());
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
        long seed = save.getSeed();
        Gamemode gamemode = save.getGamemode();
        startGame(seed, gamemode, save, true);
    }

    /**
     * Start the game environment.
     */
    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public void startGame(long seed, Gamemode gamemode, GameSave save, boolean create) {
        // Start loading.
        MessengerScreen screen = new MessengerScreen();

        // Show environment loader screen.
        showScreen(screen);
        try {
            File directory = save.getDirectory();
            if (create && directory.exists()) {
                org.apache.commons.io.FileUtils.deleteDirectory(directory);
            }

            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Creating save folder failed.");
                }
            }

            Environment environment = this.environment = new Environment(save, gamemode, seed);

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

            LoadedGame loadedGame = new LoadedGame(save, this.environment);
            loadedGame.start();

            this.loadedGame = loadedGame;
        } catch (Throwable t) {
            t.printStackTrace();
            CrashLog crashLog = new CrashLog("Game save being loaded", t);
            crashLog.add("Save Directory", save.getDirectory());
            crashLog.add("Current Description", screen.getDescription());
            crashLog.add("Create Flag", create);
            crashLog.add("Seed", seed);
            crashLog.add("Gamemode", gamemode.id());
            crash(crashLog.createCrash());
            return;
        }

        BubbleBlaster.getInstance().showScreen(null);
    }

    /**
     * Quit game environment and loaded game.
     */
    public void quitLoadedGame() {
        LoadedGame loadedGame = getLoadedGame();
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
    @NonNull
    public ScreenManager getScreenManager() {
        return screenManager;
    }

    @NonNull
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
        LoadedGame loadedGame = getLoadedGame();
        if (loadedGame != null) {
            return loadedGame.getGameSave();
        }
        return null;
    }

    ///////////////////
    //     Fonts     //
    ///////////////////
    public Font getSansFont() {
        return sansFont;
    }

    public Font getMonospaceFont() {
        return monospaceFont;
    }

    public Font getPixelFont() {
        return pixelFont;
    }

    public Font getGameFont() {
        return gameFont;
    }

    /////////////////////
    //     Cursors     //
    /////////////////////
    public Cursor getBlankCursor() {
        return blankCursor;
    }

    public Cursor getTextCursor() {
        return textCursor;
    }

    public Cursor getPointerCursor() {
        return pointerCursor;
    }

    public Cursor getDefaultCursor() {
        return defaultCursor;
    }

    public String getFontName() {
        return fontName;
    }

    ///////////////////////
    //     Rendering     //
    ///////////////////////
    @NonNull
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
        double tickCap = 1000.0 / (double) tps;
        double tickTime = 0d;
        double gameFrameTime = 0d;
        int ticksPassed = 0;

        double time = System.currentTimeMillis();

        initialGameTick();

        try {
            while (running) {
                boolean canTick = false;

                double time2 = System.currentTimeMillis();
                double passed = time2 - time;
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
                        CrashLog crashLog = new CrashLog("Game being ticked.", t);
                        crash(crashLog.createCrash());
                    }
                }

                if (tickTime >= 1000.0d) {
                    currentTps = ticksPassed;
                    ticksPassed = 0;
                    tickTime = 0;
                }

                for (Runnable task : new ArrayList<>(tasks)) {
                    task.run();
                    tasks.remove(task);
                }
                Thread.sleep(8);
            }
        } catch (Throwable t) {
            CrashLog crashLog = new CrashLog("Running game loop.", t);
            crash(crashLog.createCrash());
        }

        close();
    }

    private void rendering() {
        double tickCap = 1f / (double) tps;
        double frameTime = 0d;
        double frames = 0;

        double time = TimeProcessor.now();
        this.gameFrameTime = 0;

        initialGameTick();

        try {
            while (running) {
                profiler.start();
                boolean canTick = false;

                double time2 = TimeProcessor.now();
                double passed = time2 - time;
                this.gameFrameTime += passed;
                frameTime += passed;

                time = time2;

                while (this.gameFrameTime >= tickCap) {
                    this.gameFrameTime -= tickCap;

                }

                if (frameTime >= 1.0d) {
                    frameTime = 0;
                    fps = (int) Math.round(frames);
                    frames = 0;
                }

                frames++;

                try {
                    profiler.startSection("render");
                    wrappedRender(fps);
                    profiler.endSection("render");
                } catch (Throwable t) {
                    CrashLog crashLog = new CrashLog("Game being rendered.", t);
                    crash(crashLog.createCrash());
                }

                lastProfile = profiler.end();
                Thread.sleep(8);
            }
        } catch (Throwable t) {
            CrashLog crashLog = new CrashLog("Running game loop.", t);
            crash(crashLog.createCrash());
        }

        close();
    }

    @SuppressWarnings("DuplicatedCode")
    private void renderLoop() {
        double frameTime = 0d;
        double frames = 0;

        double time = TimeProcessor.now();
        this.gameFrameTime = 0;

        try {
            while (running) {

                double time2 = TimeProcessor.now();
                double passed = time2 - time;
                this.gameFrameTime += passed;
                frameTime += passed;

                time = time2;

                if (frameTime >= 1.0d) {
                    frameTime = 0;
                    fps = (int) Math.round(frames);
                    frames = 0;
                }

                frames++;

                try {
                    wrappedRender(fps);
                } catch (Throwable t) {
                    CrashLog crashLog = new CrashLog("Game being rendered.", t);
                    crash(crashLog.createCrash());
                }

                for (Runnable task : tasks) {
                    task.run();
                }

                tasks.clear();
            }
        } catch (Throwable t) {
            CrashLog crashLog = new CrashLog("Running game loop.", t);
            crash(crashLog.createCrash());
        }

        close();
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

    /**
     * Render method, for rendering window.
     *
     * @param fps current game framerate.
     */
    private void wrappedRender(int fps) {
        BufferStrategy bs;
        Renderer mainRender;
        BufferRender bufferRender = new BufferRender(getBounds().getSize(), this.getObserver());
        Renderer render = bufferRender.getRenderer();

        // Set filter gotten from filter event-handlers.
        try {

            // Buffer strategy (triple buffering).
            bs = this.window.canvas.getBufferStrategy();

            // Create buffers if not created yet.
            if (bs == null) {
                this.window.canvas.createBufferStrategy(2);
                bs = this.window.canvas.getBufferStrategy();
            }

            // Get GraphicsProcessor and GraphicsProcessor objects.
            mainRender = new Renderer(bs.getDrawGraphics(), getObserver());
        } catch (IllegalStateException e) {
            cachedImage = bufferRender.done();
            this.window.finalSetup();
            return;
        }

        if (this.renderSettings.isAntialiasingEnabled() && this.isTextAntialiasEnabled())
            render.hint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (this.renderSettings.isAntialiasingEnabled() && this.isAntialiasEnabled())
            render.hint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        render.hint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        render.hint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        render.hint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        List<BufferedImageOp> filters = BubbleBlaster.instance.getCurrentFilters();

        profiler.section("renderGame", () -> this.render(render, gameFrameTime));

        profiler.section("draw", () -> {
            bufferRender.setFilters(List.of());

            // Clear background.
            profiler.section("Clear", () -> {
                mainRender.clearColor(Color.BLACK);
                mainRender.clearRect(0, 0, getWidth(), getHeight());
            });
            AffineTransform transform = new AffineTransform();
            transform.setToScale(getWidth(), getHeight());
            transform.setToTranslation(0, 0);

            // Draw filtered image.
            mainRender.renderedImage(bufferRender.done(), transform);
        });

        this.fps = fps;

        // Dispose and show.
        profiler.section("dispose", render::dispose);

        try {
            bs.show();
            mainRender.dispose();
            render.dispose();
            hasRendered = true;
        } catch (IllegalStateException ignored) {

        }
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
                RenderEvents.RENDER_SCREEN_BEFORE.factory().onRenderScreenBefore(screen, renderer);
                screen.render(this, renderer, getGameFrameTime());
                RenderEvents.RENDER_SCREEN_AFTER.factory().onRenderScreenAfter(screen, renderer);
            }
        });

        // Post render.
        profiler.section("Post Render", () -> postRender(renderer));

        // Post event after rendering the game.
        RenderEvents.RENDER_GAME_AFTER.factory().onRenderGameAfter(this, renderer, frameTime);
    }

    private void postRender(Renderer renderer) {
        if (isDebugMode() || debugGuiOpen) {
            debugRenderer.render(renderer);
        }

        if (isGlitched) {
            glitchRenderer.render(renderer);
        }

        if (fadeIn) {
            final long timeDiff = System.currentTimeMillis() - fadeInStart;
            if (timeDiff <= fadeInDuration) {
                int clamp = (int) MathHelper.clamp(255 * (1f - ((float) timeDiff) / fadeInDuration), 0, 255);
                Color color = new Color(0, 0, 0, clamp);
                GuiElement.fill(renderer, 0, 0, getWidth(), getHeight(), color);
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

        final Environment env = this.environment;
        final Player player = this.player;
        if (env != null && !isPaused()) {
            final Gamemode gamemode = env.getGamemode();
            if (gamemode != null) {
                if (player != null) {
                    if (player.getLevel() > 255) {
                        BubbleBlaster.getInstance().glitch();
                    }
                }

            }
            env.tick();
        }

        final Screen screen = this.getCurrentScreen();
        if (screen != null) {
            screen.tick();
        }

        if (player != null) {
            if (KeyInput.isDown(KEY_SPACE)) {
                player.shoot();
            }
        }

        if (ticker.advance() == 40) {
            ticker.reset();
            if (LoadScreen.isDone()) {
                if (isInMainMenus()) {
                    setActivity(() -> {
                        Activity activity = new Activity();
                        activity.setState("In the menus");
                        return activity;
                    });
                } else if (isInGame()) {
                    setActivity(() -> {
                        Activity activity = new Activity();
                        activity.setState("In-Game");
                        if (player != null) {
                            double score = player.getScore();
                            activity.setDetails("Score: " + (int) score);
                        } else {
                            activity.setDetails("?? ERROR ??");
                        }
                        return activity;
                    });
                } else {
                    setActivity(() -> {
                        Activity activity = new Activity();
                        activity.setState("Is nowhere to be found");
                        return activity;
                    });
                }
            }
        }

        BubbleBlaster.ticks++;
    }

    public void keyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == KEY_F12) {
            debugGuiOpen = !debugGuiOpen;
            logger.debug("Toggling debug gui");
        } else if (keyCode == KEY_F10 && (debugMode || devMode)) {
            Environment env = environment;
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
        glitchRenderer = new GlitchRenderer(this);
        getGameWindow().init();
    }

    /**
     * @return get the default font.
     */
    public Font getFont() {
        return getSansFont();
    }

    /**
     * @return the name create the game font.
     */
    public String getGameFontName() {
        return getGameFont().getFontName();
    }

    /**
     * @return the name create the pixel font.
     */
    public String getPixelFontName() {
        return getPixelFont().getFontName();
    }

    /**
     * @return the name create the monospace font.
     */
    public String getMonospaceFontName() {
        return getMonospaceFont().getFontName();
    }

    /**
     * @return the name create the sans font.
     */
    public String getSansFontName() {
        return getSansFont().getFontName();
    }

    public FontMetrics getFontMetrics(Font font) {
        return window.canvas.getFontMetrics(font);
    }

    public boolean hasScreenOpen() {
        return screenManager.getCurrentScreen() != null;
    }

    public Sound getSound(Identifier identifier) {
        Resource resource = resourceManager.getResource(identifier);

        return null;
    }

    public synchronized MP3Player playSound(Identifier identifier) {
        // The wrapper thread is unnecessary, unless it blocks on the
        // Clip finishing; see comments.
        //        new Thread(() -> {
        try {
            Identifier identifier1 = identifier.mapPath(path -> "audio/" + path + ".mp3");
            System.out.println("identifier1 = " + identifier1);
            Resource input = resourceManager.getResource(identifier1);
            System.out.println(input);
            MP3Player player = new MP3Player(identifier + "/" + UUID.randomUUID().toString().replaceAll("-", ""), Objects.requireNonNull(input).loadOrOpenStream());
            player.play();
            return player;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        }).start();
//        return null;
    }

    public synchronized Sound playSound(Identifier identifier, double volume) {
        // The wrapper thread is unnecessary, unless it blocks on the
        // Clip finishing; see comments.
        //        new Thread(() -> {
        try {
            Sound sound = new Sound(identifier, identifier + "/" + UUID.randomUUID().toString().replaceAll("-", ""));
            sound.setVolume(volume);
            sound.play();
            return sound;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        }).start();
//        return null;
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
    @NonNull
    public List<BufferedImageOp> getCurrentFilters() {
        return new ArrayList<>();
    }

    public void loadPlayEnvironment() {
        Player player = createPlayer();
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
        return window.observer;
    }

    public Rectangle getBounds() {
        return new Rectangle(0, 0, window.getWidth(), window.getHeight());
    }

    public int getWidth() {
        return window.canvas.getWidth();
    }

    public int getHeight() {
        return window.canvas.getHeight();
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

    public ScannerResult getScanResults() {
        return scanResults;
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
        try {
            tickingThread.join();
            renderingThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        close();
    }

    void close() {
        onClose();
        window.close();
        if (crashed) {
            System.exit(0);
        }
    }

    /**
     * Handler for game crash.
     *
     * @param crash the game crash.
     */
    public void crash(@NonNull ApplicationCrash crash) {
        CrashLog crashLog = crash.getCrashLog();
        crashed = true;

        boolean overridden = false;
        try {
            overridden = onCrash(crash);
        } catch (@NonNull Throwable ignored) {

        }

        if (!overridden) {
            crashLog.defaultSave();
            crash.printCrash();
        }

        System.exit(1);

        shutdown();
        close();
    }

    /**
     * Crash handler, for overriding default crash handling.
     *
     * @param crash the crash that happened.
     * @return whether the default should be overridden or not. The value {@code }
     */
    @SuppressWarnings("unused")
    public boolean onCrash(ApplicationCrash crash) {
//        GameEvents.get().publish(new CrashEvent(crash));
        GameEvents.CRASH.factory().onCrash(crash);
        return false;
    }

    @EnsuresNonNull({"ticking"})
    @SuppressWarnings("Convert2Lambda")
    void windowLoaded() {
        this.running = true;

        this.tickingThread = new Thread(BubbleBlaster.this::ticking, "Ticker");
        this.tickingThread.start();

        this.renderingThread = new Thread(BubbleBlaster.this::rendering, "Renderer");
        this.renderingThread.start();

        this.garbageCollector = new GarbageCollector(this);
        this.garbageCollector.setDaemon(true);
        this.garbageCollector.start();
    }

    public void crash(Throwable t) {
        CrashLog crashLog = new CrashLog("Unknown source", t);
        crash(crashLog.createCrash());
    }

    public void setup() {
        LanguageManager.INSTANCE.register(new Locale("af"), "African");
        LanguageManager.INSTANCE.register(new Locale("el"), "Greek");
        LanguageManager.INSTANCE.register(new Locale("it"), "Italian");
        LanguageManager.INSTANCE.register(new Locale("en"), "english");
        LanguageManager.INSTANCE.register(new Locale("es"), "Spanish");
        LanguageManager.INSTANCE.register(new Locale("nl"), "dutch");
        LanguageManager.INSTANCE.register(new Locale("fy"), "Frisk");
        LanguageManager.INSTANCE.register(new Locale("zh"), "Chinese");

        Set<Locale> locales = LanguageManager.INSTANCE.getLocales();
        for (Locale locale : locales) {
            LanguageManager.INSTANCE.load(locale, LanguageManager.INSTANCE.getLanguageID(locale), BubbleBlaster.getInstance().getResourceManager());
        }
    }

    public void finalizeSetup() {
        LanguageScreen.onPostInitialize();
    }

    public void resize(IntSize size) {
        screenManager.resize(size);
    }

    public void fadeIn(float time) {
        fadeIn = true;
        fadeInStart = System.currentTimeMillis();
        fadeInDuration = time;
    }

    protected static class BootOptions {
        private int tps = 40;

        public BootOptions() {

        }

        public BootOptions tps(int tps) {
            this.tps = tps;
            return this;
        }
    }
}
