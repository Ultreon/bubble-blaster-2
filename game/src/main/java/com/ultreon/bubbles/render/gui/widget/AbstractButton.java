package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.core.input.MouseInput;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.vector.Vec2i;
import org.checkerframework.common.value.qual.IntRange;

public abstract class AbstractButton extends GuiComponent {
    private Runnable command;
    //    private boolean hovered;
    private boolean pressed;

    public AbstractButton(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (isWithinBounds(x, y) && button == 1) {
            this.pressed = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        this.pressed = false;
        if (isWithinBounds(x, y) && button == 1) {
            this.command.run();
            return true;
        }
        return false;
    }

    @Override
    public void make() {
        super.make();

        Vec2i mousePos = MouseInput.getPos();
        if (isWithinBounds(mousePos)) {
            BubbleBlaster.getInstance().getGameWindow().setCursor(BubbleBlaster.getInstance().getPointerCursor());
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        if (this.isHovered()) {
            BubbleBlaster.getInstance().getGameWindow().setCursor(BubbleBlaster.getInstance().getDefaultCursor());
        }
    }

    @Override
    public void mouseEnter(int x, int y) {

    }

    @Override
    public void mouseExit() {

    }

    public boolean isPressed() {
        return this.pressed;
    }

    public boolean isHovered() {
        return isWithinBounds(MouseInput.getPos());
    }

    /**
     * @return the command to run when the button is pressed.
     */
    public Runnable getCommand() {
        return this.command;
    }

    /**
     * Change the command to run when the button is pressed.
     *
     * @param command the command to change to.
     */
    public void setCommand(Runnable command) {
        this.command = command;
    }

    protected final void click() {
        getCommand().run();
    }
}
