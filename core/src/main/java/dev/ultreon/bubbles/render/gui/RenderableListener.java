package dev.ultreon.bubbles.render.gui;

import com.badlogic.gdx.math.Rectangle;
import dev.ultreon.bubbles.render.Renderable;
import dev.ultreon.libs.commons.v0.vector.Vec2i;

/**
 * Static widget, a widget that only has boundaries and something that will be drawn.
 * This is like an image, or text label. The {@link GuiComponent} class extends this and has input handling support.
 *
 * @author XyperCode
 * @see GuiComponent
 */
public interface RenderableListener extends GuiStateListener, Renderable {
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
     * @return the position create the widget.
     */
    default Vec2i getPos() {
        return new Vec2i(this.getX(), this.getY());
    }

    /**
     * @return the size create the widget.
     */
    default Vec2i getSize() {
        return new Vec2i(this.getWidth(), this.getHeight());
    }

    /**
     * @return the boundaries create the widget.
     */
    default Rectangle getBounds() {
        return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }


}
