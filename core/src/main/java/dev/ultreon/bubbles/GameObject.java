package dev.ultreon.bubbles;

import dev.ultreon.bubbles.render.Renderer;
import dev.ultreon.bubbles.vector.Vector2D;

public abstract class GameObject {
    public final Vector2D pos = new Vector2D();

    /**
     * Get the current x position of the entity.
     *
     * @return the x position.
     */
    public double getX() {
        return this.pos.x;
    }

    /**
     * Set the current x position of the entity.
     * @param x the x position.
     */
    public void setX(float x) {
        this.pos.x = x;
    }

    /**
     * Get the current y position of the entity.
     *
     * @return the y position.
     */
    public double getY() {
        return this.pos.y;
    }

    /**
     * Set the current y position of the entity.
     * @param y the y position.
     */
    public void setY(float y) {
        this.pos.y = y;
    }

    public Vector2D getPos() {
        return this.pos;
    }

    public abstract void render(Renderer renderer);
}
