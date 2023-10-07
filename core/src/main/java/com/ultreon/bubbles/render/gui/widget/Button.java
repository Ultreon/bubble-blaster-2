package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.init.Fonts;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiStateListener;
import com.ultreon.libs.text.v1.TextObject;
import org.checkerframework.common.returnsreceiver.qual.This;

public class Button extends AbstractButton implements GuiStateListener {
    protected TextObject text;

    public static Builder builder() {
        return new Builder();
    }

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
        protected Rectangle bounds = new Rectangle(10, 10, 96, 48);
        protected TextObject text = TextObject.EMPTY;
        protected Runnable command = () -> {
        };
        protected BitmapFont font = Fonts.DEFAULT.get();

        protected Builder() {
        }

        public Button build() {
            var button = new Button((int) this.bounds.x, (int) this.bounds.y, (int) this.bounds.width, (int) this.bounds.height);

            button.setText(this.text);
            button.setCommand(this.command);
            button.setFont(this.font);
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

        public @This Builder font(BitmapFont font) {
            this.font = font;
            return this;
        }
    }

    protected Button(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        if (this.isPressed()) this.backgroundColor = Color.WHITE.withAlpha(0x40);
        else if (this.isHovered()) this.backgroundColor = Color.WHITE.withAlpha(0x30);
        else this.backgroundColor = Color.WHITE.withAlpha(0x20);

        if (this.enabled)
            renderer.fill(this.getBounds(), this.backgroundColor);

        if (this.isHovered() && this.enabled) {
            renderer.hovered();
            renderer.drawEffectBox(this.x, this.y, this.width, this.height, new Insets(0, 0, 4, 0));
        }

        this.drawText(renderer);
    }

    protected void drawText(Renderer renderer) {
        AbstractButton.drawText(renderer, Color.WHITE, this.getPos(), this.getSize(), this.getText(), this.getFont());
    }
}
