package com.ultreon.bubbles.render.screen.gui;

public abstract class Shape {
    public abstract Rectangle getBounds();

    public abstract boolean contains(int x, int y);
}
