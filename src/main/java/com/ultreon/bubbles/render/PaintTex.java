package com.ultreon.bubbles.render;

import java.awt.*;

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
    public void render(Renderer renderer, int xf, int yf, int xs, int ys) {
        renderer.paint(paint);
        switch (mode) {
            case DRAW -> renderer.rectLine(xf, yf, xs - xf, ys - yf);
            case FILL -> renderer.rect(xf, yf, xs - xf, ys - yf);
        }
    }

    enum Mode {FILL, DRAW}
}
