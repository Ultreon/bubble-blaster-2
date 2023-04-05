package com.ultreon.bubbles.render.gui.border;

import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;

import java.awt.*;

@SuppressWarnings("unused")
public class OuterBorder extends Border {
    public OuterBorder(com.ultreon.bubbles.render.Insets insets) {
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
     * @param x        the x-positon.
     * @param y        the y-position.
     * @param width    the width.
     * @param height   the height.
     */
    @Override
    public void paintBorder(Renderer renderer, int x, int y, int width, int height) {
        // Get insets
        Insets insets = getBorderInsets();

        // Save old paint.
        Paint oldPaint = renderer.getPaint();

        // Set paint.
        renderer.paint(getPaint());

        // Draw rectangles around the component, but do not draw
        // in the component area itself.
        renderer.rect(x - insets.left, y - insets.top, width + insets.left + insets.right, insets.top);
        renderer.rect(x - insets.left, y, insets.left, height);
        renderer.rect(x + width, y, insets.right, height);
        renderer.rect(x - insets.left, y + height, width + insets.left + insets.right, insets.bottom);

        // Set backup paint.
        renderer.paint(oldPaint);
    }
}
