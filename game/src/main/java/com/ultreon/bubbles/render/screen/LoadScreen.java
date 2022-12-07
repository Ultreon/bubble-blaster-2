package com.ultreon.bubbles.render.screen;

import com.ultreon.bubbles.Main;
import com.ultreon.bubbles.command.*;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.event.v2.GameEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.mod.loader.ModLoader;
import com.ultreon.bubbles.registry.Registers;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.TextureCollection;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.bubbles.util.Util;
import com.ultreon.commons.lang.Messenger;
import com.ultreon.commons.lang.Pair;
import com.ultreon.commons.lang.ProgressMessenger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public final class LoadScreen extends Screen implements Runnable {
    public static final Color BACKGROUND = new Color(72, 72, 72);
    private static final Logger LOGGER = LogManager.getLogger("Game-Loader");
    private static final float FADE_IN = 1000f;
    private static LoadScreen instance = null;
    private final List<Pair<String, Long>> messages = new CopyOnWriteArrayList<>();
    private Thread loadThread;
    private final String title = "";
    private final String description = "";
    private static boolean done;
    private ProgressMessenger progMain = null;
    private ProgressMessenger progAlt = null;
    private final Messenger msgMain = new Messenger(this::logMain);
    private final Messenger msgAlt = new Messenger(this::logAlt);
    private String curMainMsg = "";
    private String curAltMsg = "";
    private long startTime;
    private final ModLoader modLoader = new ModLoader(Main.mainClassLoader);

    public LoadScreen() {
        instance = this;
    }

    public static LoadScreen get() {
        return done ? null : instance;
    }

    @Override
    public Cursor getDefaultCursor() {
        return BubbleBlaster.getInstance().getBlankCursor();
    }

    @Override
    public void init() {
        LOGGER.info("Showing LoadScene");

        BubbleBlaster.getInstance().getGameWindow().setCursor(BubbleBlaster.getInstance().getDefaultCursor());

        new Thread(this).start();
    }

    @Override
    public boolean onClose(Screen to) {
        return isDone();
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis();
        }

        renderer.color(new Color(72, 72, 72));
        renderer.rect(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());

        int i = 0;

        // Draw progress components.
        if (progMain != null) {
            int progress = progMain.getProgress();
            int max = progMain.getMax();

            // Draw current 1st line message.
            if (curMainMsg != null) {
                renderer.color(new Color(128, 128, 128));
                GraphicsUtils.drawCenteredString(renderer, curMainMsg, new Rectangle2D.Double(0, (double) BubbleBlaster.getInstance().getHeight() / 2 - 15, BubbleBlaster.getInstance().getWidth(), 30), new Font(game.getSansFontName(), Font.PLAIN, 20));
            }

            renderer.color(new Color(128, 128, 128));
            renderer.rect(BubbleBlaster.getInstance().getWidth() / 2 - 150, BubbleBlaster.getInstance().getHeight() / 2 + 15, 300, 3);

            setupGradient(renderer);
            renderer.rect(BubbleBlaster.getInstance().getWidth() / 2 - 150, BubbleBlaster.getInstance().getHeight() / 2 + 15, (int) (300d * (double) progress / (double) max), 3);

            // Draw 2nd progress components.
            if (progAlt != null) {
                int progressSub = progAlt.getProgress();
                int maxSub = progAlt.getMax();

                // Draw current 2nd line message.
                if (curAltMsg != null) {
                    renderer.color(new Color(128, 128, 128));
                    GraphicsUtils.drawCenteredString(renderer, curAltMsg, new Rectangle2D.Double(0, (double) BubbleBlaster.getInstance().getHeight() / 2 + 60, BubbleBlaster.getInstance().getWidth(), 30), new Font(game.getSansFontName(), Font.PLAIN, 20));
                }

                renderer.color(new Color(128, 128, 128));
                renderer.rect(BubbleBlaster.getInstance().getWidth() / 2 - 150, BubbleBlaster.getInstance().getHeight() / 2 + 90, 300, 3);

                setupGradient(renderer);
                renderer.rect(BubbleBlaster.getInstance().getWidth() / 2 - 150, BubbleBlaster.getInstance().getHeight() / 2 + 90, (int) (300d * (double) progressSub / (double) maxSub), 3);
            }
        }

        renderer.color(new Color(127, 127, 127));
    }

    private void setupGradient(Renderer renderer) {
        renderer.color(new Color(0, 192, 255));
        GradientPaint p = new GradientPaint(0, (float) BubbleBlaster.getInstance().getWidth() / 2 - 150, new Color(0, 192, 255), (float) BubbleBlaster.getInstance().getWidth() / 2 + 150, 0f, new Color(0, 255, 192));
        renderer.paint(p);
    }

    @Override
    public void run() {
        this.progMain = new ProgressMessenger(msgMain, 12);

        // Get game directory in Java's File format.
        File gameDir = BubbleBlaster.getGameDir();

        // Check game directory exists, if not, create it!
        if (!gameDir.exists()) {
            if (!gameDir.mkdirs()) {
                throw new IllegalStateException("Game Directory isn't created!");
            }
        }

        LOGGER.info("Loading mods...");
        this.progMain.sendNext("Loading mods...");
        modLoader.scanForJars();
        this.progAlt = null;

        LOGGER.info("Scanning mods...");
        this.progMain.sendNext("Scanning mods...");
        modLoader.scan(msgAlt, new AtomicReference<>(progAlt));
        this.progAlt = null;

        LOGGER.info("Loading resources...");
        this.progMain.sendNext("Loading resources...");
        game().getResourceManager().importResources(game.getGameFile());
        for (File file : modLoader.getFiles()) {
            game().getResourceManager().importResources(file);
        }
        this.progAlt = null;

        LOGGER.info("Setting up mods...");
        this.progMain.sendNext("Setting up mods...");
        modLoader.init(msgAlt, new AtomicReference<>(progAlt));
        this.progAlt = null;

        // Loading object holders
        this.progMain.sendNext("Loading the fonts...");
        game().loadFonts();
        this.progAlt = null;

        this.progMain.sendNext("Setting up the game...");
        initialize();
        this.progAlt = null;

        LOGGER.info("Setup the game...");
        this.progMain.sendNext("Setting up the game...");
        this.game.setup();
        this.progAlt = null;

        // Set up components in registry.
        this.progMain.send("Setting up components...");
        this.progMain.increment();
        Collection<Registry<?>> registries = Registry.getRegistries();
        this.progAlt = new ProgressMessenger(msgAlt, registries.size());
        for (Registry<?> registry : registries) {
            this.progAlt.send(registry.id().toString());
            this.progAlt.increment();
            GameEvents.AUTO_REGISTER.factory().onAutoRegister(registry);
            registry.freeze();
        }
        this.progAlt = null;

        this.progMain.send("");
        this.progMain.increment();
        Collection<TextureCollection> values = Registers.TEXTURE_COLLECTIONS.values();
        this.progAlt = new ProgressMessenger(this.msgAlt, values.size());
        for (TextureCollection collection : values) {
            GameEvents.COLLECT_TEXTURES.factory().onCollectTextures(collection);
            this.progAlt.increment();
        }

        // BubbleSystem
        this.progMain.sendNext("Initialize bubble system...");
        BubbleSystem.init();

        // Load complete.
        this.progMain.sendNext("Load Complete!");
        game.finalizeSetup();

        // Registry dump.
        this.progMain.sendNext("Registry Dump.");
        Registry.dump();
        GameEvents.REGISTRY_DUMP.factory().onDump();

        LoadScreen.done = true;

        Util.getGame().getScreenManager().displayScreen(new TitleScreen());
    }

    private BubbleBlaster game() {
        return this.game;
    }

    public static boolean isDone() {
        return LoadScreen.done;
    }

    public void initialize() {
        BubbleBlaster main = Util.getGame();

        // Request focus.
        game.getGameWindow().requestFocus();

        // Commands
        CommandConstructor.add("tp", new TeleportCommand());
        CommandConstructor.add("heal", new HealCommand());
        CommandConstructor.add("level", new LevelCommand());
        CommandConstructor.add("health", new HealthCommand());
        CommandConstructor.add("score", new ScoreCommand());
        CommandConstructor.add("effect", new EffectCommand());
        CommandConstructor.add("spawn", new SpawnCommand());
        CommandConstructor.add("shutdown", new ShutdownCommand());
        CommandConstructor.add("teleport", new TeleportCommand());
        CommandConstructor.add("game-over", new GameOverCommand());
        CommandConstructor.add("blood-moon", new BloodMoonCommand());

        try {
            GlobalSaveData.instance().load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void logAlt(String s) {
        this.curAltMsg = s;
    }

    private void logMain(String s) {
        this.curMainMsg = s;
    }
}
