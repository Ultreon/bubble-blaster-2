package com.ultreon.bubbles.render.gui.widget;

import com.ultreon.bubbles.render.Renderer;
import org.checkerframework.common.value.qual.IntRange;

public class Panel extends Container {
    /**
     * @param x      position create the widget
     * @param y      position create the widget
     * @param width  size create the widget
     * @param height size create the widget
     */
    public Panel(int x, int y, @IntRange(from = 0) int width, @IntRange(from = 0) int height) {
        super(x, y, width, height);
    }

    @Override
    public void renderChildren(Renderer renderer) {
        children.forEach(c -> c.render(renderer));
    }

    @Override
    public void render(Renderer renderer) {
        Renderer ngg2 = renderer.subInstance(getX(), getY(), getWidth(), getHeight());
        renderComponent(ngg2);
        ngg2.dispose();

        Renderer ngg3 = renderer.subInstance(getX(), getY(), getWidth(), getHeight());
        renderChildren(ngg3);
        ngg3.dispose();
    }

    @Override
    public void renderComponent(Renderer renderer) {
        renderer.color(getBackgroundColor());
        renderer.fill(getBounds());
    }

    @Override
    public void tick() {

    }
}
