package com.ultreon.bubbles.render;

import java.awt.*;
import java.awt.geom.AffineTransform;

class RenderState {
    private final AffineTransform transform;
    private final Color clearColor;
    private final Color color;
    private final Paint paint;
    private final Font font;
    private final Shape clip;
    private final Font fallbackFont;
    private final Renderer renderer;
    private final Composite composite;
    private final RenderingHints hints;
    private final Stroke stroke;
    private double translationX;
    private double translationY;

    RenderState(Renderer renderer) {
        this.renderer = renderer;
        this.clearColor = this.renderer.gfx.getBackground();
        this.transform = this.renderer.gfx.getTransform();
        this.color = this.renderer.gfx.getColor();
        this.paint = this.renderer.gfx.getPaint();
        this.font = this.renderer.gfx.getFont();
        this.clip = this.renderer.gfx.getClip();
        this.fallbackFont = this.renderer.fallbackFont;
        this.composite = this.renderer.gfx.getComposite();
        this.hints = this.renderer.gfx.getRenderingHints();
        this.stroke = this.renderer.gfx.getStroke();
    }

    void revert() {
        this.renderer.translate(-translationX, -translationY);
        this.renderer.setTransform(transform);
        this.renderer.clearColor(clearColor);
        this.renderer.color(color);
        this.renderer.paint(paint);
        this.renderer.font(font);
        this.renderer.simpleClip(clip);
        this.renderer.fallbackFont(fallbackFont);
        this.renderer.composite(composite);
        this.renderer.hints(hints);
        this.renderer.stroke(stroke);
    }
}
