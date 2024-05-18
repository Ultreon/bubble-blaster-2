package dev.ultreon.bubbles.render.gui;

import com.badlogic.gdx.math.Rectangle;

public abstract class Shape {
    public abstract Rectangle getBounds();

    public abstract boolean contains(int x, int y);
}
