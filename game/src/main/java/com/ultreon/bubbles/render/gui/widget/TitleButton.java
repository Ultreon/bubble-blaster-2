package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.common.text.LiteralText;
import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;

import java.awt.*;

@SuppressWarnings("unused")
public class TitleButton extends AbstractButton {
    private TextObject text;

    public void setText(String text) {
        this.text = new LiteralText(text);
    }

    public void setText(TextObject text) {
        this.text = text;
    }

    public TextObject getText() {
        return text;
    }

    public static class Builder {
        private Rectangle bounds = new Rectangle(10, 10, 96, 48);
        private TextObject text = TextObject.EMPTY;
        private Runnable command = () -> {

        };

        public Builder() {
        }

        public TitleButton build() {
            TitleButton button = new TitleButton(bounds.x, bounds.y, bounds.width, bounds.height);

            button.setText(text);
            button.setCommand(command);
            return button;
        }

        public Builder bounds(Rectangle bounds) {
            this.bounds = bounds;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            this.bounds = new Rectangle(x, y, width, height);
            return this;
        }

        public Builder text(String text) {
            this.text = new LiteralText(text);
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

    protected TitleButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void render(Renderer renderer) {
        Color textColor;

        fill(renderer, 0, 0, width, height, 0xff606060);

        if (isPressed()) {
            // Shadow
            Paint old = renderer.getPaint();

            fill(renderer, 0, 0, width, height, 0xff484848);

            renderer.drawEffectBox(0, 0, width, height, new Insets(1, 1, 1, 1));
            renderer.paint(old);
            textColor = Color.white;
        } else if (isHovered()) {
            Paint old = renderer.getPaint();

            renderer.drawEffectBox(0, 0, width, height, new Insets(2, 2, 2, 2));
            renderer.paint(old);
            textColor = Color.rgb(0xffffff);
        } else {
            renderer.stroke(new BasicStroke(1.0f));
            textColor = Color.rgb(0xe0e0e0);
        }

        OptionsNumberInput.ArrowButton.drawText(renderer, textColor, getSize(), text, font);
    }

    @SuppressWarnings("EmptyMethod")
    public void tick(Gamemode gamemode) {

    }
}
