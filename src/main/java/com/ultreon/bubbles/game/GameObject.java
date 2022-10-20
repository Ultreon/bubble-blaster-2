package com.ultreon.bubbles.game;

import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.vector.Vec2f;

public abstract class GameObject {
    private float x;
    private float y;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    protected void setX(float x) {
        this.x = x;
    }

    protected void setY(float y) {
        this.y = y;
    }

    public Vec2f getPosition() {
        return new Vec2f(x, y);
    }

    public abstract void render(Renderer renderer);
}
