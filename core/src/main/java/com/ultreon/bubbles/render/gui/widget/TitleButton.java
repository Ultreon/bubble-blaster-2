package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.libs.text.v0.TextObject;


@SuppressWarnings("unused")
public class TitleButton extends AbstractButton {
    private TextObject text;

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
        private Rectangle bounds = new Rectangle(10, 10, 96, 48);
        private TextObject text = TextObject.EMPTY;
        private Runnable command = () -> {

        };

        public Builder() {
        }

        public TitleButton build() {
            TitleButton button = new TitleButton((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);

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

    protected TitleButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        Color textColor;

        renderer.setColor(0xff606060);
        renderer.roundRect(x, y, width-1, height-1, Math.min(width, height)-4, Math.min(width, height)-2);

        if (isPressed()) {
            renderer.setColor(0xff484848);
            renderer.roundRect(x, y, width-1, height-1, Math.min(width, height)-4, 10);

            renderer.drawRoundEffectBox(x, y, width-1, height-1, Math.min(width, height)-4, 1);
            textColor = Color.WHITE;
        } else if (isHovered()) {
            renderer.drawRoundEffectBox(x + 1, y + 1, width-4, height-4, Math.min(width, height)-8, 2);
            textColor = Color.rgb(0xffffff);
        } else {
            renderer.setLineWidth(1.0f);
            textColor = Color.rgb(0xe0e0e0);
        }

        OptionsNumberInput.ArrowButton.drawText(renderer, textColor, getPos(), getSize(), text, Fonts.SANS_REGULAR_20.get());
    }

    @SuppressWarnings("EmptyMethod")
    public void tick(Gamemode gamemode) {

    }
}
