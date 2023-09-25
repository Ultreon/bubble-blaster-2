package com.ultreon.bubbles.common;

import java.io.Serializable;

/**
 * @author XyperCode
 * @since 0.0.0
 */
public class EntityPos implements Serializable {
    protected final int x;
    protected final int y;

    public EntityPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
