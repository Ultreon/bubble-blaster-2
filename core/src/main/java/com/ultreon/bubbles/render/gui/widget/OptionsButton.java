package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.render.Anchor;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.font.Font;
import com.ultreon.bubbles.render.font.Thickness;
import com.ultreon.bubbles.render.gui.GuiStateListener;
import com.ultreon.bubbles.vector.Vec2i;
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
        return text;
    }

    public static class Builder {
        private Rectangle bounds = new Rectangle(10, 10, 96, 48);
        private TextObject text = TextObject.EMPTY;
        private Runnable command = () -> {
        };

        public Builder() {
        }

        public OptionsButton build() {
            OptionsButton button = new OptionsButton(bounds.x, bounds.y, bounds.width, bounds.height);

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

    protected OptionsButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void render(Renderer renderer) {
        Color textColor;
        fill(renderer, 0, 0, width, height, 0x20ffffff);
        if (isPressed()) {
            renderer.drawEffectBox(2, 2, width - 4, height - 4, new Insets(1, 1, 1, 1));

            textColor = Color.white;
        } else if (isHovered()) {
            renderer.drawEffectBox(2, 2, width - 4, height - 4, new Insets(2, 2, 2, 2));

            textColor = Color.rgb(0xffffff);
        } else {
            textColor = Color.lightGray;
        }

        drawText(renderer, textColor, getSize(), text, font);
    }

    static void drawText(Renderer renderer, Color textColor, Vec2i size, TextObject text, Font font) {
        renderer.subInstance(4, 4, size.x - 8, size.y - 8, subRender -> {
            subRender.setColor(textColor);
            font.draw(subRender, text, 16, (size.x - 8) / 2f, (size.y - 8) / 2f, Thickness.BOLD, Anchor.CENTER);
            subRender.dispose();
        });
    }
}
