package com.ultreon.bubbles.render.gui.border;

import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;

import java.awt.*;

@SuppressWarnings("unused")
public class Border {
    private final Insets borderInsets;
    private boolean borderOpaque;
    private Paint paint = new Color(0, 0, 0);

    public Border(Insets insets) {
        this.borderInsets = insets;
    }

    public Border(int all) {
        this(all, all);
    }

    public Border(int vertical, int horizontal) {
        this(vertical, horizontal, vertical, horizontal);
    }

    public Border(int top, int left, int bottom, int right) {
        this.borderInsets = new com.ultreon.bubbles.render.Insets(top, left, bottom, right);
    }

    public void paintBorder(Renderer renderer, int x, int y, int width, int height) {
        Insets insets = getBorderInsets();

        //  Set paint.
        renderer.paint(paint);

        //  Draw rectangles around the component, but do not draw
        //  in the component area itself.
        renderer.rect(x + insets.left, y, width - insets.left - insets.right, insets.top);
        renderer.rect(x, y, insets.left, height);
        renderer.rect(x + width - insets.right, y, insets.right, height);
        renderer.rect(x + insets.left, y + height - insets.bottom, width - insets.left - insets.right, insets.bottom);
    }

    public boolean isBorderOpaque() {
        return borderOpaque;
    }

    public void setBorderOpaque(boolean borderOpaque) {
        this.borderOpaque = borderOpaque;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Insets getBorderInsets() {
        return borderInsets;
    }

    public void setBorderTop(int topWidth) {
        borderInsets.top = topWidth;
    }

    public void setBorderLeft(int leftWidth) {
        borderInsets.left = leftWidth;
    }

    public void setBorderBottom(int bottomWidth) {
        borderInsets.bottom = bottomWidth;
    }

    public void setBorderRight(int rightWidth) {
        borderInsets.right = rightWidth;
    }

    public int getBorderTop() {
        return borderInsets.top;
    }

    public int getBorderLeft() {
        return borderInsets.left;
    }

    public int getBorderBottom() {
        return borderInsets.bottom;
    }

    public int getBorderRight() {
        return borderInsets.right;
    }
}