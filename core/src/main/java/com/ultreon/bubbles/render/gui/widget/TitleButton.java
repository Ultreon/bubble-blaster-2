package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.libs.text.v1.TextObject;


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
        return this.text;
    }

    public static class Builder {
        private Rectangle bounds = new Rectangle(10, 10, 96, 48);
        private TextObject text = TextObject.EMPTY;
        private Runnable command = () -> {

        };

        public Builder() {
        }

        public TitleButton build() {
            TitleButton button = new TitleButton((int) this.bounds.x, (int) this.bounds.y, (int) this.bounds.width, (int) this.bounds.height);

            button.setText(this.text);
            button.setCommand(this.command);
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

        float radius = 14f;
        renderer.fillRoundRect(this.x, this.y, this.width - 1, this.height - 1, radius, Color.GRAY_6);

        if (this.isPressed()) {
            renderer.setColor(0xff484848);
            renderer.fillRoundRect(this.x, this.y, this.width - 1, this.height - 1, radius, Color.GRAY_6);

            renderer.drawRoundEffectBox(this.x, this.y, this.width, this.height, radius, 2);
            textColor = Color.WHITE;
        } else if (this.isHovered()) {
            renderer.drawRoundEffectBox(this.x, this.y, this.width, this.height, radius, 4);
            textColor = Color.rgb(0xffffff);
        } else {
            renderer.setLineThickness(1.0f);
            textColor = Color.rgb(0xe0e0e0);
        }

        AbstractButton.drawText(renderer, textColor, this.getPos(), this.getSize(), this.text, Fonts.SANS_REGULAR_20.get());
    }

    @SuppressWarnings("EmptyMethod")
    public void tick(Gamemode gamemode) {

    }
}
