package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.libs.text.v0.TextObject;

public class InGameButton extends AbstractButton {
    private TextObject text = TextObject.EMPTY;

    public void setText(String text) {
        this.text = TextObject.literal(text);
    }

    public void setText(TextObject text) {
        this.text = text;
    }

    public TextObject getText() {
        return text;
    }

    public static class Builder {
        private Rectangle _bounds = new Rectangle(10, 10, 96, 48);
        private TextObject text = TextObject.EMPTY;
        private Runnable command = () -> {
        };

        public Builder() {
        }

        public InGameButton build() {
            InGameButton button = new InGameButton((int) _bounds.x, (int) _bounds.y, (int) _bounds.width, (int) _bounds.height);

            button.setText(text);
            button.setCommand(command);
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

    public InGameButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        Color textColor;

        if (this.isPressed()) {
            renderer.drawEffectBox(x + 1, y + 1, width - 2, height - 2, new Insets(1));
            textColor = Color.WHITE;
        } else if (this.isHovered()) {
            renderer.fill(x, y, width, height);
            renderer.drawEffectBox(x, y, width, height, new Insets(2));
            textColor = Color.rgb(0xffffff);
        } else {
            renderer.setLineWidth(1);
            renderer.drawEffectBox(x, y, width, height, new Insets(1));
            textColor = Color.rgb(0x80ffffff);
        }

        OptionsNumberInput.ArrowButton.drawText(renderer, textColor, getPos(), getSize(), text, font);
    }
}
