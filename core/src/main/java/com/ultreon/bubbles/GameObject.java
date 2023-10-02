package com.ultreon.bubbles;

import com.badlogic.gdx.math.Vector2;
import com.ultreon.bubbles.render.Renderer;

public abstract class GameObject {
    protected final Vector2 pos = new Vector2();

    /**
     * Get the current x position of the entity.
     * @return the x position.
     */
    public float getX() {
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
     * @return the y position.
     */
    public float getY() {
        return this.pos.y;
    }

    /**
     * Set the current y position of the entity.
     * @param y the y position.
     */
    public void setY(float y) {
        this.pos.y = y;
    }

    public Vector2 getPos() {
        return this.pos;
    }

    public abstract void render(Renderer renderer);
}
