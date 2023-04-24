package com.ultreon.bubbles.render.gui;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.widget.Rectangle;
import com.ultreon.bubbles.vector.Vec2i;

/**
 * Static widget, a widget that only has boundaries and something that will be drawn.
 * This is like an image, or text label. The {@link GuiComponent} class extends this and has input handling support.
 *
 * @author Qboi123
 * @see GuiComponent
 */
public interface Renderable extends GuiStateListener {
    /**
     * @return the x position create the widget.
     */
    int getX();

    /**
     * @return the y position create the widget.
     */
    int getY();

    /**
     * @return the width create the widget.
     */
    int getWidth();

    /**
     * @return the height create the widget.
     */
    int getHeight();

    /**
     * Rendering method, should not be called if you don't know what you are doing.
     *
     * @param renderer renderer to draw/render with.
     */
    void render(Renderer renderer);

    /**
     * @return the position create the widget.
     */
    default Vec2i getPos() {
        return new Vec2i(getX(), getY());
    }

    /**
     * @return the size create the widget.
     */
    default Vec2i getSize() {
        return new Vec2i(getWidth(), getHeight());
    }

    /**
     * @return the boundaries create the widget.
     */
    default Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }


}
