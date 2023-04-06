package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.event.v2.EventResult;
import com.ultreon.bubbles.event.v2.ScreenEvents;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.vector.size.IntSize;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Scene manager, used for change between scenes.
 *
 * @see Screen
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ScreenManager {
    private static final Logger LOGGER = LogManager.getLogger("Screen-Manager");
    private final Screen startScreen;
    private final BubbleBlaster game;

    @Nullable
    private Screen currentScreen;
    private boolean initialized = false;

    private ScreenManager(Screen startScreen, BubbleBlaster game) {
        this.game = game;
        this.currentScreen = this.startScreen = startScreen;
    }

    public static ScreenManager create(Screen start, BubbleBlaster game) {
        return new ScreenManager(start, game);
    }

    /**
     * Display a new scene.
     *
     * @param scene the scene to display
     * @return if changing the scene was successful.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean displayScreen(@Nullable Screen scene) {
        return displayScreen(scene, false);
    }

    /**
     * Display a new scene.
     *
     * @param screen the scene to display
     * @return if changing the scene was successful.
     */
    public boolean displayScreen(@Nullable Screen screen, boolean force) {
        if (currentScreen != null) {
            if (currentScreen.onClose(screen) && !ScreenEvents.CLOSE.factory().onClose(screen, force) || force) {
                return initNextScreen(screen);
            }
        } else {
            return initNextScreen(screen);
        }
        return false;
    }

    private boolean initNextScreen(@Nullable Screen screen) {
        if (screen == null && game.isInMainMenus()) {
            screen = new TitleScreen();
        }
        EventResult<Screen> result = ScreenEvents.OPEN.factory().onOpen(screen);
        this.currentScreen = result.isInterrupted() ? result.getValue() : screen;

        game.getGameWindow().setCursor(screen != null ? screen.getDefaultCursor() : game.getDefaultCursor());

        if (currentScreen != null) {
            ScreenEvents.INIT.factory().onInit(this.currentScreen);
            this.currentScreen.init();
        } else {
            LOGGER.debug("Showing <<NO-SCENE>>");
        }
        return true;
    }

    public synchronized void start() {
        if (this.initialized) {
            throw new IllegalStateException("SceneManager already initialized.");
        }

        EventResult<Screen> result = ScreenEvents.OPEN.factory().onOpen(this.startScreen);
        this.currentScreen = result.isInterrupted() ? result.getValue() : this.startScreen;
        this.game.getGameWindow().setCursor(this.startScreen.getDefaultCursor());

        ScreenEvents.INIT.factory().onInit(this.currentScreen);
        this.startScreen.init();
        this.initialized = true;
    }

    @Nullable
    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void resize(IntSize size) {
        final Screen screen = currentScreen;
        if (screen != null) {
            screen.resize(size.width(), size.height());
        }
    }
}
