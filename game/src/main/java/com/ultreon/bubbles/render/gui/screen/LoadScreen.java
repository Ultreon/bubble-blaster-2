package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.command.*;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.event.v2.GameEvents;
import com.ultreon.bubbles.event.v2.LifecycleEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.mod.ModDataManager;
import com.ultreon.bubbles.registry.Registry;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.TextureCollection;
import com.ultreon.bubbles.render.TextureManager;
import com.ultreon.bubbles.resources.Resource;
import com.ultreon.bubbles.util.GraphicsUtils;
import com.ultreon.bubbles.util.Util;
import com.ultreon.commons.lang.Messenger;
import com.ultreon.commons.lang.Pair;
import com.ultreon.commons.lang.ProgressMessenger;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unused")
public final class LoadScreen extends Screen implements Runnable {
    public static final Color BACKGROUND = Color.rgb(0x484848);
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
//    private final ModLoader modLoader = new ModLoader(Main.mainClassLoader);

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
        boolean done = isDone();
        if (done) return super.onClose(to);
        return false;
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis();
        }

        renderer.color(Color.rgb(0x484848));
        renderer.rect(0, 0, width, height);

        int i = 0;

        // Draw progress components.
        if (progMain != null) {
            int progress = progMain.getProgress();
            int max = progMain.getMax();

            // Draw current 1st line message.
            if (curMainMsg != null) {
                renderer.color(Color.rgb(0x808080));
                GraphicsUtils.drawCenteredString(renderer, curMainMsg, new Rectangle2D.Double(0, (double) BubbleBlaster.getInstance().getHeight() / 2 - 15, BubbleBlaster.getInstance().getWidth(), 30), new Font(game.getSansFontName(), Font.PLAIN, 20));
            }

            renderer.color(Color.rgb(0x808080));
            renderer.rect(BubbleBlaster.getInstance().getWidth() / 2 - 150, BubbleBlaster.getInstance().getHeight() / 2 + 15, 300, 3);

            setupGradient(renderer);
            renderer.rect(BubbleBlaster.getInstance().getWidth() / 2 - 150, BubbleBlaster.getInstance().getHeight() / 2 + 15, (int) (300d * (double) progress / (double) max), 3);

            // Draw 2nd progress components.
            if (progAlt != null) {
                int progressSub = progAlt.getProgress();
                int maxSub = progAlt.getMax();

                // Draw current 2nd line message.
                if (curAltMsg != null) {
                    renderer.color(Color.rgb(0x808080));
                    GraphicsUtils.drawCenteredString(renderer, curAltMsg, new Rectangle2D.Double(0, (double) BubbleBlaster.getInstance().getHeight() / 2 + 60, BubbleBlaster.getInstance().getWidth(), 30), new Font(game.getSansFontName(), Font.PLAIN, 20));
                }

                renderer.color(Color.rgb(0x808080));
                renderer.rect(BubbleBlaster.getInstance().getWidth() / 2 - 150, BubbleBlaster.getInstance().getHeight() / 2 + 90, 300, 3);

                setupGradient(renderer);
                renderer.rect(BubbleBlaster.getInstance().getWidth() / 2 - 150, BubbleBlaster.getInstance().getHeight() / 2 + 90, (int) (300d * (double) progressSub / (double) maxSub), 3);
            }
        }

        renderer.color(Color.rgb(0x7f7f7f));
    }

    private void setupGradient(Renderer renderer) {
        renderer.color(Color.rgb(0x00c0ff));
        GradientPaint p = new GradientPaint(0, (float) BubbleBlaster.getInstance().getWidth() / 2 - 150, Color.rgb(0x00c0ff).toAwt(), (float) BubbleBlaster.getInstance().getWidth() / 2 + 150, 0f, Color.rgb(0x00ffc0).toAwt());
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

        LOGGER.info("Loading resources...");
        this.progMain.sendNext("Loading resources...");
        game().getResourceManager().importResources(game.getGameFile());
        for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            List<Path> paths = container.getOrigin().getPaths();
            for (Path path : paths) {
                game().getResourceManager().importResources(path);
            }
        }
        this.progAlt = null;

        LOGGER.info("Setting up mods...");
        this.progMain.sendNext("Setting up mods...");
        for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = container.getMetadata();
            metadata.getIconPath(256).flatMap(container::findPath).ifPresentOrElse(path1 -> {
                try {
                    ModDataManager.setIcon(container, ImageIO.read(path1.toUri().toURL()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                try {
                    Resource resource = game.getResourceManager().getResource(BubbleBlaster.id("textures/mods/missing.png"));
                    if (resource == null) {
                        resource = TextureManager.DEFAULT_TEXTURE;
                    }
                    ModDataManager.setIcon(container, ImageIO.read(resource.getUrl()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        ModContainer container = FabricLoader.getInstance().getModContainer("java").orElseThrow();
        try {
            Resource resource = game.getResourceManager().getResource(BubbleBlaster.id("textures/mods/java.png"));
            if (resource == null) resource = TextureManager.DEFAULT_TEXTURE;
            System.out.println("ModDataManager.setIcon(container, ImageIO.read(resource.getUrl())) = " + ModDataManager.setIcon(container, ImageIO.read(resource.getUrl())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LifecycleEvents.SETUP.factory().onSetup(game);
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

        // Loading object holders
        this.progMain.sendNext("Loading the fonts...");
        game().loadFonts();
        GameEvents.LOAD_FONTS.factory().onLoadFonts(game::loadFont);
        this.progAlt = null;

        this.progMain.sendNext("Setting up the game...");
        initialize();
        this.progAlt = null;

        LOGGER.info("Setup the game...");
        this.progMain.sendNext("Setting up the game...");
        this.game.setup();
        this.progAlt = null;

        this.progMain.send("");
        this.progMain.increment();
        Collection<TextureCollection> values = Registry.TEXTURE_COLLECTIONS.values();
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

        game.finish();
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
        CommandConstructor.add("max-health", new MaxHealthCommand());
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
