package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.command.*;
import com.ultreon.bubbles.data.GlobalSaveData;
import com.ultreon.bubbles.entity.bubble.BubbleSystem;
import com.ultreon.bubbles.event.v1.ConfigEvents;
import com.ultreon.bubbles.event.v1.GameEvents;
import com.ultreon.bubbles.event.v1.LifecycleEvents;
import com.ultreon.bubbles.mod.ModDataManager;
import com.ultreon.bubbles.registry.Registries;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.TextureCollection;
import com.ultreon.bubbles.render.TextureManager;
import com.ultreon.bubbles.settings.GameSettings;
import com.ultreon.bubbles.util.FileHandles;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unused")
public final class LoadScreen extends Screen implements Runnable {
    public static final Color BACKGROUND = Color.rgb(0x484848);
    private static final Color TEXT_COLOR = Color.rgb(0xc0c0c0);
    public static final Color PROGRESSBAR_BG = Color.rgb(0x808080);
    public static final float PROGRESS_BAR_WIDTH = 500f;

    private static final Logger LOGGER = LogManager.getLogger("Game-Loader");
    private static final float FADE_IN = 1000f;
    private static LoadScreen instance = null;
    private final List<Pair<String, Long>> messages = new CopyOnWriteArrayList<>();
    private Thread loadThread;
    private final String title = "";
    private final String description = "";
    private static boolean done;
    private volatile ProgressMessenger progressMain = null;
    private volatile ProgressMessenger progressAlt = null;
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
        if (this.progressMain != null) {
            int progress = this.progressMain.getProgress();
            int max = this.progressMain.getMax();

            renderer.setLineWidth(9.0f);

            // Draw current 1st line message.
            if (this.curMainMsg != null) {
                renderer.setColor(TEXT_COLOR);
                renderer.drawCenteredText(this.font, this.curMainMsg, this.width / 2f, this.height / 2f);
            }

            renderer.fill(this.width / 2f - PROGRESS_BAR_WIDTH / 2, this.height / 2 + 19 + 2, PROGRESS_BAR_WIDTH, 1, PROGRESSBAR_BG);

            renderer.setColor(Color.rgb(0x0040ff));
            int effectWidth = (int) (PROGRESS_BAR_WIDTH * (double) (progress + 1) / (double) max);
            if (effectWidth >= 1)
                renderer.fillEffect(this.width / 2f - PROGRESS_BAR_WIDTH / 2, this.height / 2 + 19, effectWidth, 5);

            // Draw 2nd progress components.
            if (progressAlt != null) {
                int progressSub = progressAlt.getProgress();
                int maxSub = progressAlt.getMax();

                // Draw current 2nd line message.
                if (curAltMsg != null) {
                    renderer.setColor(TEXT_COLOR);
                    renderer.drawCenteredText(font, curAltMsg, width / 2f, height / 2f + 75);
                }

                renderer.fill(this.width / 2f - PROGRESS_BAR_WIDTH / 2, this.height / 2 + 94 + 2, PROGRESS_BAR_WIDTH, 1, PROGRESSBAR_BG);

                int effectWidthSub = (int) (PROGRESS_BAR_WIDTH * (double) (progressSub + 1) / (double) maxSub);
                if (effectWidthSub >= 1)
                    renderer.fillEffect(this.width / 2f - PROGRESS_BAR_WIDTH / 2, this.height / 2 + 94, effectWidthSub, 5);
            }
        }
    }

    @Override
    public void run() {
        LOGGER.info("Loading started");

        GameSettings.nopInit();
        BubbleBlasterConfig.register();

        this.progressMain = new ProgressMessenger(msgMain, 11);

        // Get game directory in Java's File format.
        File gameDir = BubbleBlaster.getGameDir();

        // Check game directory exists, if not, create it!
        if (!gameDir.exists()) {
            if (!gameDir.mkdirs()) {
                throw new IllegalStateException("Game Directory isn't created!");
            }
        }

        LOGGER.info("Loading resources...");
        this.progressMain.sendNext("Loading resources...");
        try {
            game().getResourceManager().importPackage(game.getGameFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Collection<ModContainer> allMods = FabricLoader.getInstance().getAllMods();
        this.progressAlt = new ProgressMessenger(msgAlt, allMods.size());
        for (ModContainer container : allMods) {
            progressAlt.sendNext(container.getMetadata().getName());
            List<Path> paths = container.getOrigin().getPaths();
            for (Path path : paths) {
                try {
                    game().getResourceManager().importPackage(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.progressAlt = null;

        LOGGER.info("Setting up mods...");
        this.progressMain.sendNext("Setting up mods...");
        for (ModContainer container : allMods) {
            ModMetadata metadata = container.getMetadata();
            metadata.getIconPath(256).flatMap(container::findPath).ifPresentOrElse(path1 -> ModDataManager.setIcon(container, BubbleBlaster.invokeAndWait(() -> {
                try {
                    return new Texture(new Pixmap(FileHandles.imageBytes(path1.toUri().toURL())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })), () -> {
                Resource resource = game.getResourceManager().getResource(BubbleBlaster.id("textures/mods/missing.png"));
                if (resource == null) {
                    resource = TextureManager.DEFAULT_TEX_RESOURCE;
                }
                Resource finalResource = resource;
                ModDataManager.setIcon(container, BubbleBlaster.invokeAndWait(() -> {
                    try {
                        return new Texture(new Pixmap(FileHandles.imageBytes(finalResource.loadOrGet())));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
            });
        }

        this.addModIcon("java", "textures/mods/java.png");
        this.addModIcon("bubbleblaster", "icon.png");

        LifecycleEvents.SETUP.factory().onSetup(game);
        this.progressAlt = null;

        // Set up components in registry.
        this.progressMain.send("Setting up components...");
        this.progressMain.increment();
        Collection<Registry<?>> registries = Registry.getRegistries();
        this.progressAlt = new ProgressMessenger(msgAlt, registries.size());
        for (Registry<?> registry : registries) {
            this.progressAlt.send(registry.id().toString());
            this.progressAlt.increment();
            RegistryEvents.AUTO_REGISTER.factory().onAutoRegister(registry);
        }
        Registry.freeze();
        this.progressAlt = null;

        // Loading object holders
        this.progressMain.sendNext("Loading the fonts...");
        BubbleBlaster.invokeAndWait(() -> {
            game().loadFonts();
            GameEvents.LOAD_FONTS.factory().onLoadFonts(game::loadFont);
        });
        this.progressAlt = null;

        this.progressMain.sendNext("Setting up the game...");
        initialize();
        this.progressAlt = null;

        LOGGER.info("Setup the game...");
        this.progressMain.sendNext("Setting up the game...");
        this.game.setup();
        this.progressAlt = null;

        this.progressMain.send("");
        this.progressMain.increment();
        Collection<TextureCollection> values = Registries.TEXTURE_COLLECTIONS.values();
        this.progressAlt = new ProgressMessenger(this.msgAlt, values.size());
        for (TextureCollection collection : values) {
            GameEvents.COLLECT_TEXTURES.factory().onCollectTextures(collection);
            this.progressAlt.sendNext(String.valueOf(Registries.TEXTURE_COLLECTIONS.getKey(collection)));
        }
        this.progressAlt = null;

        // BubbleSystem
        this.progressMain.sendNext("Initialize bubble system...");
        BubbleSystem.init();

        // Load complete.
        this.progressMain.sendNext("Load Complete!");
        this.game.finalizeSetup();

        // Registry dump.
        this.progressMain.sendNext("Registry Dump.");
        Registry.dump();

        ConfigEvents.RELOAD_ALL.factory().onReloadAll();

        LoadScreen.done = true;

        BubbleBlaster.invoke(this.game::finish);
    }

    private void addModIcon(String modId, String path) {
        Resource resource = this.game.getResourceManager().getResource(BubbleBlaster.id(path));
        if (resource == null) resource = TextureManager.DEFAULT_TEX_RESOURCE;
        Resource finalResource = resource;
        ModDataManager.setIcon(modId, BubbleBlaster.invokeAndWait(() -> {
            try {
                return new Texture(new Pixmap(FileHandles.imageBytes(finalResource.loadOrGet())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
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
