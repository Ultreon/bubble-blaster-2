package com.ultreon.bubbles.render.gui.border;

import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;

public class OuterBorder extends Border {
    public OuterBorder(Insets insets) {
        super(insets);
    }

    public OuterBorder(int all) {
        super(all);
    }

    public OuterBorder(int vertical, int horizontal) {
        super(vertical, horizontal);
    }

    public OuterBorder(int top, int left, int bottom, int right) {
        super(top, left, bottom, right);
    }

    /**
     * Paints a border.
     *
     * @param renderer the graphics.
     * @param x        the x-position.
     * @param y        the y-position.
     * @param width    the width.
     * @param height   the height.
     */
    @Override
    public void drawBorder(Renderer renderer, float x, float y, float width, float height) {
        var insets = this.getBorderInsets();

        // Draw rectangles around the component, but do not draw
        // in the component area itself.
        switch (this.renderType) {
            case COLOR:
                renderer.fill(x - insets.left, y - insets.top, width + insets.left + insets.right, insets.top, this.color);
                renderer.fill(x - insets.left, y, insets.left, height, this.color);
                renderer.fill(x + width, y, insets.right, height, this.color);
                renderer.fill(x - insets.left, y + height, width + insets.left + insets.right, insets.bottom, this.color);
                break;
            case EFFECT:
                renderer.fillEffect(x - insets.left, y - insets.top, width + insets.left + insets.right, insets.top, this.effectSpeed);
                renderer.fillEffect(x - insets.left, y, insets.left, height, this.effectSpeed);
                renderer.fillEffect(x + width, y, insets.right, height, this.effectSpeed);
                renderer.fillEffect(x - insets.left, y + height, width + insets.left + insets.right, insets.bottom, this.effectSpeed);
                break;
            case ERROR_EFFECT:
                renderer.fillErrorEffect(x - insets.left, y - insets.top, width + insets.left + insets.right, insets.top, this.effectSpeed);
                renderer.fillErrorEffect(x - insets.left, y, insets.left, height, this.effectSpeed);
                renderer.fillErrorEffect(x + width, y, insets.right, height, this.effectSpeed);
                renderer.fillErrorEffect(x - insets.left, y + height, width + insets.left + insets.right, insets.bottom, this.effectSpeed);
                break;
        }
    }
}
