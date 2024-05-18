package dev.ultreon.bubbles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import dev.ultreon.bubbles.util.exceptions.OneTimeUseException;
import dev.ultreon.libs.commons.v0.Identifier;

public class DefaultGameWindow implements GameWindow {
    private final Properties properties;
    private boolean initialized = false;

    public DefaultGameWindow(Properties properties) {
        this.properties = properties;
        this.setFullscreen(properties.fullscreen);
        this.setTitle(properties.title);
    }

    @Override
    public int getWidth() {
        return Gdx.graphics.getWidth();
    }

    @Override
    public void setWidth(int width) {
        Gdx.graphics.setWindowedMode(width, this.getHeight());
    }

    @Override
    public int getHeight() {
        return Gdx.graphics.getHeight();
    }

    @Override
    public void setHeight(int height) {
        Gdx.graphics.setWindowedMode(this.getWidth(), height);
    }

    @Override
    public synchronized void init() {
        if (this.initialized) {
            throw new OneTimeUseException("The game window is already initialized.");
        }
        this.initialized = true;

        BubbleBlaster.getLogger().info("Initialized game window");

        this.game().windowLoaded();
    }

    @Override
    public void dispose() {
        // Platform might not support dispose.
    }

    @Override
    public Cursor registerCursor(int hotSpotX, int hotSpotY, Identifier identifier) {
        return Gdx.graphics.newCursor(new Pixmap(BubbleBlaster.resource(identifier.mapPath(s -> "textures/cursor/" + s + ".png"))), hotSpotX, hotSpotY);
    }

    @Override
    public void finalSetup() {

    }

    @Override
    public void setVisible(boolean visible) {

    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public void requestFocus() {

    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public void requestUserAttention() {

    }

    public Properties getProperties() {
        return this.properties;
    }
}
