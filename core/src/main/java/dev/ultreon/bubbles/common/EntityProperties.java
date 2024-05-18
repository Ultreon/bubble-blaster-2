package dev.ultreon.bubbles.common;

import java.io.Serializable;

/**
 * @author XyperCode
 * @since 0.0.0
 */
public abstract class EntityProperties implements Serializable {
    protected final int x;
    protected final int y;

    public EntityProperties(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
