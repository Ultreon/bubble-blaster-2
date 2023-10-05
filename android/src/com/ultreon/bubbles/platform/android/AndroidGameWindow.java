package com.ultreon.bubbles.platform.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.*;
import com.ultreon.bubbles.event.v1.WindowEvents;
import com.ultreon.bubbles.GamePlatform;
import com.ultreon.commons.exceptions.OneTimeUseException;
import com.ultreon.libs.commons.v0.Identifier;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Objects;

/**
 * Window class for the game's window.
 *
 * @author XyperCode
 */
@SuppressWarnings("unused")
public class AndroidGameWindow implements GameWindow {
    private static final Marker MARKER = MarkerFactory.getMarker("GameWindow");

    private boolean initialized = false;

    @IntRange(from = 0)
    private int fps;
    private Thread mainThread;
    private boolean visible;

    /**
     * Window constructor.
     */
    public AndroidGameWindow(Properties properties) {
        this.setSize(properties.width, properties.height);
        Gdx.graphics.setResizable(false);
    }

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public void setWidth(int width) {
        Gdx.graphics.setWindowedMode(width, this.getHeight());
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    public void setHeight(int height) {
        Gdx.graphics.setWindowedMode(this.getWidth(), height);
    }

    /**
     * Initialized window.
     */
    public synchronized void init() {
        if (this.initialized) {
            throw new OneTimeUseException("The game window is already initialized.");
        }
        this.initialized = true;

        BubbleBlaster.getLogger().info(MARKER, "Initialized game window");

        this.game().windowLoaded();
    }

    public void dispose() {
        Gdx.app.exit();
    }

    public Cursor registerCursor(int hotSpotX, int hotSpotY, Identifier identifier) {
        return Gdx.graphics.newCursor(new Pixmap(BubbleBlaster.resource(identifier.mapPath(s -> "textures/cursor/" + s + ".png"))), hotSpotX, hotSpotY);
    }

    public void finalSetup() {
        // TODO: Use final setup in game window.
    }

    public void toggleFullscreen() {
        this.setFullscreen(!this.isFullscreen());
    }

    public void setFullscreen(boolean enable) {
        if (this.isFullscreen() && !enable) {
            if (!WindowEvents.WINDOW_FULLSCREEN.factory().onWindowFullscreen(this, false).isCanceled()) {
                this.setVisible(true);
                Gdx.graphics.setWindowedMode(Constants.DEFAULT_SIZE.x, Constants.DEFAULT_SIZE.y);
                this.requestFocus();
            }
        } else if (!this.isFullscreen() && enable) {
            if (!WindowEvents.WINDOW_FULLSCREEN.factory().onWindowFullscreen(this, true).isCanceled()) {
                this.setVisible(true);
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
                this.game().resize(mode.width, mode.height);
                this.requestFocus();
            }
        }

        BubbleBlasterConfig.FULLSCREEN.set(enable);
        BubbleBlasterConfig.save();
    }

    public void setVisible(boolean visible) {

    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public Rectangle getBounds() {
        return new Rectangle(0, 0, this.getWidth(), this.getHeight());
    }

    public int getX() {
        return 0;
    }

    public int getY() {
        return 0;
    }

    public void requestFocus() {

    }

    public boolean isFocused() {
        return this.game().isFocused();
    }

    /**
     * Taskbar feature, flashes the taskbar icon on Windows.
     * Other operating systems are unknown for this behavior.
     */
    public void requestUserAttention() {

    }

    public void showError(@NotNull String title, @Nullable String description) {
        AndroidPlatform platform = (AndroidPlatform) GamePlatform.get();
        AndroidLauncher launcher = platform.getLauncher();
        launcher.showMessage(title, description);
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public static class Properties {
        private final int width;
        private final int height;
        private final String title;
        private boolean fullscreen;
        private Runnable onClose = () -> {};

        public Properties(@NotNull String title, int width, int height) {
            if (width < 0) throw new IllegalArgumentException("Width is negative");
            if (height < 0) throw new IllegalArgumentException("Height is negative");
            Objects.requireNonNull(title, "Title is set to null");

            this.width = width;
            this.height = height;
            this.title = title;
        }

        @Contract("->this")
        public Properties fullscreen() {
            this.fullscreen = true;
            return this;
        }

        public Properties close(Runnable onClose) {
            this.onClose = onClose;
            return this;
        }
    }
}
