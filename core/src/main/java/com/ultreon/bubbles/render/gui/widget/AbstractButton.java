package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import com.ultreon.libs.text.v1.TextObject;
import org.checkerframework.common.value.qual.IntRange;

public abstract class AbstractButton extends GuiComponent {
    private Runnable command;
    private boolean pressed;

    public AbstractButton(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    protected static void drawText(Renderer renderer, Color color, Vec2i pos, Vec2i size, TextObject text, BitmapFont font) {
        renderer.scissored(pos.x + 4, pos.y + 4, size.x - 8, size.y - 8, () -> {
            renderer.drawTextCenter(font, text.getText(), pos.x + (size.x - 8) / 2f, pos.y + (size.y - 8) / 2f, color);
        });
    }

    @Override
    public boolean mousePress(int x, int y, int button) {
        if (button == Buttons.LEFT && this.enabled && this.visible) {
            this.pressed = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseRelease(int x, int y, int button) {
        if (button == Buttons.LEFT && this.enabled && this.visible && this.pressed) {
            this.pressed = false;
            this.playMenuEvent();
            this.command.run();
            return true;
        }
        return false;
    }

    @Override
    public void mouseDrag(int x, int y, int nx, int ny, int button) {
        if (this.isHovered() && button == Buttons.LEFT && this.enabled && this.visible) {
            this.pressed = true;
            return;
        }
        super.mouseDrag(x, y, nx, ny, button);
    }

    @Override
    public void make() {
        super.make();

        if (this.isHovered()) {
            BubbleBlaster.getInstance().getGameWindow().setCursor(BubbleBlaster.getInstance().getPointerCursor());
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        if (this.isHovered()) {
            BubbleBlaster.getInstance().getGameWindow().setCursor(BubbleBlaster.getInstance().getDefaultCursor());
        }
    }

    @Override
    public void mouseExit() {
        this.pressed = false;
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
        this.getCommand().run();
    }
}
