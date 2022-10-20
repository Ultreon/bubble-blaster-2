package com.ultreon.bubbles.render.screen;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.input.KeyInput;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.gui.Container;
import com.ultreon.bubbles.render.screen.gui.GuiElement;
import com.ultreon.bubbles.render.screen.gui.InputWidget;
import org.checkerframework.common.value.qual.IntRange;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unused")
public abstract class Screen extends Container {
    private InputWidget focused;
    @IntRange(from = 0)
    private int focusIndex = 0;

    public Screen() {
        super(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());
    }

    private boolean valid;
    protected final BubbleBlaster game = BubbleBlaster.getInstance();

    public final void resize(int width, int height) {
        this.onResize(width, height);
        this.width = width;
        this.height = height;
    }

    protected void onResize(int width, int height) {

    }

    public void init(int width, int height) {
        this.init();
    }

    /**
     * Show Scene
     *
     * @author Qboi
     */
    public abstract void init();

    /**
     * Hide scene, unbind events.
     *
     * @param to the next scene to go.
     * @return true to cancel change screen.
     * @author Qboi
     */
    public boolean onClose(Screen to) {
        return true;
    }

    public void forceClose() {

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

    public void mouseExit() {
        if (this.hoveredInteractable != null) {
            this.hoveredInteractable.mouseExit();
            this.hoveredInteractable = null;
        }
    }

    @Override
    public boolean keyPress(int keyCode, char character) {
        if (keyCode == KeyInput.Map.KEY_ESCAPE) {
            game.showScreen(null);
            return true;
        }

        if (super.keyPress(keyCode, character)) return true;

        if (keyCode == KeyInput.Map.KEY_TAB) {
            this.focusIndex++;
            onChildFocusChanged();
            return true;
        }

        return this.focused != null && this.focused.keyPress(keyCode, character);
    }

    @Override
    public boolean keyRelease(int keyCode, char character) {
        if (keyCode == KeyInput.Map.KEY_TAB) return true;

        return this.focused != null && this.focused.keyRelease(keyCode, character);
    }

    @Override
    public boolean charType(int keyCode, char character) {
        if (keyCode == KeyInput.Map.KEY_TAB) return true;

        return this.focused != null && this.focused.charType(keyCode, character);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        InputWidget inputWidget = getWidgetAt(x, y);
        if (inputWidget != null) {
            focused = inputWidget;
            return true;
        }
        return super.mousePress(x, y, button);
    }

    public void onChildFocusChanged() {
        CopyOnWriteArrayList<InputWidget> clone = new CopyOnWriteArrayList<>(children);
        if (this.focusIndex >= clone.size()) {
            this.focusIndex = 0;
        }

        this.focused = clone.get(this.focusIndex);
    }

    /**
     * @return the currently focused widget.
     */
    public InputWidget getFocusedWidget() {
        return focused;
    }

    /**
     * Renders the screen.<br>
     * <b>Note: <i>super calls are recommended to make the gui work correctly.</i></b>
     *
     * @param game         the game's instance.
     * @param renderer     the renderer.
     * @param partialTicks partial ticks / frame time.
     */
    public void render(BubbleBlaster game, Renderer renderer, float partialTicks) {
        for (InputWidget inputWidget : this.children) {
            inputWidget.render(renderer);
        }
    }

    /**
     * Adds a {@link InputWidget controllable widget} to the screen, including initializing it with {@link GuiElement#make()}.
     *
     * @param widget the widget to add.
     * @param <T>    the widget's type.
     * @return the same widget as the parameter.
     */
    @Override
    public final <T extends InputWidget> T add(T widget) {
        this.children.add(widget);
        widget.make();
        return widget;
    }

    /**
     * Get the default cursor for the screen.
     *
     * @return the default cursor.
     */
    public Cursor getDefaultCursor() {
        return BubbleBlaster.getInstance().getDefaultCursor();
    }

    /**
     * Returns if the screen should pause the game or not.
     *
     * @return true to pause the game.
     * @see BubbleBlaster#isPaused()
     */
    public boolean doesPauseGame() {
        return true;
    }
}
