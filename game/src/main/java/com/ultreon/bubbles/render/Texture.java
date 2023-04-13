package com.ultreon.bubbles.render;

import java.awt.image.Raster;

public abstract class Texture {
    public void draw(Renderer renderer, int x, int y, int width, int height) {
        draw(renderer, x, y, width, height, 0, 0, getWidth(), getHeight());
    }

    protected abstract int getWidth();

    protected abstract int getHeight();

    public abstract void draw(Renderer renderer, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight);

    public abstract Raster getRaster();
}
