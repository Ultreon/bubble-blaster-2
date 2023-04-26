package com.ultreon.bubbles.render;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;

import java.awt.*;
import java.awt.geom.AffineTransform;

class RenderState {
    private final Matrix4 transform;
    private final Color clearColor;
    private final Color color;
    private final Paint paint;
    private final BitmapFont font;
    private final Shape clip;
    private final BitmapFont fallbackFont;
    private final Renderer renderer;
    private final Composite composite;
    private final RenderingHints hints;
    private final Stroke stroke;
    private double translationX;
    private double translationY;

    RenderState(Renderer renderer) {
        this.renderer = renderer;
        this.clearColor = this.renderer.getClearColor();
        this.transform = this.renderer.getTransform();
        this.color = this.renderer.getColor();
        this.paint = this.renderer.getPaint();
        this.font = this.renderer.getFont();
        this.clip = this.renderer.getClip();
        this.fallbackFont = this.renderer.getFallbackFont();
        this.composite = this.renderer.getComposite();
        this.hints = this.renderer.getRenderingHints();
        this.stroke = this.renderer.getStroke();
    }

    void revert() {
        this.renderer.translate(-translationX, -translationY);
//        this.renderer.setTransform(transform);
        this.renderer.clearColor(clearColor);
        this.renderer.setColor(color);
        this.renderer.paint(paint);
        this.renderer.setFont(font);
        this.renderer.simpleClip(clip);
        this.renderer.fallbackFont(fallbackFont);
        this.renderer.composite(composite);
        this.renderer.hints(hints);
        this.renderer.stroke(stroke);
    }
}
