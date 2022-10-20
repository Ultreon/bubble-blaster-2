package com.ultreon.bubbles.render;

public class Insets extends java.awt.Insets {
    /**
     * Creates and initializes a new {@code Insets} object with the
     * specified top, left, bottom, and right insets.
     *
     * @param top    the inset from the top.
     * @param left   the inset from the left.
     * @param bottom the inset from the bottom.
     * @param right  the inset from the right.
     */
    public Insets(int top, int left, int bottom, int right) {
        super(top, left, bottom, right);
    }

    public void shrink(int amount) {
        top -= amount;
        left -= amount;
        bottom -= amount;
        right -= amount;
    }
}
