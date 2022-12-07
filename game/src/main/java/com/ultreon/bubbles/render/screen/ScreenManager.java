package com.ultreon.bubbles.render.screen;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.vector.size.IntSize;
import com.ultreon.commons.annotation.MethodsReturnNonnullByDefault;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;

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
            LOGGER.debug("Hiding " + currentScreen.getClass().getSimpleName());
            if (currentScreen.onClose(screen) || force) {
                return initNextScreen(screen);
            }
            LOGGER.debug("Hiding " + currentScreen.getClass().getSimpleName() + " canceled.");
        } else {
            LOGGER.debug("Hiding <<NO-SCENE>>");
            return initNextScreen(screen);
        }
        return false;
    }

    private boolean initNextScreen(@Nullable Screen screen) {
        if (screen == null && game.isInMainMenus()) {
            screen = new TitleScreen();
        }
        this.currentScreen = screen;
        if (screen != null) {
            game.getGameWindow().setCursor(screen.getDefaultCursor());
        } else {
            game.getGameWindow().setCursor(game.getDefaultCursor());
        }

        if (currentScreen != null) {
            LOGGER.debug("Showing " + currentScreen.getClass().getSimpleName());
            this.currentScreen.init();
        } else {
            LOGGER.debug("Showing <<NO-SCENE>>");
        }
        return true;
    }

    public synchronized void start() {
        if (initialized) {
            throw new IllegalStateException("SceneManager already initialized.");
        }

        this.currentScreen = this.startScreen;
        LOGGER.debug("Showing " + currentScreen.getClass().getSimpleName());
        game.getGameWindow().setCursor(this.startScreen.getDefaultCursor());
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
