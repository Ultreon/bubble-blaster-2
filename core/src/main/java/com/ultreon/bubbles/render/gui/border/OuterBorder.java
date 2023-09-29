package com.ultreon.bubbles.render.gui.border;

import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;

@SuppressWarnings("unused")
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
    public void drawBorder(Renderer renderer, int x, int y, int width, int height) {
        Insets insets = getBorderInsets();

        // Draw rectangles around the component, but do not draw
        // in the component area itself.
        switch (renderType) {
            case COLOR -> {
                renderer.setColor(color);
                renderer.rect(x - insets.left, y - insets.top, width + insets.left + insets.right, insets.top);
                renderer.rect(x - insets.left, y, insets.left, height);
                renderer.rect(x + width, y, insets.right, height);
                renderer.rect(x - insets.left, y + height, width + insets.left + insets.right, insets.bottom);
            }
            case EFFECT -> {
                renderer.fillEffect(x - insets.left, y - insets.top, width + insets.left + insets.right, insets.top, effectSpeed);
                renderer.fillEffect(x - insets.left, y, insets.left, height, effectSpeed);
                renderer.fillEffect(x + width, y, insets.right, height, effectSpeed);
                renderer.fillEffect(x - insets.left, y + height, width + insets.left + insets.right, insets.bottom, effectSpeed);
            }
            case ERROR_EFFECT -> {
                renderer.fillErrorEffect(x - insets.left, y - insets.top, width + insets.left + insets.right, insets.top, effectSpeed);
                renderer.fillErrorEffect(x - insets.left, y, insets.left, height, effectSpeed);
                renderer.fillErrorEffect(x + width, y, insets.right, height, effectSpeed);
                renderer.fillErrorEffect(x - insets.left, y + height, width + insets.left + insets.right, insets.bottom, effectSpeed);
            }
        }
    }
}
