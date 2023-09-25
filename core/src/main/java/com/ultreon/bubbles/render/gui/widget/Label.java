package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.libs.text.v0.TextObject;

public class Label extends GuiComponent {
    private TextObject text;
    private boolean wrapped;

    protected Color foregroundColor;
    private int fontSize;

    public Label(String text, int x, int y, int width, int height) {
        this(TextObject.literal(text), x, y, width, height);
    }

    public Label(TextObject text, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.text = text;

        backgroundColor = Color.TRANSPARENT;
        foregroundColor = Color.WHITE;
    }

    @Override
    public void render(Renderer renderer) {
        renderComponent(renderer);
    }

    @Override
    public void renderComponent(Renderer renderer) {
        fill(renderer, 0, 0, width, height, backgroundColor);

        renderer.setColor(foregroundColor);

        if (wrapped) renderer.drawWrappedText(this.font, text.getText(), 0, 0, getWidth());
        else renderer.drawMultiLineText(this.font , text.getText(), 0, 0);
    }

    public TextObject getText() {
        return text;
    }

    public void setText(TextObject text) {
        this.text = text;
    }

    public String getLiteralText() {
        return this.text.getText();
    }

    public void setLiteralText(String text) {
        this.text = TextObject.literal(text);
    }

    public boolean isWrapped() {
        return wrapped;
    }

    public void setWrapped(boolean wrapped) {
        this.wrapped = wrapped;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontSize() {
        return fontSize;
    }
}
