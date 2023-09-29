package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiStateListener;
import com.ultreon.libs.commons.v0.vector.Vec2i;
import com.ultreon.libs.text.v0.TextObject;

@SuppressWarnings("unused")
public class OptionsButton extends AbstractButton implements GuiStateListener {
    protected TextObject text;

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

        public OptionsButton build() {
            OptionsButton button = new OptionsButton((int) this.bounds.x, (int) this.bounds.y, (int) this.bounds.width, (int) this.bounds.height);

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

    protected OptionsButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        Color textColor;
        renderer.fill(this.x, this.y, this.width, this.height, Color.WHITE.withAlpha(0x20));
        if (this.isPressed()) {
            renderer.drawEffectBox(this.x + 2, this.y + 2, this.width - 4, this.height - 4, new Insets(1, 1, 1, 1));

            textColor = Color.WHITE;
        } else if (this.isHovered()) {
            renderer.drawEffectBox(this.x + 2, this.y + 2, this.width - 4, this.height - 4, new Insets(2, 2, 2, 2));

            textColor = Color.rgb(0xffffff);
        } else {
            textColor = Color.LIGHT_GRAY;
        }

        OptionsButton.drawText(renderer, textColor, this.getPos(), this.getSize(), this.text, this.font);
    }

    static void drawText(Renderer renderer, Color color, Vec2i pos, Vec2i size, TextObject text, BitmapFont font) {
        renderer.scissored(pos.x + 4, pos.y + 4, size.x - 8, size.y - 8, () -> {
            renderer.setColor(color);
            renderer.drawCenteredText(font, text.getText(), pos.x + (size.x - 8) / 2f, pos.y + (size.y - 8) / 2f);
        });
    }
}
