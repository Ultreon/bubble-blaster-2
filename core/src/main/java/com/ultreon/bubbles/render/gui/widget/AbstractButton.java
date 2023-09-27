package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.debug.Debug;
import com.ultreon.bubbles.init.SoundEvents;
import com.ultreon.bubbles.render.gui.GuiComponent;
import org.checkerframework.common.value.qual.IntRange;

public abstract class AbstractButton extends GuiComponent {
    private Runnable command;
    private boolean pressed;

    public AbstractButton(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (isHovered() && button == Buttons.LEFT && enabled && visible) {
            this.pressed = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        if (isHovered() && button == Buttons.LEFT && enabled && visible && pressed) {
            this.pressed = false;
            this.playMenuEvent();
            Debug.notify("Mouse Press", "x = " + x + ", y = " + y + ", button = " + button);
            this.command.run();
            return true;
        }
        return false;
    }

    @Override
    public void mouseDrag(int x, int y, int nx, int ny, int button) {
        if (isHovered() && button == Buttons.LEFT && enabled && visible) {
            pressed = true;
            return;
        }
        super.mouseDrag(x, y, nx, ny, button);
    }

    @Override
    public void make() {
        super.make();

        if (isHovered()) {
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
    public void mouseExit() {
        pressed = false;
        super.mouseExit();
    }

    public boolean isPressed() {
        return this.pressed;
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
