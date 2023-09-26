package com.ultreon.bubbles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.event.v1.WindowEvents;
import com.ultreon.bubbles.input.GameInput;
import com.ultreon.bubbles.resources.ResourceFileHandle;
import com.ultreon.commons.exceptions.OneTimeUseException;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.size.IntSize;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.swing.*;
import java.util.Objects;

/**
 * Window class for the game's window.
 *
 * @author XyperCode
 */
@SuppressWarnings("unused")
public class GameWindow {
    private static final Marker MARKER = MarkerFactory.getMarker("GameWindow");

    private boolean initialized = false;

    @IntRange(from = 0)
    private int fps;
    private Thread mainThread;
    private boolean visible;

    /**
     * Window constructor.
     */
    public GameWindow(Properties properties) {
        setSize(new IntSize(properties.width, properties.height));
        Gdx.graphics.setResizable(false);
    }

    public IntSize getSize() {
        var width = Gdx.graphics.getWidth();
        var height = Gdx.graphics.getHeight();
        return new IntSize(width, height);
    }

    public void setSize(IntSize size) {
        Gdx.graphics.setWindowedMode(size.width(), size.height());
    }

    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    public void setWidth(int width) {
        Gdx.graphics.setWindowedMode(width, getSize().height());
    }

    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    public void setHeight(int height) {
        Gdx.graphics.setWindowedMode(getSize().width(), height);
    }

    /**
     * Initialized window.
     */
    public synchronized void init() {
        if (initialized) {
            throw new OneTimeUseException("The game window is already initialized.");
        }

        BubbleBlaster.getLogger().info(MARKER, "Post-init stage of game window.");

        this.initialized = true;

        BubbleBlaster.getLogger().info(MARKER, "Initialized game window");

        game().windowLoaded();
    }

    void dispose() {
        Gdx.app.exit();
    }

    public Cursor registerCursor(int hotSpotX, int hotSpotY, Identifier identifier) {
        return Gdx.graphics.newCursor(new Pixmap(new ResourceFileHandle(identifier.mapPath(s -> "textures/cursor/" + s + ".png"))), hotSpotX, hotSpotY);
    }

    private BubbleBlaster game() {
        return BubbleBlaster.getInstance();
    }

    public void finalSetup() {
        // TODO: Use final setup in game window.
    }

    public void toggleFullscreen() {
        this.setFullscreen(!this.isFullscreen());
    }

    public void setFullscreen(boolean enable) {
        if (!Constants.ALLOW_FULLSCREEN) {
            this.setVisible(true);
            this.requestFocus();
            return;
        }

        if (this.isFullscreen() && !enable) {
            if (!WindowEvents.WINDOW_FULLSCREEN.factory().onWindowFullscreen(this, false).isCanceled()) {
                this.setVisible(true);
                Gdx.graphics.setFullscreenMode(null);
                this.requestFocus();
            }
        } else if (!this.isFullscreen() && enable) {
            if (!WindowEvents.WINDOW_FULLSCREEN.factory().onWindowFullscreen(this, true).isCanceled()) {
                this.setVisible(true);
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                this.requestFocus();
            }
        }
    }

    public boolean isFullscreen() {
        return Gdx.graphics.isFullscreen();
    }

    public void setVisible(boolean visible) {
        Lwjgl3Window window = this.getLwjglWindow();
        window.setVisible(visible);
        this.visible = visible;

        if (visible) window.focusWindow();
    }

    private Lwjgl3Window getLwjglWindow() {
        return ((Lwjgl3Graphics) Gdx.graphics).getWindow();
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Rectangle getBounds() {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }

    public int getX() {
        return 0;
    }

    public int getY() {
        return 0;
    }

    @Nullable
    public Vec2i getMousePosition() {
        return new Vec2i(Gdx.input.getX(), Gdx.input.getY());
    }

    public void setCursor(Cursor cursor) {
        Gdx.graphics.setCursor(cursor);
    }

    public void setCursor(Cursor.SystemCursor cursor) {
        Gdx.graphics.setSystemCursor(cursor);

    }

    public void requestFocus() {
        this.getLwjglWindow().flash();
        this.getLwjglWindow().focusWindow();
    }

    public boolean isFocused() {
        return game().isFocused();
    }

    /**
     * Taskbar feature, flashes the taskbar icon on Windows.
     * Other operating systems are unknown for this behavior.
     */
    public void requestUserAttention() {
        this.getLwjglWindow().flash();
    }

    public void showError(@NotNull String title, @Nullable String description) {
        JOptionPane.showMessageDialog(null, description, title, JOptionPane.ERROR_MESSAGE);
    }

    public void setTitle(@NotNull String title) {
        Gdx.graphics.setTitle(title);
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public static class Properties {
        private final int width;
        private final int height;
        private final String title;
        private boolean fullscreen;
        private Runnable onClose = () -> {};

        @SuppressWarnings("ConstantConditions")
        public Properties(@NotNull String title, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
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
