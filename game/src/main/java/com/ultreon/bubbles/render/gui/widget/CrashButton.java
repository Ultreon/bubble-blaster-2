package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.common.text.LiteralText;
import com.ultreon.bubbles.common.text.TextObject;
import com.ultreon.bubbles.core.input.MouseInput;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiStateListener;
import com.ultreon.bubbles.render.gui.border.Border;
import com.ultreon.bubbles.render.gui.border.OuterBorder;

import java.awt.*;

public class CrashButton extends AbstractButton implements GuiStateListener {
    private Runnable command;
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
        private Rectangle _bounds = new Rectangle(10, 10, 96, 48);
        private TextObject _text = TextObject.EMPTY;
        private Runnable _command = () -> {
        };

        public Builder() {
        }

        public CrashButton build() {
            CrashButton button = new CrashButton(_bounds.x, _bounds.y, _bounds.width, _bounds.height);

            button.setText(_text);
            button.setCommand(_command);
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
            this._text = new LiteralText(text);
            return this;
        }

        public Builder text(TextObject text) {
            this._text = text;
            return this;
        }

        public Builder command(Runnable command) {
            this._command = command;
            return this;
        }
    }

    public CrashButton(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(Renderer renderer) {
        Color textColor;

        if (isPressed()) {
            Paint old = renderer.getPaint();
            GradientPaint p = new GradientPaint(0, y, Color.rgb(0xff0000).toAwt(), width, y + height, Color.rgb(0xff4000).toAwt());
            renderer.paint(p);
            renderer.fill(getBounds());
            renderer.paint(old);

            textColor = Color.white;
        } else if (isHovered()) {
            renderer.stroke(new BasicStroke(4.0f));

            Paint old = renderer.getPaint();
            GradientPaint p = new GradientPaint(0, y, Color.rgb(0xff0000).toAwt(), width, y + height, Color.rgb(0xff4000).toAwt());
            renderer.paint(p);
            Border border = new OuterBorder(2, 2, 2, 2);
            border.setPaint(p);
            border.paintBorder(renderer, x + 1, y + 1, width - 2, height - 2);

            renderer.paint(old);

            textColor = Color.rgb(0xffffff);
        } else {
            renderer.stroke(new BasicStroke(1.0f));

            renderer.color(Color.rgb(0xffffff));
            Border border = new Border(1, 1, 1, 1);
            border.setPaint(Color.rgb(0xffffff).toAwt());
            border.paintBorder(renderer, x, y, width, height);

            textColor = Color.rgb(0xffffff);
        }

        OptionsNumberInput.ArrowButton.paint0a(renderer, textColor, getBounds(), text);
    }

    public Runnable getCommand() {
        return command;
    }

    public void setCommand(Runnable command) {
        this.command = command;
    }
}
