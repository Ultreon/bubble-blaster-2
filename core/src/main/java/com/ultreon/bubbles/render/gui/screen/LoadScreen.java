package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.command.*;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.event.v1.LifecycleEvents;
import com.ultreon.bubbles.mod.ModDataManager;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.*;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.util.Util;
import com.ultreon.bubbles.util.Utils;
import com.ultreon.libs.commons.v0.Messenger;
import com.ultreon.libs.commons.v0.MessengerImpl;
import com.ultreon.libs.commons.v0.ProgressMessenger;
import com.ultreon.libs.commons.v0.tuple.Pair;
import com.ultreon.libs.registries.v0.Registry;
import com.ultreon.libs.registries.v0.event.RegistryEvents;
import com.ultreon.libs.resources.v0.Resource;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
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
    private volatile ProgressMessenger progMain = null;
    private volatile ProgressMessenger progAlt = null;
    private final Messenger msgMain = new MessengerImpl(this::logMain);
    private final Messenger msgAlt = new MessengerImpl(this::logAlt);
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
    public void init() {
        Utils.hideCursor();

        new Thread(this).start();
    }

    @Override
    public boolean onClose(Screen to) {
        boolean done = isDone();
        if (done) {
            Utils.showCursor();
            return super.onClose(to);
        }
        return false;
    }

    @Override
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis();
        }

        this.renderBackground(renderer);

        int i = 0;

        // Draw progress components.
        if (progMain != null) {
            int progress = progMain.getProgress();
            int max = progMain.getMax();

            renderer.setLineWidth(9.0f);

            // Draw current 1st line message.
            if (curMainMsg != null) {
                renderer.setColor(Color.rgb(0x808080));
                renderer.drawCenteredText(font, curMainMsg, width / 2f, height / 2f);
            }

            renderer.setColor(Color.rgb(0x808080));
            renderer.roundedLine(this.width / 2f - 150, this.height / 2f + 20, this.width / 2f + 150, this.height / 2f + 20);
            renderer.circle(this.width / 2f - 150, this.height / 2f + 20, 9f);
            renderer.circle(this.width / 2f + 150, this.height / 2f + 20, 9f);

            renderer.setColor(Color.rgb(0x0040ff));
            int effectWidth = (int) (300d * (double) progress / (double) max);
            if (effectWidth >= 1)
                renderer.fillEffect(this.width / 2 - 150, this.height / 2 + 19, effectWidth, 3);

            // Draw 2nd progress components.
            if (progAlt != null) {
                int progressSub = progAlt.getProgress();
                int maxSub = progAlt.getMax();

                // Draw current 2nd line message.
                if (curAltMsg != null) {
                    renderer.setColor(Color.rgb(0x808080));
                    renderer.drawCenteredText(font, curAltMsg, width / 2f, height / 2f + 75);
                }

                renderer.setColor(Color.rgb(0x808080));
                renderer.roundedLine(this.width / 2f - 150, this.height / 2f + 95, this.width / 2f + 150, this.height / 2f + 95);
                renderer.circle(this.width / 2f - 150, this.height / 2f + 95, 9f);
                renderer.circle(this.width / 2f + 150, this.height / 2f + 95, 9f);

                int effectWidthSub = (int) (300d * (double) progressSub / (double) maxSub);
                if (effectWidthSub >= 1)
                    renderer.fillEffect(this.width / 2 - 150, this.height / 2 + 94, effectWidthSub, 3);
            }
        }
    }

    private void setupGradient(Renderer renderer) {
//        renderer.setColor(Color.rgb(0x00c0ff));
//        GradientPaint p = new GradientPaint(0, (float) this.width / 2 - 150, Color.rgb(0x00c0ff).toAwt(), (float) this.width / 2 + 150, 0f, Color.rgb(0x00ffc0).toAwt());
//        renderer.paint(p);
    }

    @Override
    public void run() {
        LOGGER.info("Loading started");

        GameSettings.nopInit();

        this.progMain = new ProgressMessenger(msgMain, 11);

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
        try {
            game().getResourceManager().importPackage(game.getGameFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Collection<ModContainer> allMods = FabricLoader.getInstance().getAllMods();
        this.progAlt = new ProgressMessenger(msgAlt, allMods.size());
        for (ModContainer container : allMods) {
            progAlt.sendNext(container.getMetadata().getName());
            List<Path> paths = container.getOrigin().getPaths();
            for (Path path : paths) {
                try {
                    game().getResourceManager().importPackage(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.progAlt = null;

        LOGGER.info("Setting up mods...");
        this.progMain.sendNext("Setting up mods...");
        for (ModContainer container : allMods) {
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
                        resource = TextureManager.DEFAULT_TEX_RESOURCE;
                    }
                    ModDataManager.setIcon(container, resource.readImage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        ModContainer container = FabricLoader.getInstance().getModContainer("java").orElseThrow();
        try {
            Resource resource = game.getResourceManager().getResource(BubbleBlaster.id("textures/mods/java.png"));
            if (resource == null) resource = TextureManager.DEFAULT_TEX_RESOURCE;
            ModDataManager.setIcon(container, resource.readImage());
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
            RegistryEvents.AUTO_REGISTER.factory().onAutoRegister(registry);
        }
        Registry.freeze();
        this.progAlt = null;

        // Loading object holders
        this.progMain.sendNext("Loading the fonts...");
        BubbleBlaster.runLater(() -> {
            game().loadFonts();
            GameEvents.LOAD_FONTS.factory().onLoadFonts(game::loadFont);
        });
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
        Collection<TextureCollection> values = Registries.TEXTURE_COLLECTIONS.values();
        this.progAlt = new ProgressMessenger(this.msgAlt, values.size());
        for (TextureCollection collection : values) {
            GameEvents.COLLECT_TEXTURES.factory().onCollectTextures(collection);
            this.progAlt.sendNext(String.valueOf(Registries.TEXTURE_COLLECTIONS.getKey(collection)));
        }
        this.progAlt = null;

        // BubbleSystem
        this.progMain.sendNext("Initialize bubble system...");
        BubbleSystem.init();

        // Load complete.
        this.progMain.sendNext("Load Complete!");
        this.game.finalizeSetup();

        // Registry dump.
        this.progMain.sendNext("Registry Dump.");
        Registry.dump();

        LoadScreen.done = true;

        BubbleBlaster.invoke(this.game::finish);
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
        CommandConstructor.add("teleport", new TeleportCommand());
        CommandConstructor.add("game-over", new GameOverCommand());
        CommandConstructor.add("blood-moon", new BloodMoonCommand());

        try {
            GlobalSaveData.instance().load();
            GlobalSaveData.instance().dump();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void logAlt(String s) {
        this.curAltMsg = s;
    }

    private void logMain(String s) {
        this.curMainMsg = s;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
