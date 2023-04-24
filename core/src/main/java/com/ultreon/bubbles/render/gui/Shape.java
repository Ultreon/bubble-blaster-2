package com.ultreon.bubbles.render.gui;

import com.ultreon.bubbles.render.gui.widget.Rectangle;

public abstract class Shape {
    public abstract Rectangle getBounds();

    public abstract boolean contains(int x, int y);
}
