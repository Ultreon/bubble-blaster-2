package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.common.text.LiteralText;
import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.core.input.MouseInput;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.init.Sounds;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiStateListener;
import com.ultreon.bubbles.render.gui.border.Border;
import com.ultreon.bubbles.util.GraphicsUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

@SuppressWarnings("unused")
public class OptionsButton extends AbstractButton implements GuiStateListener {
    protected TextObject text;

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

    protected OptionsButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void mouseEnter(int x, int y) {
        super.mouseEnter(x, y);
        if (isWithinBounds(x, y)) {
            Sounds.UI_BUTTON_FOCUS_CHANGE.get().play(0.1f);
        }
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void render(Renderer renderer) {
        Rectangle bounds = getBounds();

        Color textColor;
        if (isPressed() && isWithinBounds(MouseInput.getPos())) {
            // Border
            Paint old = renderer.getPaint();
            GradientPaint p = new GradientPaint(0, y, Color.rgb(0x0080ff).toAwt(), 0f, y + height, Color.rgb(0x00ff80).toAwt());
            renderer.paint(p);
            renderer.fill(bounds);
            renderer.paint(old);

            textColor = Color.white;
        } else if (isHovered()) {
            renderer.color(Color.rgb(0x808080));
            renderer.fill(bounds);

            // Border
            double shiftX = ((double) width * 2) * BubbleBlaster.getTicks() / (BubbleBlaster.TPS * 10);
            GradientPaint p = new GradientPaint(x + ((float) shiftX - width), 0, Color.rgb(0x0080ff).toAwt(), x + (float) shiftX, 0f, Color.rgb(0x00ff80).toAwt(), true);
            Border border = new Border(0, 0, 2, 0);
            border.setPaint(p);
            border.paintBorder(renderer, x, y, width, height);

            textColor = Color.rgb(0xffffff);
        } else {
            renderer.color(Color.rgb(0x808080));
            renderer.fill(bounds);

            textColor = Color.lightGray;
        }

        paint0a(renderer, textColor, bounds, text);
    }

    static void paint0a(Renderer renderer, Color textColor, Rectangle bounds, TextObject text) {
        Renderer gg1 = renderer.subInstance(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);
        gg1.color(textColor);
        GraphicsUtils.drawCenteredString(gg1, text, new Rectangle2D.Double(0, 0, bounds.width - 2, bounds.height - 2), new Font(BubbleBlaster.getInstance().getFont().getName(), Font.BOLD, 16));
        gg1.dispose();
    }
}
