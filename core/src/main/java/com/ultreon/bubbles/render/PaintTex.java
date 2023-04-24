package com.ultreon.bubbles.render;

import java.awt.*;
import java.awt.image.Raster;

@Deprecated
public class PaintTex extends Texture {
    private Paint paint;
    private final Mode mode = Mode.FILL;

    public void modify(Paint paint) {
        this.paint = paint;
    }

    public PaintTex(Paint paint) {
        this.paint = paint;
    }

    @Override
    protected int getWidth() {
        return 0;
    }

    @Override
    protected int getHeight() {
        return 0;
    }

    @Override
    public void draw(Renderer renderer, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight) {
        renderer.paint(paint);
        switch (mode) {
            case DRAW -> renderer.rectLine(x, y, width, height);
            case FILL -> renderer.rect(x, y, width, height);
        }
    }

    @Override
    public Raster getRaster() {
        return null;
    }

    enum Mode {FILL, DRAW}
}
