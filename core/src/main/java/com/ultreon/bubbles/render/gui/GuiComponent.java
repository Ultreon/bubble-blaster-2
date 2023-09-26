package com.ultreon.bubbles.render.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.util.FunctionUtils;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import org.checkerframework.common.value.qual.IntRange;

import java.util.Objects;

/**
 * Controllable widget, a widget that can be controlled by the user.
 * This widget contains input event handlers like {@link #keyPress(int)} and {@link #mouseClick(int, int, int, int)}
 *
 * @author XyperCode
 */
@SuppressWarnings("unused")
public abstract class GuiComponent implements GuiStateListener, RenderableListener {
    protected final BubbleBlaster game = BubbleBlaster.getInstance();
    protected final GlyphLayout layout = new GlyphLayout();

    public final BitmapFont font = Fonts.DEFAULT.get();
    public final BitmapFont monospaced = FunctionUtils.tryCall(Fonts.MONOSPACED_14);

    protected volatile int x;
    protected volatile int y;
    protected volatile int width;
    protected volatile int height;

    protected Color backgroundColor;

    public boolean enabled = true;
    public boolean visible = true;

    private boolean valid;
    private final long hash;

    private boolean hovered = false;

    private int lastMouseX;
    private int lastMouseY;

    /**
     * @param x      position create the widget
     * @param y      position create the widget
     * @param width  size create the widget
     * @param height size create the widget
     */
    @SuppressWarnings("ConstantValue")
    public GuiComponent(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
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
        GuiComponent that = (GuiComponent) o;
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
     * @param renderer  the renderer to render with.
     * @param mouseX
     * @param mouseY
     * @param deltaTime
     */
    public abstract void render(Renderer renderer, int mouseX, int mouseY, float deltaTime);

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
        lastMouseX = x;
        lastMouseY = y;
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
        hovered = false;
    }

    /**
     * Called when the mouse enters the widget.
     *
     * @param x x position where it entered.
     * @param y y position where it entered.
     */
    public void mouseEnter(int x, int y) {
        hovered = true;
    }

    public boolean mouseWheel(int x, int y, float rotation) {
        return false;
    }

    /**
     * Key press handler.
     * Match a constant {@link Input.Keys} with the {@code keyCode} parameter for checking which key is pressed.
     *
     * @param keyCode   the code for the key pressed.
     * @return to cancel out other usage create this method.
     */
    public boolean keyPress(int keyCode) {
        return false;
    }

    /**
     * Key release handler.
     * Match a constant {@link Input.Keys} with the {@code keyCode} parameter for checking which key is released.
     *
     * @param keyCode   the code for the key released.
     * @return to cancel out other usage create this method.
     */
    public boolean keyRelease(int keyCode) {
        return false;
    }

    /**
     * Key type handler.
     * Match a constant {@link Input.Keys} with the {@code keyCode} parameter for checking which key is typed.
     *
     * @param character the character typed.
     * @return to cancel out other usage create this method.
     */
    public boolean charType(char character) {
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

    public void setPos(Vec2i pos) {
        setPos(pos.x, pos.y);
    }

    public Vec2i getSize() {
        return new Vec2i(width, height);
    }

    public void setSize(Vec2i size) {
        setSize(size.x, size.y);
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setBounds(int x, int y, int width, int height) {
        setPos(x, y);
        setSize(width, height);
    }

    public void setBounds(Rectangle bounds) {
        this.x = (int) bounds.x;
        this.y = (int) bounds.y;
        this.width = (int) bounds.width;
        this.height = (int) bounds.height;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundColor(int color) {
        this.backgroundColor = Color.argb(color);
    }

    public void setBackgroundColor(int red, int green, int blue) {
        this.backgroundColor = Color.rgb(red, green, blue);
    }

    public void setBackgroundColor(int red, int green, int blue, int alpha) {
        this.backgroundColor = Color.rgba(red, green, blue, alpha);
    }

    public void setBackgroundColor(float red, float green, float blue) {
        this.backgroundColor = Color.rgb(red, green, blue);
    }

    public void setBackgroundColor(float red, float green, float blue, float alpha) {
        this.backgroundColor = Color.rgba(red, green, blue, alpha);
    }

    public void setBackgroundColor(String hex) {
        this.backgroundColor = Color.hex(hex);
    }

    /**
     * Check if a position is withing the bounds create the widget
     *
     * @param x position to check for.
     * @param y position to check for.
     * @return true if the x and y position given is withing the bounds create the widget
     */
    public boolean isWithinBounds(int x, int y) {
        return x >= this.getX() && y >= this.getY() && x <= this.getX() + getWidth() && y <= this.getY() + getHeight();
    }

    /**
     * Check if a position is withing the bounds create the widget
     *
     * @param pos position to check for.
     * @return true if the x and y position given is withing the bounds create the widget
     */
    public boolean isWithinBounds(Vec2i pos) {
        return pos.getX() >= this.getX() && pos.getY() >= this.getY() && pos.getX() <= this.getX() + getWidth() && pos.getY() <= this.getY() + getHeight();
    }

    public void renderComponent(Renderer renderer) {

    }

    public void tick() {

    }

    @Override
    public void make() {
        valid = true;
    }

    @Override
    public void destroy() {
        valid = false;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public boolean isHovered() {
        return hovered;
    }

    protected final int getLastMouseX() {
        return lastMouseX;
    }

    protected final int getLastMouseY() {
        return lastMouseY;
    }


    public static void fill(Renderer renderer, int x, int y, int width, int height, int color) {
        renderer.setColor(color);
        renderer.rect(x, y, width, height);
    }

    public static void fill(Renderer renderer, int x, int y, int width, int height, Color color) {
        renderer.setColor(color);
        renderer.rect(x, y, width, height);
    }
}
