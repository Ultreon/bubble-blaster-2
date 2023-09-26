package com.ultreon.bubbles.render.gui.widget;

import com.badlogic.gdx.math.Rectangle;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiStateListener;
import com.ultreon.libs.text.v0.TextObject;


public class CrashButton extends AbstractButton implements GuiStateListener {
    private Runnable command;
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
        private Rectangle _bounds = new Rectangle(10, 10, 96, 48);
        private TextObject _text = TextObject.EMPTY;
        private Runnable _command = () -> {
        };

        public Builder() {
        }

        public CrashButton build() {
            CrashButton button = new CrashButton((int) _bounds.x, (int) _bounds.y, (int) _bounds.width, (int) _bounds.height);

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
            this._text = TextObject.literal(text);
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
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        Color textColor;

        if (isPressed()) {
//            Paint old = renderer.getPaint();
//            GradientPaint p = new GradientPaint(0, 0, Color.rgb(0xff0000).toAwt(), width, height, Color.rgb(0xff4000).toAwt());
//            renderer.paint(p);
//            renderer.rect(0, 0, width, height);
//            renderer.paint(old);

            textColor = Color.WHITE;
        } else if (isHovered()) {
            renderer.setStrokeWidth(4.0f);

//            Paint old = renderer.getPaint();
//            GradientPaint p = new GradientPaint(0, 0, Color.rgb(0xff0000).toAwt(), width, height, Color.rgb(0xff4000).toAwt());
//            renderer.paint(p);
//            Border border = new OuterBorder(2, 2, 2, 2);
//            border.setPaint(p);
//            border.paintBorder(renderer, 1, 1, width - 2, height - 2);
//
//            renderer.paint(old);

            textColor = Color.rgb(0xffffff);
        } else {
            renderer.setStrokeWidth(1.0f);

            renderer.setColor(Color.rgb(0xffffff));
//            Border border = new Border(1, 1, 1, 1);
//            border.setPaint(Color.rgb(0xffffff).toAwt());
//            border.paintBorder(renderer, 0, 0, width, height);

            textColor = Color.rgb(0xffffff);
        }

        OptionsNumberInput.ArrowButton.drawText(renderer, textColor, getPos(), getSize(), text, font);
    }

    public Runnable getCommand() {
        return command;
    }

    public void setCommand(Runnable command) {
        this.command = command;
    }
}
