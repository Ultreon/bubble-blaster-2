package com.ultreon.bubbles.platform.desktop;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.google.common.base.Suppliers;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.DesktopGameWindow;
import com.ultreon.bubbles.GamePlatform;
import com.ultreon.bubbles.GameWindow;
import com.ultreon.bubbles.platform.desktop.imgui.ImGuiRenderer;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.TextureManager;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.bubbles.util.FileHandles;
import com.ultreon.commons.os.OperatingSystem;
import com.ultreon.gameprovider.bubbles.OS;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.Messenger;
import com.ultreon.libs.commons.v0.ProgressMessenger;
import com.ultreon.libs.crash.v0.CrashLog;
import com.ultreon.libs.resources.v0.Resource;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;
import net.fabricmc.loader.impl.util.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ultreon.bubbles.BubbleBlaster.NAMESPACE;

public class DesktopPlatform extends GamePlatform {
    private static final Supplier<ModContainer> FABRIC_LOADER_CONTAINER = Suppliers.memoize(() -> FabricLoader.getInstance().getModContainer("fabricloader").orElseThrow());
    private static final Supplier<ModMetadata> FABRIC_LOADER_META = Suppliers.memoize(() -> FABRIC_LOADER_CONTAINER.get().getMetadata());
    private static final Supplier<Version> FABRIC_LOADER_VERSION = Suppliers.memoize(() -> FABRIC_LOADER_META.get().getVersion());
    private static final Supplier<ModContainer> LIBGDX_CONTAINER = Suppliers.memoize(() -> FabricLoader.getInstance().getModContainer("libgdx").orElseThrow());
    private static final Supplier<ModMetadata> LIBGDX_META = Suppliers.memoize(() -> LIBGDX_CONTAINER.get().getMetadata());
    private static final Supplier<Version> LIBGDX_VERSION = Suppliers.memoize(() -> LIBGDX_META.get().getVersion());
    private static final Supplier<ModContainer> GAME_CONTAINER = Suppliers.memoize(() -> FabricLoader.getInstance().getModContainer(NAMESPACE).orElseThrow());
    private static final Supplier<ModMetadata> GAME_META = Suppliers.memoize(() -> GAME_CONTAINER.get().getMetadata());
    private static final Supplier<Version> GAME_VERSION = Suppliers.memoize(() -> GAME_META.get().getVersion());

    private final Arguments arguments;
    private final boolean debug;
    private final FileHandle dataDir;
    private final URL gameFile;

    public DesktopPlatform(Arguments arguments) {
        this.arguments = arguments;
        this.debug = this.arguments.getExtraArgs().contains("--debug");

        Path path;
        if (OS.isWindows()) path = Paths.get(System.getenv("APPDATA"), "BubbleBlaster");
        else if (OS.isMacintosh()) path = Paths.get(System.getProperty("user.home"), "Library/Application Support/BubbleBlaster");
        else if (OS.isLinux()) path = Paths.get(System.getProperty("user.home"), ".config/BubbleBlaster");
        else throw new RuntimeException("Unsupported platform " + System.getProperty("os.name"));

        this.dataDir = new FileHandle(path.toFile());
        this.gameFile = BubbleBlaster.class.getProtectionDomain().getCodeSource().getLocation();
    }

    @Override
    public GameWindow createWindow(GameWindow.Properties properties) {
        return new DesktopGameWindow(properties);
    }

    @Override
    public FileHandle data(String path) {
        return this.dataDir.child(path);
    }

    @Override
    public Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    @Override
    public OperatingSystem getOperatingSystem() {
        if (OS.isWindows()) return OperatingSystem.Windows;
        if (OS.isLinux()) return OperatingSystem.Linux;
        if (OS.isMacintosh()) return OperatingSystem.MacOS;
        else throw new UnsupportedOperationException("Unsupported operating system");
    }

    @Override
    public boolean isDebug() {
        return this.debug;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Screen openModListScreen() {
        return new ModListScreen();
    }

    @Override
    public void setupMods() {
        for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = container.getMetadata();
            metadata.getIconPath(256).flatMap(container::findPath).ifPresentOrElse(path1 -> ModDataManager.setIcon(container, BubbleBlaster.invokeAndWait(() -> {
                try {
                    return new Texture(new Pixmap(FileHandles.imageBytes(path1.toUri().toURL())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })), () -> {
                Resource resource = this.game().getResourceManager().getResource(BubbleBlaster.id("textures/mods/missing.png"));
                if (resource == null) {
                    resource = TextureManager.DEFAULT_TEX_RESOURCE;
                }
                Resource finalResource = resource;
                ModDataManager.setIcon(container, BubbleBlaster.invokeAndWait(() -> new Texture(new Pixmap(FileHandles.imageBytes(finalResource.loadOrGet())))));
            });
        }

        this.addModIcon("java", BubbleBlaster.id("textures/mods/java.png"));
        this.addModIcon("bubbleblaster", BubbleBlaster.id("icon.png"));
    }

    public void addModIcon(String modId, Identifier location) {
        Resource resource = this.game().getResourceManager().getResource(location);
        if (resource == null) resource = TextureManager.DEFAULT_TEX_RESOURCE;
        Resource finalResource = resource;
        ModDataManager.setIcon(modId, BubbleBlaster.invokeAndWait(() -> new Texture(new Pixmap(FileHandles.imageBytes(finalResource.loadOrGet())))));
    }

    @Override
    public void loadModResources(AtomicReference<ProgressMessenger> progressAlt, Messenger msgAlt) {
        Collection<ModContainer> allMods = FabricLoader.getInstance().getAllMods();
        ProgressMessenger progressMessenger = new ProgressMessenger(msgAlt, allMods.size());
        progressAlt.set(progressMessenger);
        for (ModContainer container : allMods) {
            progressAlt.get().sendNext(container.getMetadata().getName());
            ModOrigin origin = container.getOrigin();
            if (origin.getKind() == ModOrigin.Kind.PATH) {
                List<Path> paths = origin.getPaths();
                for (Path path : paths) {
                    try {
                        this.game().getResourceManager().importPackage(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        progressAlt.set(null);
    }

    @Override
    public void loadGameResources(AtomicReference<ProgressMessenger> progressAlt, Messenger msgAlt) {
        try {
            this.game().getResourceManager().importPackage(this.getGameFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL getGameFile() {
        return gameFile;
    }

    @Override
    public FileHandle getDataDirectory() {
        return this.dataDir;
    }

    public String getArg(String key) {
        return this.arguments.get(key);
    }

    public String getArgOrDefault(String key, String value) {
        return this.arguments.getOrDefault(key, value);
    }

    public List<String> getExtraArgs() {
        return this.arguments.getExtraArgs();
    }

    public boolean hasArg(String key) {
        return this.arguments.containsKey(key);
    }

    @Override
    public boolean allowsMods() {
        return true;
    }

    @Override
    public int getModsCount() {
        return FabricLoader.getInstance().getAllMods().stream().filter(modContainer -> modContainer.getOrigin().getKind() == ModOrigin.Kind.PATH).collect(Collectors.toList()).size();
    }

    @Override
    public void initImGui() {
        ImGuiRenderer.init();
    }

    @Override
    public void renderImGui(Renderer renderer) {
        ImGuiRenderer.render(renderer);
    }

    @Override
    public void dispose() {
        ImGuiRenderer.dispose();
    }

    @Override
    public void initMods() {
        // Invoke entry points.
        EntrypointUtils.invoke("main", ModInitializer.class, ModInitializer::onInitialize);
    }

    @Override
    public String getFabricLoaderVersion() {
        return FABRIC_LOADER_VERSION.get().getFriendlyString();
    }

    @Override
    public String getLibGDXVersion() {
        return LIBGDX_VERSION.get().getFriendlyString();
    }

    @Override
    public String getGameVersion() {
        return GAME_VERSION.get().getFriendlyString();
    }

    @Override
    public void toggleDebugGui() {
        ImGuiRenderer.DEBUG_GUI_OPEN.set(!ImGuiRenderer.DEBUG_GUI_OPEN.get());
    }

    @Override
    public boolean isCollisionShapesShown() {
        return ImGuiRenderer.SHOW_COLLISIONS_SHAPES.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDesktop() {
        return true;
    }

    @Override
    public void handleCrash(CrashLog crashLog) {
        crashLog.writeToFile(new File(GamePlatform.get().data("game-crashes").file(), crashLog.getDefaultFileName()));
    }
}
