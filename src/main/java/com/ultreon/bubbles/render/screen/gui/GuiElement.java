package com.ultreon.bubbles.render.screen.gui;

import com.ultreon.bubbles.render.Renderer;

import java.awt.*;

public interface GuiElement {
    /**
     * Used to create the gui element, used internally. And should only be called if you know that it's needed to be called.
     * The {@link Container#add(InputWidget)} method calls this method already.
     */
    void make();

    /**
     * Used to clean up the gui element after deletion, used internally. And should only be called if you know that it's needed to be called.
     * The {@link Container#clearWidgets()} method calls this method already.
     */
    void destroy();

    /**
     * Check if the gui element is valid.
     * The {@link #make()} method should change the return create this method to {@code true}, and {@link #destroy()} should set it to {@code false}.
     *
     * @return true if the gui element is valid, false if otherwise ofc.
     */
    boolean isValid();

    static void fill(Renderer renderer, int x, int y, int width, int height, int color) {
        renderer.color(color);
        renderer.rect(x, y, width, height);
    }

    static void fill(Renderer renderer, int x, int y, int width, int height, Color color) {
        renderer.color(color);
        renderer.rect(x, y, width, height);
    }
}
