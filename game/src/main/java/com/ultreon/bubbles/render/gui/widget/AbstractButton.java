package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.core.input.MouseInput;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.init.Sounds;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.bubbles.vector.Vec2i;
import org.checkerframework.common.value.qual.IntRange;

public abstract class AbstractButton extends GuiComponent {
    private Runnable command;
    private boolean pressed;

    public AbstractButton(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (isHovered() && button == 1 && enabled && visible) {
            this.pressed = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        if (isHovered() && button == 1 && enabled && visible && pressed) {
            this.pressed = false;
            Sounds.MENU_EVENT.play(0.2d);
            this.command.run();
            return true;
        }
        return false;
    }

    @Override
    public void mouseDrag(int x, int y, int nx, int ny, int button) {
        if (isHovered() && button == 1 && enabled && visible) {
            pressed = true;
            return;
        }
        super.mouseDrag(x, y, nx, ny, button);
    }

    @Override
    public void make() {
        super.make();

        Vec2i mousePos = MouseInput.getPos();
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
