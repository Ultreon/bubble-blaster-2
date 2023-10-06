package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.gui.GuiStateListener;
import com.ultreon.libs.text.v1.TextObject;
import org.checkerframework.common.returnsreceiver.qual.This;

public class ToggleButton extends Button implements GuiStateListener {
    protected TextObject text;
    private boolean toggled;

    public static Builder builder() {
        return new Builder();
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void setToggled(boolean toggled) {
        if (toggled == this.toggled)
            return;

        this.toggled = toggled;
        super.click();
    }

    @Override
    protected void click() {
        this.toggled = !this.toggled;
        super.click();
    }

    public static class Builder extends Button.Builder {
        public Builder bounds(Rectangle bounds) {
            this.bounds = bounds;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            this.bounds = new Rectangle(x, y, width, height);
            return this;
        }

        public Builder text(String text) {
            this.text = TextObject.literal(text);
            return this;
        }

        public Builder text(TextObject text) {
            this.text = text;
            return this;
        }

        public Builder command(Runnable command) {
            this.command = command;
            return this;
        }

        public @This Builder font(BitmapFont font) {
            this.font = font;
            return this;
        }

        public ToggleButton build() {
            ToggleButton button = new ToggleButton((int) this.bounds.x, (int) this.bounds.y, (int) this.bounds.width, (int) this.bounds.height);

            button.setText(this.text);
            button.setCommand(this.command);
            button.setFont(this.font);
            return button;
        }
    }

    protected ToggleButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public boolean isPressed() {
        return this.isToggled();
    }
}
