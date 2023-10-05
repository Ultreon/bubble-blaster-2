package com.ultreon.bubbles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.event.v1.WindowEvents;
import com.ultreon.libs.commons.v0.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Window class for the game's window.
 *
 * @author XyperCode
 */
@SuppressWarnings("unused")
public interface GameWindow {
    /**
     * Window constructor.
     */
    static GameWindow create(Properties properties) {
        GameWindow window = GamePlatform.get().createWindow(properties);
        window.setSize(properties.width, properties.height);
        Gdx.graphics.setResizable(false);
        return window;
    }

    default Vector2 getSize() {
        return new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    default void setSize(int width, int height) {
        Gdx.graphics.setWindowedMode(width, height);
    }

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    /**
     * Initialized window.
     */
    void init();

    void dispose();

    Cursor registerCursor(int hotSpotX, int hotSpotY, Identifier identifier);

    default BubbleBlaster game() {
        return BubbleBlaster.getInstance();
    }

    void finalSetup();

    default void toggleFullscreen() {
        this.setFullscreen(!this.isFullscreen());
    }

    default void setFullscreen(boolean enable) {
        if (this.isFullscreen() && !enable) {
            if (!WindowEvents.WINDOW_FULLSCREEN.factory().onWindowFullscreen(this, false).isCanceled()) {
                Gdx.graphics.setWindowedMode(Constants.DEFAULT_SIZE.x, Constants.DEFAULT_SIZE.y);
            }
        } else if (!this.isFullscreen() && enable) {
            if (!WindowEvents.WINDOW_FULLSCREEN.factory().onWindowFullscreen(this, true).isCanceled()) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
                this.game().resize(mode.width, mode.height);
            }
        }

        BubbleBlasterConfig.FULLSCREEN.set(enable);
        BubbleBlasterConfig.save();
    }

    default boolean isFullscreen() {
        return Gdx.graphics.isFullscreen();
    }

    void setVisible(boolean visible);

    boolean isVisible();

    boolean isInitialized();

    default Rectangle getBounds() {
        return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    int getX();

    int getY();

    default Vector2 getMousePosition() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    default void setCursor(Cursor cursor) {
//        Gdx.graphics.setCursor(cursor);
    }

    default void setCursor(Cursor.SystemCursor cursor) {
//        Gdx.graphics.setSystemCursor(cursor);
//
    }

    void requestFocus();

    boolean isFocused();

    /**
     * Taskbar feature, flashes the taskbar icon on Windows.
     * Other operating systems are unknown for this behavior.
     */
    void requestUserAttention();

    void showError(@NotNull String title, @Nullable String description);

    default void setTitle(@NotNull String title) {
        Gdx.graphics.setTitle(title);
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    class Properties {
        public final int width;
        public final int height;
        public final String title;
        public boolean fullscreen;
        public Runnable onClose = () -> {};

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
