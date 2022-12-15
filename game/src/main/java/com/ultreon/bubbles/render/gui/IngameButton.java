package com.ultreon.bubbles.render.gui;

import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.common.text.LiteralText;
import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.core.input.MouseInput;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.gui.AbstractButton;
import com.ultreon.bubbles.render.screen.gui.border.Border;

import java.awt.*;

public class IngameButton extends AbstractButton {
    private TextObject text = TextObject.EMPTY;

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
        private Rectangle _bounds = new Rectangle(10, 10, 96, 48);
        private TextObject text = TextObject.EMPTY;
        private Runnable command = () -> {
        };

        public Builder() {
        }

        public IngameButton build() {
            IngameButton button = new IngameButton(_bounds.x, _bounds.y, _bounds.width, _bounds.height);

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

    public IngameButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(Renderer renderer) {
        Color textColor;

        if (isPressed() && isWithinBounds(MouseInput.getPos())) {
            Paint old = renderer.getPaint();
            GradientPaint p = new GradientPaint(0, y, new Color(0, 192, 255), 0f, y + height, new Color(0, 255, 192));
            renderer.paint(p);
            Border border = new Border(1, 1, 1, 1);
            border.setPaint(new Color(255, 255, 255, 128));
            border.paintBorder(renderer, x + 1, y + 1, width - 2, height - 2);
            renderer.paint(old);

            textColor = Color.white;
        } else if (isHovered()) {
            Paint old = renderer.getPaint();

            double shiftX = ((double) width * 2) * BubbleBlaster.getTicks() / (BubbleBlaster.TPS * 10);
            GradientPaint p = new GradientPaint(x + ((float) shiftX - width), 0, new Color(0, 192, 255), x + (float) shiftX, 0f, new Color(0, 255, 192), true);
            renderer.paint(p);

            Border border1 = new Border(2, 2, 2, 2);
            border1.setPaint(p);
            border1.paintBorder(renderer, x, y, width, height);
            renderer.paint(old);

            textColor = new Color(255, 255, 255);
        } else {
            Paint old = renderer.getPaint();

            Border border1 = new Border(1, 1, 1, 1);
            border1.setPaint(new Color(0x7fffffff, true));
            border1.paintBorder(renderer, x, y, width, height);
            renderer.paint(old);

            textColor = new Color(255, 255, 255, 128);
        }

        OptionsNumberInput.ArrowButton.paint0a(renderer, textColor, getBounds(), text);
    }
}
