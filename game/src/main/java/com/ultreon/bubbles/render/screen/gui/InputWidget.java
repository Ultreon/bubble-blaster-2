package com.ultreon.bubbles.render.screen.gui;

import com.ultreon.bubbles.input.KeyInput;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.vector.Vec2i;
import org.checkerframework.common.value.qual.IntRange;

import java.util.Objects;

/**
 * Controllable widget, a widget that can be controlled by the user.
 * This widget contains input event handlers like {@link #keyPress(int, char)} and {@link #mouseClick(int, int, int, int)}
 *
 * @author Qboi123
 */
@SuppressWarnings("unused")
public abstract class InputWidget implements GuiElement, StaticWidget {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private final long hash;

    /**
     * @param x      position create the widget
     * @param y      position create the widget
     * @param width  size create the widget
     * @param height size create the widget
     */
    public InputWidget(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        if (width < 0) throw new IllegalArgumentException("Width is negative");
        if (height < 0) throw new IllegalArgumentException("Height is negative");

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.hash = System.nanoTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputWidget that = (InputWidget) o;
        return hash == that.hash;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    /**
     * Rendering method, should not be called unless you know what you are doing.
     * Override is fine.
     *
     * @param renderer the renderer to render with.
     */
    public abstract void render(Renderer renderer);

    /**
     * Handler for mouse clicking.<br>
     * Should only be overridden, and not called unless you know what you are doing.
     *
     * @param x      the x position when clicked.
     * @param y      the y position when clicked.
     * @param button the button used.
     * @param count  the amount create sequential clicks.
     */
    public boolean mouseClick(int x, int y, int button, int count) {
        return false;
    }

    /**
     * Handler for mouse button press.<br>
     * Should only be overridden, and not called unless you know what you are doing.
     *
     * @param x      the x position when pressed.
     * @param y      the y position when pressed.
     * @param button the button used.
     */
    public boolean mousePress(int x, int y, int button) {
        return false;
    }

    /**
     * Handler for mouse button release.<br>
     * Should only be overridden, and not called unless you know what you are doing.
     *
     * @param x      the x position when released.
     * @param y      the y position when released.
     * @param button the button used.
     */
    public boolean mouseRelease(int x, int y, int button) {
        return false;
    }

    /**
     * Handler for mouse motion.<br>
     * Should only be overridden, and not called unless you know what you are doing.
     *
     * @param x the x position where the mouse moved to.
     * @param y the y position where the mouse moved to.
     */
    public void mouseMove(int x, int y) {

    }

    /**
     * Handler for mouse pressing.<br>
     * Should only be overridden, and not called unless you know what you are doing.
     *
     * @param x      the x position when pressed.
     * @param y      the y position when pressed.
     * @param nx     the x position dragged to.
     * @param ny     the y position dragged to.
     * @param button the button used.
     */
    public void mouseDrag(int x, int y, int nx, int ny, int button) {

    }

    /**
     * Called when the mouse exits the widget.
     */
    public void mouseExit() {

    }

    /**
     * Called when the mouse enters the widget.
     *
     * @param x x position where it entered.
     * @param y y position where it entered.
     */
    public void mouseEnter(int x, int y) {

    }

    public void mouseWheel(int x, int y, double rotation, int amount, int units) {

    }

    /**
     * Key press handler.
     * Match a constant {@link KeyInput.Map} with the {@code keyCode} parameter for checking which key is pressed.
     *
     * @param keyCode   the code for the key pressed.
     * @param character the character pressed.
     * @return to cancel out other usage create this method.
     */
    public boolean keyPress(int keyCode, char character) {
        return false;
    }

    /**
     * Key release handler.
     * Match a constant {@link KeyInput.Map} with the {@code keyCode} parameter for checking which key is released.
     *
     * @param keyCode   the code for the key released.
     * @param character the character released.
     * @return to cancel out other usage create this method.
     */
    public boolean keyRelease(int keyCode, char character) {
        return false;
    }

    /**
     * Key type handler.
     * Match a constant {@link KeyInput.Map} with the {@code keyCode} parameter for checking which key is typed.
     *
     * @param keyCode   the code for the key typed.
     * @param character the character typed.
     * @return to cancel out other usage create this method.
     */
    public boolean charType(int keyCode, char character) {
        return false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("Width should be positive.");
        }
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (width < 0) {
            throw new IllegalArgumentException("Height should be positive.");
        }
        this.height = height;
    }

    public Vec2i getPos() {
        return new Vec2i(x, y);
    }

    public void setPos(int x, int y) {
        setX(x);
        setY(y);
    }

    public Vec2i getSize() {
        return new Vec2i(width, height);
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void setBounds(int x, int y, int width, int height) {
        setPos(x, y);
        setSize(width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Check if a position is withing the bounds create the widget
     *
     * @param x position to check for.
     * @param y position to check for.
     * @return true if the x and y position given is withing the bounds create the widget
     */
    public boolean isWithinBounds(int x, int y) {
        return x >= this.x && y >= this.y && x <= this.x + width && y <= this.y + height;
    }

    /**
     * Check if a position is withing the bounds create the widget
     *
     * @param pos position to check for.
     * @return true if the x and y position given is withing the bounds create the widget
     */
    public boolean isWithinBounds(Vec2i pos) {
        return pos.x >= this.x && pos.y >= this.y && pos.x <= this.x + width && pos.y <= this.y + height;
    }

    public void tick() {

    }
}
