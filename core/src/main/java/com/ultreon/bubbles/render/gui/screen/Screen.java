package com.ultreon.bubbles.render.gui.screen;

import com.ultreon.bubbles.core.input.KeyboardInput;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import org.checkerframework.common.value.qual.IntRange;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unused")
public abstract class Screen extends com.ultreon.bubbles.render.gui.widget.Container {
    protected final BubbleBlaster game = BubbleBlaster.getInstance();
    private GuiComponent focused;
    @IntRange(from = 0)
    private int focusIndex = 0;
    private Screen backScreen;


    public Screen() {
        super(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());
    }

    public Screen(Screen backScreen) {
        super(0, 0, BubbleBlaster.getInstance().getWidth(), BubbleBlaster.getInstance().getHeight());
        this.backScreen = backScreen;
    }

    protected final void setBackScreen(Screen screen) {
        this.backScreen = screen;
    }

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
        for (GuiComponent child : children) {
            child.destroy();
        }
        return true;
    }

    public void forceClose() {

    }

    public void mouseExit() {
        if (this.hoveredInteractable != null) {
            this.hoveredInteractable.mouseExit();
            this.hoveredInteractable = null;
        }
    }

    @Override
    public boolean keyPress(int keyCode) {
        if (keyCode == KeyboardInput.Map.KEY_ESCAPE) {
            if (backScreen != null) {
                game.showScreen(backScreen);
                return true;
            }
            game.showScreen(null);
            return true;
        }

        if (super.keyPress(keyCode)) return true;

        if (keyCode == KeyboardInput.Map.KEY_TAB) {
            this.focusIndex++;
            onChildFocusChanged();
            return true;
        }

        return this.focused != null && this.focused.keyPress(keyCode);
    }

    @Override
    public boolean keyRelease(int keyCode) {
        if (keyCode == KeyboardInput.Map.KEY_TAB) return true;

        return this.focused != null && this.focused.keyRelease(keyCode);
    }

    @Override
    public boolean charType(char character) {
        if (character == '\t') return true;

        return this.focused != null && this.focused.charType(character);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        GuiComponent inputWidget = getWidgetAt(x, y);
        if (inputWidget != null) {
            focused = inputWidget;
        }
        return super.mousePress(x, y, button);
    }

    public void onChildFocusChanged() {
        CopyOnWriteArrayList<GuiComponent> clone = new CopyOnWriteArrayList<>(children);
        if (this.focusIndex >= clone.size()) {
            this.focusIndex = 0;
        }

        this.focused = clone.get(this.focusIndex);
    }

    /**
     * @return the currently focused widget.
     */
    public GuiComponent getFocusedWidget() {
        return focused;
    }

    public void renderBackground(Renderer renderer) {
        if (game.environment != null) {
            renderer.setColor(0x80000000);
        } else {
            renderer.setColor(0xff1e1e1e);
        }
        renderer.fill(getBounds());
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
        render(renderer);
    }

    @Override
    public void render(Renderer renderer) {
        renderBackground(renderer);
        renderChildren(renderer);
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
