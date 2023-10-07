package com.ultreon.bubbles.render.gui.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ultreon.bubbles.Axis2D;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.CrashFiller;
import com.ultreon.bubbles.input.MobileInput;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.render.gui.widget.Circle;
import com.ultreon.bubbles.render.gui.widget.Container;
import com.ultreon.bubbles.world.World;
import com.ultreon.libs.crash.v0.CrashLog;
import com.ultreon.libs.text.v1.TextObject;
import org.checkerframework.common.value.qual.IntRange;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Screen extends Container implements CrashFiller {
    protected final BubbleBlaster game = BubbleBlaster.getInstance();
    private GuiComponent focused;
    @IntRange(from = 0)
    private int focusIndex = 0;
    private Screen backScreen;

    protected final TextObject title;
    protected int middleX;
    protected int middleY;

    public Screen() {
        this(TextObject.EMPTY);
    }

    public Screen(TextObject title) {
        super(0, 0, 0, 0);
        this.title = title;
        this.backScreen = this.game.getCurrentScreen();
    }

    public Screen(Screen backScreen) {
        this(TextObject.EMPTY, backScreen);
    }

    public Screen(TextObject title, Screen backScreen) {
        super(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());
        this.title = title;
        this.backScreen = backScreen;
    }

    protected final void setBackScreen(Screen screen) {
        this.backScreen = screen;
    }

    public final void resize(int width, int height) {
        this.onResize(width, height);
        this.init(width, height);
    }

    protected void onResize(int width, int height) {

    }

    public void init(int width, int height) {
        this.clearWidgets();

        this.width = width;
        this.height = height;
        this.middleX = width / 2;
        this.middleY = height / 2;

        this.init();
    }

    /**
     * Show Scene
     *
     * @author XyperCode
     */
    public abstract void init();

    /**
     * Hide scene, unbind events.
     *
     * @param to the next scene to go.
     * @return true to cancel change screen.
     * @author XyperCode
     */
    @CanIgnoreReturnValue
    public boolean close(Screen to) {
        for (GuiComponent child : this.children)
            child.dispose();

        return false;
    }

    public void back() {
        this.game.showScreen(this.backScreen);
    }

    public void forceClose() {

    }

    @Override
    public boolean keyPress(int keyCode) {
        if (keyCode == Input.Keys.ESCAPE) {
            if (this.backScreen != null) {
                this.game.showScreen(this.backScreen);
                return true;
            }
            this.game.showScreen(null);
            return true;
        }

        if (super.keyPress(keyCode)) return true;

        if (keyCode == Input.Keys.TAB) {
            this.focusIndex++;
            this.onChildFocusChanged();
            return true;
        }

        return this.focused != null && this.focused.keyPress(keyCode);
    }

    @Override
    public boolean keyRelease(int keyCode) {
        if (keyCode == Input.Keys.TAB) return true;

        return this.focused != null && this.focused.keyRelease(keyCode);
    }

    @Override
    public boolean charType(char character) {
        if (character == '\t') return true;

        return this.focused != null && this.focused.charType(character);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        GuiComponent inputWidget = this.getWidgetAt(x, y);
        if (inputWidget != null) {
            this.focused = inputWidget;
        }
        return super.mousePress(x, y, button);
    }

    public void onChildFocusChanged() {
        CopyOnWriteArrayList<GuiComponent> clone = new CopyOnWriteArrayList<>(this.children);
        if (clone.isEmpty()) return;
        if (this.focusIndex >= clone.size()) {
            this.focusIndex = 0;
        }

        this.focused = clone.get(this.focusIndex);
    }

    /**
     * @return the currently focused widget.
     */
    public GuiComponent getFocusedWidget() {
        return this.focused;
    }

    public void renderBackground(Renderer renderer) {
        if (this.game.world != null)
            renderer.fillGradient(0, 0, this.getWidth(), this.getHeight(), Color.BLACK.withAlpha(0xb0), Color.BLACK.withAlpha(0xc0), Axis2D.VERTICAL);
        else
            renderer.fill(0, 0, this.getWidth(), this.getHeight(), Color.grayscale(0x1e));
    }

    /**
     * Renders the screen.<br>
     * <b>Note: <i>super calls are recommended to make the gui work correctly.</i></b>
     *
     * @param game      the game's instance.
     * @param renderer  the renderer.
     * @param mouseX    the mouse's current x position
     * @param mouseY    the mouse's current y position
     * @param deltaTime partial ticks / frame time.
     */
    public void render(BubbleBlaster game, Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.render(renderer, mouseX, mouseY, deltaTime);
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.renderBackground(renderer);
        this.renderChildren(renderer, mouseX, mouseY, deltaTime);
    }

    /**
     * Get the default cursor for the screen.
     *
     * @return the default cursor.
     */
    public Cursor.SystemCursor getDefaultCursor() {
        return Cursor.SystemCursor.Arrow;
    }

    /**
     * Returns if the screen should pause the game or not.
     *
     * @return true to pause the game.
     * @see BubbleBlaster#isPaused()
     */
    public boolean doesPauseGame() {
        World world = this.game.world;
        return world != null && world.getGamemode().canBePaused();
    }

    @Override
    public void fillInCrash(CrashLog crashLog) {

    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        Circle circle = new Circle(this.width - 100, 100, 50);
        if (circle.contains(x, y)) {
            this.playMenuEvent();
            this.game.showScreen(this.backScreen);
            return true;
        }
        return super.mouseRelease(x, y, button);
    }

    public void renderCloseButton(Renderer renderer, int mouseX, int mouseY) {
        Circle circle = new Circle(this.width - 100, 100, 50);
        renderer.fillCircle(this.width - 100, 100, 100, Color.WHITE.withAlpha(circle.contains(mouseX, mouseY) ? MobileInput.isTouchDown() ? 0x80 : 0x60 : 0x40));
        renderer.setLineThickness(3);
        renderer.line(this.width - 125, 75, this.width - 75, 125, Color.WHITE);
        renderer.line(this.width - 125, 125, this.width - 75, 75, Color.WHITE);
    }
}
