package com.ultreon.bubbles.game;

import com.badlogic.gdx.Gdx;
import com.ultreon.bubbles.core.CursorManager;
import com.ultreon.bubbles.event.v1.WindowEvents;
import com.ultreon.bubbles.input.GameInput;
import com.ultreon.bubbles.vector.Vec2i;
import com.ultreon.commons.exceptions.OneTimeUseException;
import com.ultreon.libs.commons.v0.Identifier;
import com.ultreon.libs.commons.v0.size.IntSize;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Window class for the game's window.
 *
 * @author Qboi123
 */
@SuppressWarnings("unused")
public class GameWindow {
    private static final Marker MARKER = MarkerFactory.getMarker("GameWindow");

    // AWT Toolkit.
    @NotNull
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();

    // Graphics thingies.
    @NotNull
    private final GraphicsEnvironment gfxEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private boolean initialized = false;

    @IntRange(from = 0)
    private int fps;
    private CursorManager cursorManager;
    private Thread mainThread;
    private GameInput input;

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

        input = new GameInput();
        Gdx.input.setInputProcessor(input);

        this.initialized = true;

        BubbleBlaster.getLogger().info(MARKER, "Initialized game window");

        game().windowLoaded();
    }

    void dispose() {
        Gdx.app.exit();
    }

    public Cursor registerCursor(int hotSpotX, int hotSpotY, Identifier identifier) {
        return null;
    }

    private BubbleBlaster game() {
        return BubbleBlaster.getInstance();
    }

    public void finalSetup() {
        // TODO: Use final setup in game window.
    }

    public void toggleFullscreen() {
        setFullscreen(!isFullscreen());
    }

    public void setFullscreen(boolean fullscreen) {
        if (isFullscreen() && !fullscreen) { // If currently not fullscreen and disabling fullscreen.
            if (!WindowEvents.WINDOW_FULLSCREEN.factory().onWindowFullscreen(this, false).isCanceled()) {
                Gdx.graphics.setFullscreenMode(null);
            }
        } else if (!isFullscreen() && fullscreen) { // If currently in fullscreen and enabling fullscreen.
            if (!WindowEvents.WINDOW_FULLSCREEN.factory().onWindowFullscreen(this, true).isCanceled()) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }
    }

    public boolean isFullscreen() {
        return Gdx.graphics.isFullscreen();
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

    public void setCursor(Cursor defaultCursor) {
        BubbleBlaster.getLogger().warn("Setting cursor is not yet supported.", new RuntimeException());
//        if (cursorManager == null) return;
//        cursorManager.setCursor(this.frame, defaultCursor);
    }

    public void requestFocus() {

    }

    public boolean isFocused() {
        return game().isFocused();
    }

    /**
     * Taskbar feature, flashes the taskbar icon on Windows.
     * Other operating systems are unknown for this behavior.
     */
    public void requestUserAttention() {
        // TODO: Make user attention requests work with LibGDX.
//        if (Taskbar.isTaskbarSupported()) {
//            Taskbar taskbar = Taskbar.getTaskbar();
//            if (taskbar.isSupported(Taskbar.Feature.USER_ATTENTION_WINDOW)) {
//                taskbar.requestWindowUserAttention(this.frame);
//            }
//        }
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
