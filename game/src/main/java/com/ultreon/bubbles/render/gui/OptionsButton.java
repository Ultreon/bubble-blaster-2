package com.ultreon.bubbles.render.gui;

import com.ultreon.bubbles.animation.Animation;
import com.ultreon.bubbles.common.text.LiteralText;
import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.game.BubbleBlaster;
import com.ultreon.bubbles.core.input.MouseInput;
import com.ultreon.bubbles.media.SoundInstance;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.screen.gui.AbstractButton;
import com.ultreon.bubbles.render.screen.gui.GuiElement;
import com.ultreon.bubbles.render.screen.gui.Rectangle;
import com.ultreon.bubbles.render.screen.gui.border.Border;
import com.ultreon.bubbles.util.GraphicsUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

@SuppressWarnings("unused")
public class OptionsButton extends AbstractButton implements GuiElement {
    protected TextObject text;
    private final Animation grayAnim = new Animation(0x80);
    private final Animation borderAnim = new Animation(0);
    private boolean wasHovered;
    private double transitionSpeed = 0.2;

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
        private com.ultreon.bubbles.render.screen.gui.Rectangle bounds = new com.ultreon.bubbles.render.screen.gui.Rectangle(10, 10, 96, 48);
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

        public Builder bounds(com.ultreon.bubbles.render.screen.gui.Rectangle bounds) {
            this.bounds = bounds;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            this.bounds = new com.ultreon.bubbles.render.screen.gui.Rectangle(x, y, width, height);
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

    public double getTransitionSpeed() {
        return transitionSpeed;
    }

    public void setTransitionSpeed(double transitionSpeed) {
        this.transitionSpeed = transitionSpeed;
    }

    @Override
    public void mouseEnter(int x, int y) {
        super.mouseEnter(x, y);
        if (isWithinBounds(x, y)) {
            SoundInstance focusChangeSFX = new SoundInstance(BubbleBlaster.id("sfx/ui/button/focus_change"), "focusChange");
            focusChangeSFX.setVolume(0.2d);
            focusChangeSFX.play();
        }
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void render(Renderer renderer) {
        com.ultreon.bubbles.render.screen.gui.Rectangle bounds = getBounds();

        boolean hovered = isHovered();
        if (hovered != wasHovered) {
            wasHovered = hovered;
            if (hovered) {
                grayAnim.goTo(0xa0, transitionSpeed);
                borderAnim.goTo(2, transitionSpeed);
            } else {
                grayAnim.goTo(0x80, transitionSpeed);
                borderAnim.goTo(0, transitionSpeed);
            }
        }

        var i = (int)grayAnim.get();

        renderer.color(new Color(i, i, i));
        renderer.fill(bounds);

        Color textColor;
        if (isPressed() && isWithinBounds(MouseInput.getPos())) {
            // Border
            Paint old = renderer.getPaint();
            GradientPaint p = new GradientPaint(0, y, new Color(0, 192, 255), 0f, y + height, new Color(0, 255, 192));
            renderer.paint(p);
            renderer.fill(bounds);
            renderer.paint(old);

            textColor = Color.white;
        } else if (hovered) {
            // Border
            double shiftX = ((double) width * 2) * BubbleBlaster.getTicks() / (BubbleBlaster.TPS * 10);
            GradientPaint p = new GradientPaint(x + ((float) shiftX - width), 0, new Color(0, 192, 255), x + (float) shiftX, 0f, new Color(0, 255, 192), true);
            Border border = new Border(0, 0, (int) borderAnim.get(), 0);
            border.setPaint(p);
            border.paintBorder(renderer, x, y, width, height);

            textColor = new Color(255, 255, 255);
        } else {
            textColor = new Color(192, 192, 192);
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
