package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;

public class Label extends GuiComponent {
    private String text;
    private boolean wrapped;

    protected Color foregroundColor;

    public Label(String text, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.text = text;

        backgroundColor = Color.transparent;
        foregroundColor = Color.white;
    }

    @Override
    public void render(Renderer renderer) {
        renderComponent(renderer);
    }

    @Override
    public void renderComponent(Renderer renderer) {
        fill(renderer, 0, 0, width, height, backgroundColor);

        renderer.color(foregroundColor);

        if (wrapped) renderer.wrappedText(text, 0, 0, getWidth());
        else renderer.multiLineText(text, 0, 0);
    }

    @Override
    public void tick() {

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
