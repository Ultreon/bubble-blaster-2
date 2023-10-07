package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiStateListener;
import com.ultreon.libs.text.v1.TextObject;


public class CrashButton extends AbstractButton implements GuiStateListener {
    private Runnable command;
    private TextObject text;

    public void setText(String text) {
        this.text = TextObject.literal(text);
    }

    public void setText(TextObject text) {
        this.text = text;
    }

    public TextObject getText() {
        return this.text;
    }

    public static class Builder {
        private Rectangle _bounds = new Rectangle(10, 10, 96, 48);
        private TextObject text = TextObject.EMPTY;
        private Runnable command = () -> {
        };

        public Builder() {
        }

        public CrashButton build() {
            var button = new CrashButton((int) this._bounds.x, (int) this._bounds.y, (int) this._bounds.width, (int) this._bounds.height);

            button.setText(this.text);
            button.setCommand(this.command);
            return button;
        }

        public Builder bounds(Rectangle bounds) {
            this._bounds = bounds;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            this._bounds = new Rectangle(x, y, width, height);
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
    }

    public CrashButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        Color textColor;

        if (this.isPressed()) {
            renderer.drawErrorEffectBox(this.getBounds(), new Insets(2));

            textColor = Color.WHITE;
        } else if (this.isHovered()) {
            renderer.setLineThickness(4.0f);
            renderer.drawErrorEffectBox(this.x + 1, this.y + 1, this.width - 2, this.height - 2, new Insets(2));

            textColor = Color.rgb(0xffffff);
        } else {
            renderer.setLineThickness(1.0f);
            renderer.box(this.getBounds(), Color.WHITE, new Insets(1));

            textColor = Color.rgb(0xffffff);
        }

        AbstractButton.drawText(renderer, textColor, this.getPos(), this.getSize(), this.text, this.font);
    }

    @Override
    public Runnable getCommand() {
        return this.command;
    }

    @Override
    public void setCommand(Runnable command) {
        this.command = command;
    }
}
