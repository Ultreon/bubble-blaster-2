package com.ultreon.bubbles;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.files.FileHandle;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.screen.Screen;
import com.ultreon.commons.os.OperatingSystem;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.Messenger;
import com.ultreon.libs.commons.v0.ProgressMessenger;
import com.ultreon.libs.crash.v0.CrashLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

public abstract class GamePlatform {
    private static GamePlatform instance;
    private boolean debugGuiOpen;

    public static GamePlatform get() {
        return instance;
    }

    public GamePlatform() {
        if (instance != null) throw new IllegalStateException("Game platform is already created.");
        instance = this;
    }

    public abstract FileHandle data(String path);

    public boolean isMobile() {
        switch (this.getOperatingSystem()) {
            case Android:
            case IOS:
                return true;
            default:
                return false;
        }
    }

    public abstract Logger getLogger(String name);

    public abstract OperatingSystem getOperatingSystem();

    public void handleCrash(CrashLog crashLog) {
    }

    public abstract FileHandle getDataDirectory();

    public boolean isDebug() {
        return false;
    }

    public boolean isDevelopmentEnvironment() {
        return false;
    }

    @Nullable
    public abstract Screen openModListScreen();

    public void setupMods() {

    }

    public void loadModResources() {

    }

    protected BubbleBlaster game() {
        return BubbleBlaster.getInstance();
    }

    public void addModIcon(String modId, Identifier path) {

    }

    public abstract void loadModResources(AtomicReference<ProgressMessenger> progressAlt, Messenger msgAlt);

    public abstract void loadGameResources(AtomicReference<ProgressMessenger> progressAlt, Messenger msgAlt);

    public int getModsCount() {
        return 0;
    }

    public boolean allowsMods() {
        return false;
    }

    public void renderImGui(Renderer renderer) {

    }

    public void dispose() {

    }

    public void initMods() {

    }

    public boolean isDebugGuiOpen() {
        return this.debugGuiOpen;
    }

    @Nullable
    public String getFabricLoaderVersion() {
        return null;
    }

    public String getLibGDXVersion() {
        return Version.VERSION;
    }

    public abstract String getGameVersion();

    public void initImGui() {

    }

    public void toggleDebugGui() {
        this.debugGuiOpen = !this.debugGuiOpen;
    }

    public boolean isCollisionShapesShown() {
        return false;
    }

    public GameWindow createWindow(GameWindow.Properties properties) {
        return new DefaultGameWindow(properties);
    }

    public boolean isModLoaded(String modId) {
        return false;
    }

    public boolean isDesktop() {
        return false;
    }

    public abstract void showError(@NotNull String title, @Nullable String description);

    public void create() {

    }

    public int getRecommendedFPS() {
        return 60;
    }
}
