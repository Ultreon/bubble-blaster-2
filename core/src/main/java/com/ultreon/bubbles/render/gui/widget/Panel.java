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
    public void render(Renderer renderer, int mouseX, int mouseY, float deltaTime) {
        this.renderComponent(renderer);
        this.renderChildren(renderer, mouseX, mouseY, deltaTime);
    }

    @Override
    public void renderComponent(Renderer renderer) {
        GuiComponent.fill(renderer, 0, 0, this.width, this.height, this.getBackgroundColor());
    }

}
