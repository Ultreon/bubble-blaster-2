package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import com.ultreon.libs.text.v1.TextObject;

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

        this.backgroundColor = Color.TRANSPARENT;
        this.foregroundColor = Color.WHITE;
    }

    @Override
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.renderComponent(renderer);
    }

    @Override
    public void renderComponent(Renderer renderer) {
        GuiComponent.fill(renderer, 0, 0, this.width, this.height, this.backgroundColor);

        renderer.setColor(this.foregroundColor);

        if (this.wrapped) renderer.drawWrappedText(this.font, this.text.getText(), 0, 0, this.getWidth(), this.foregroundColor);
        else renderer.drawMultiLineText(this.font , this.text.getText(), 0, 0, this.foregroundColor);
    }

    public TextObject getText() {
        return this.text;
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
        return this.wrapped;
    }

    public void setWrapped(boolean wrapped) {
        this.wrapped = wrapped;
    }

    public Color getForegroundColor() {
        return this.foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontSize() {
        return this.fontSize;
    }
}
