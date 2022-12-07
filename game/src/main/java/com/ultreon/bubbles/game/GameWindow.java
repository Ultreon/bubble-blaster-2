package com.ultreon.bubbles.game;

import com.ultreon.bubbles.common.Identifier;
import com.ultreon.bubbles.core.CursorManager;
import com.ultreon.bubbles.event.v2.EventResult;
import com.ultreon.bubbles.event.v2.GameEvents;
import com.ultreon.bubbles.input.KeyInput;
import com.ultreon.bubbles.input.MouseInput;
import com.ultreon.bubbles.render.screen.PauseScreen;
import com.ultreon.bubbles.vector.Vec2i;
import com.ultreon.bubbles.vector.size.IntSize;
import com.ultreon.commons.exceptions.OneTimeUseException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.IntRange;
import org.jdesktop.swingx.JXFrame;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Window class for the game's window.
 *
 * @author Qboi123
 */
@SuppressWarnings("unused")
public class GameWindow implements WindowListener, WindowFocusListener, WindowStateListener, ComponentListener {
    // GUI Elements.
    @NonNull
    private final Frame frame;
    @NonNull
    final Canvas canvas;

    // AWT Toolkit.
    @NonNull
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();

    // Graphics thingies.
    @NonNull
    final ImageObserver observer;
    @NonNull
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    @NonNull
    private final GraphicsDevice device;
    @NonNull
    final Properties properties;
    private boolean initialized = false;

    @IntRange(from = 0)
    private int fps;
    private CursorManager cursorManager;
    private Thread mainThread;

    /**
     * Window constructor.
     *
     * @param properties window properties, not fully implemented yet. LOL
     */
    @SuppressWarnings("FunctionalExpressionCanBeFolded")
    public GameWindow(@NonNull Properties properties) throws IOException {
        this.properties = properties;
        this.frame = setupFrame(properties);

        this.device = getGraphicsDevice(this.frame);

        this.canvas = createCanvas(properties);
        this.observer = this.canvas::imageUpdate; // Didn't use canvas directly because create security reasons.
        this.frame.add(this.canvas);
    }

    /**
     * Set up the game frame.
     *
     * @param properties the window properties.
     * @return the game frame.
     * @throws IOException when a resource couldn't be loaded.
     */
    @NotNull
    private JXFrame setupFrame(@NotNull Properties properties) throws IOException {
        var frame = new JXFrame(properties.title);

        setupFrameSettings(frame, properties);
        setupFrameListeners(frame);
        return frame;
    }

    /**
     * Setup graphics device.
     * @return the graphics device.
     */
    @NotNull
    private GraphicsDevice getGraphicsDevice(Frame frame) {
        return frame.getGraphicsConfiguration().getDevice();
    }

    /**
     * Create the rendering canvas.
     * @param properties the game window properties.
     * @return the created canvas.
     */
    @NotNull
    private Canvas createCanvas(@NotNull Properties properties) {
        // Setup canvas.
        var canvas = new Canvas(this.frame.getGraphicsConfiguration()) {
            @Override
            public void paint(Graphics g) {
                if (BubbleBlaster.hasRendered()) {
                    super.paint(g);
                } else {
                    g.drawImage(BubbleBlaster.getInstance().cachedImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        canvas.enableInputMethods(true);
        canvas.setFocusTraversalKeysEnabled(false);

        if (properties.fullscreen) {
            GraphicsDevice graphicsDevice = getGraphicsDevice(frame);
            Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
            canvas.setSize(bounds.getSize());
        } else {
            canvas.setSize(properties.width, properties.height);
        }
        canvas.setBackground(new Color(72, 72, 72));
        canvas.setSize(properties.width, properties.height);
        return canvas;
    }

    /**
     * Set up the listeners for the game frame.
     */
    private void setupFrameListeners(JXFrame frame) {
        // Set listeners.
        frame.addComponentListener(this);
        frame.addWindowListener(this);
        frame.addWindowFocusListener(this);
    }

    /**
     * Set up the game frame settings.
     * @param frame the window frame.
     * @param properties the game window properties.
     * @throws IOException when a resource couldn't be loaded.
     */
    private void setupFrameSettings(JXFrame frame, @NotNull Properties properties) throws IOException {
        // Setup frame settings.
        frame.setPreferredSize(new Dimension(properties.width, properties.height));
        if (properties.fullscreen) {
            GraphicsDevice graphicsDevice = getGraphicsDevice(frame);
            Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
            frame.setBounds(bounds);
            frame.setUndecorated(true);
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setSize(properties.width, properties.height);
            System.out.println("screenSize.height = " + screenSize.height);
            System.out.println("properties.height = " + properties.height);
            frame.setLocationRelativeTo(null);
        }
        frame.setResizable(false);
        frame.enableInputMethods(true);
        frame.setFocusTraversalKeysEnabled(false);
        try (InputStream inputStream = BubbleBlaster.getGameJar().openStream("icon.png")) {
            frame.setIconImage(ImageIO.read(inputStream));
        }

        // Set flag attributes.
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Thw window is resized.
     * @param e the event to be processed
     */
    @Override
    public void componentResized(ComponentEvent e) {
        GameWindow.this.canvas.setSize(e.getComponent().getSize());
        BubbleBlaster game = BubbleBlaster.getInstance();
        game.resize(new IntSize(e.getComponent().getSize()));
    }

    /**
     * The window moved.
     * @param e the event to be processed
     */
    @Override
    public void componentMoved(ComponentEvent e) {

    }

    /**
     * The window is shown
     * @param e the event to be processed
     */
    @Override
    public void componentShown(ComponentEvent e) {
        GameWindow.this.canvas.setSize(e.getComponent().getSize());
        BubbleBlaster game = BubbleBlaster.getInstance();
        game.resize(new IntSize(e.getComponent().getSize()));
    }

    /**
     * The window is hidden
     * @param e the event to be processed
     */
    @Override
    public void componentHidden(ComponentEvent e) {

    }

    /**
     * The window just opened.
     * @param e the event to be processed
     */
    @Override
    public void windowOpened(WindowEvent e) {
        GameWindow.this.canvas.setSize(e.getComponent().getSize());
        BubbleBlaster game = BubbleBlaster.getInstance();
        game.resize(new IntSize(e.getComponent().getSize()));
    }

    /**
     * Window tries to close. (Cancellable)
     * @param e the event to be processed
     */
    @Override
    public void windowClosing(WindowEvent e) {
        EventResult<Boolean> booleanEventResult = GameEvents.WINDOW_CLOSING.factory().onWindowClosing(this);
        Boolean cancel = booleanEventResult.getValue();
        if (cancel == null || !cancel) {
            game().close();
        }
    }

    /**
     * Window is closed.
     * @param e the event to be processed
     */
    @Override
    public void windowClosed(WindowEvent e) {

    }

    /**
     * Window minimized.
     * @param e the event to be processed
     */
    @Override
    public void windowIconified(WindowEvent e) {

    }

    /**
     * Window un-minimized.
     * @param e the event to be processed
     */
    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void windowActivated(WindowEvent e) {

    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    /**
     * Window has gained focus.
     * @param e the event to be processed
     */
    @Override
    public void windowGainedFocus(WindowEvent e) {
        GameWindow.this.canvas.requestFocus();
    }

    /**
     * Window list focus.
     * This pauses the game.
     * @param e the event to be processed
     */
    @Override
    public void windowLostFocus(WindowEvent e) {
        BubbleBlaster game = game();
        if (game != null && game.isInGame()) {
            game.showScreen(new PauseScreen());
        }
    }

    /**
     * State create window has changed.
     * @param e the event to be processed
     */
    @Override
    public void windowStateChanged(WindowEvent e) {

    }

    /**
     * Initialized window.
     */
    public synchronized void init() {
        if (initialized) {
            throw new OneTimeUseException("The game window is already initialized.");
        }

        KeyInput.init();
        MouseInput.init();

        KeyInput.listen(this.frame);
        MouseInput.listen(this.frame);

        KeyInput.listen(this.canvas);
        MouseInput.listen(this.canvas);

        this.initialized = true;

        BubbleBlaster.getLogger().info("Initialized game window");

        game().windowLoaded();
    }

    void close() {
        this.frame.dispose(); // Window#dispose() closes the awt-based window.
        System.exit(0);
    }

    public Cursor registerCursor(int hotSpotX, int hotSpotY, Identifier identifier) {
        Identifier textureEntry = new Identifier("textures/cursors/" + identifier.path(), identifier.location());
        Image image;
        try (InputStream assetAsStream = game().getResourceManager().openResourceStream(textureEntry)) {
            image = ImageIO.read(assetAsStream);
        } catch (IOException e) {
            throw new IOError(e);
        }

        return toolkit.createCustomCursor(image, new Point(hotSpotX, hotSpotY), identifier.toString());
    }

    private BubbleBlaster game() {
        return BubbleBlaster.getInstance();
    }

    public void finalSetup() {
        this.frame.setVisible(true);
        this.canvas.setVisible(true);
    }

    public void toggleFullscreen() {
        if (isFullscreen()) {
            device.setFullScreenWindow(null);
        } else {
            device.setFullScreenWindow(this.frame);
        }
    }

    public void setFullscreen(boolean fullscreen) {
        if (isFullscreen() && !fullscreen) { // If currently not fullscreen and disabling fullscreen.
            device.setFullScreenWindow(null); // Set fullscreen to false.
        } else if (!isFullscreen() && fullscreen) { // If currently in fullscreen and enabling fullscreen.
            device.setFullScreenWindow(this.frame); // Set fullscreen to true.
        }
    }

    public boolean isFullscreen() {
        return device.getFullScreenWindow() == this.frame;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Rectangle getBounds() {
        return this.frame.getBounds();
    }

    public int getWidth() {
        return this.frame.getWidth();
    }

    public int getHeight() {
        return this.frame.getHeight();
    }

    public int getX() {
        return this.frame.getX();
    }

    public int getY() {
        return this.frame.getY();
    }

    @Nullable
    public Vec2i getMousePosition() {
        MouseInput.getPos();
        @Nullable Point mousePosition = this.canvas.getMousePosition();

        if (mousePosition == null) return null;
        return new Vec2i(mousePosition);
    }

    public void setCursor(Cursor defaultCursor) {
        if (cursorManager == null) return;
        cursorManager.setCursor(this.frame, defaultCursor);
    }

    public void requestFocus() {
        this.frame.requestFocus();
        this.canvas.requestFocus();
    }

    public boolean isFocused() {
        return this.frame.isFocused();
    }

    /**
     * Taskbar feature, flashes the taskbar icon on Windows.
     * Other operating systems are unknown for this behavior.
     */
    public void requestUserAttention() {
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Taskbar.Feature.USER_ATTENTION_WINDOW)) {
                taskbar.requestWindowUserAttention(this.frame);
            }
        }
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public static class Properties {
        private final int width;
        private final int height;
        private final String title;
        private boolean fullscreen;

        @SuppressWarnings("ConstantConditions")
        public Properties(@NonNull String title, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
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
    }
}
