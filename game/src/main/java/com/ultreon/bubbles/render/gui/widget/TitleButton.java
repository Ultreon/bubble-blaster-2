package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.common.text.LiteralText;
import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.core.input.MouseInput;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.gamemode.Gamemode;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.border.Border;

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

        renderer.color(Color.rgb(0x606060));
        renderer.fill(getBounds());

        if (isPressed()) {
            // Shadow
            Paint old = renderer.getPaint();

            double shiftX = ((double) width * 2) * BubbleBlaster.getTicks() / (BubbleBlaster.TPS * 10);
            GradientPaint p = new GradientPaint(x + ((float) shiftX - width), 0, Color.rgb(0x00c0ff).toAwt(), x + (float) shiftX, 0f, Color.rgb(0x00ffc0).toAwt(), true);
            renderer.color(Color.rgb(0x484848));
            renderer.fill(getBounds());

            Border border = new Border(0, 0, 1, 0);
            border.setPaint(p);
            border.paintBorder(renderer, x, y, width, height);

            renderer.paint(old);
            textColor = Color.white;
        } else if (isHovered()) {
            Paint old = renderer.getPaint();

            double shiftX = ((double) width * 2) * BubbleBlaster.getTicks() / (BubbleBlaster.TPS * 10);
            GradientPaint p = new GradientPaint(x + ((float) shiftX - width), 0, Color.rgb(0x00c0ff).toAwt(), x + (float) shiftX, 0f, Color.rgb(0x00ffc0).toAwt(), true);
            Border border = new Border(0, 0, 2, 0);
            border.setPaint(p);
            border.paintBorder(renderer, x, y, width, height);

            renderer.paint(old);
            textColor = Color.rgb(0xffffff);
        } else {
            renderer.stroke(new BasicStroke(1.0f));
            textColor = Color.rgb(0xe0e0e0);
        }

        OptionsNumberInput.ArrowButton.paint0a(renderer, textColor, getBounds(), text);
    }

    @SuppressWarnings("EmptyMethod")
    public void tick(Gamemode gamemode) {

    }
}
