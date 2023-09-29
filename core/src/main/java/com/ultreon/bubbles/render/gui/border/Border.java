package com.ultreon.bubbles.render.gui.border;

import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;

@SuppressWarnings("unused")
public class Border {
    private final Insets borderInsets;
    private boolean borderOpaque;
    protected Color color = Color.WHITE;
    protected RenderType renderType = RenderType.COLOR;
    protected float effectSpeed = BubbleBlasterConfig.DEFAULT_EFFECT_SPEED.get();

    public Border(Insets insets) {
        this.borderInsets = insets;
    }

    public Border(int all) {
        this(all, all);
    }

    public Border(int vertical, int horizontal) {
        this(vertical, horizontal, vertical, horizontal);
    }

    public Border(int top, int left, int bottom, int right) {
        this.borderInsets = new Insets(top, left, bottom, right);
    }

    public void drawBorder(Renderer renderer, float x, float y, float width, float height) {
        Insets insets = this.getBorderInsets();

        // Draw rectangles around the component, but do not draw
        // in the component area itself.
        switch (this.renderType) {
            case COLOR -> {
                renderer.setColor(this.color);
                renderer.fill(x + insets.left, y, width - insets.left - insets.right, insets.top);
                renderer.fill(x, y, insets.left, height);
                renderer.fill(x + width - insets.right, y, insets.right, height);
                renderer.fill(x + insets.left, y + height - insets.bottom, width - insets.left - insets.right, insets.bottom);
            }
            case EFFECT -> {
                renderer.fillEffect(x + insets.left, y, width - insets.left - insets.right, insets.top, this.effectSpeed);
                renderer.fillEffect(x, y, insets.left, height, this.effectSpeed);
                renderer.fillEffect(x + width - insets.right, y, insets.right, height, this.effectSpeed);
                renderer.fillEffect(x + insets.left, y + height - insets.bottom, width - insets.left - insets.right, insets.bottom, this.effectSpeed);
            }
            case ERROR_EFFECT -> {
                renderer.fillErrorEffect(x + insets.left, y, width - insets.left - insets.right, insets.top, this.effectSpeed);
                renderer.fillErrorEffect(x, y, insets.left, height, this.effectSpeed);
                renderer.fillErrorEffect(x + width - insets.right, y, insets.right, height, this.effectSpeed);
                renderer.fillErrorEffect(x + insets.left, y + height - insets.bottom, width - insets.left - insets.right, insets.bottom, this.effectSpeed);
            }
        }
    }

    public boolean isBorderOpaque() {
        return this.borderOpaque;
    }

    public void setBorderOpaque(boolean borderOpaque) {
        this.borderOpaque = borderOpaque;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public RenderType getRenderType() {
        return this.renderType;
    }

    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
    }

    public void setEffectSpeed(float effectSpeed) {
        this.effectSpeed = effectSpeed;
    }

    public float getEffectSpeed() {
        return this.effectSpeed;
    }

    public Insets getBorderInsets() {
        return this.borderInsets;
    }

    public void setBorderTop(int topWidth) {
        this.borderInsets.top = topWidth;
    }

    public void setBorderLeft(int leftWidth) {
        this.borderInsets.left = leftWidth;
    }

    public void setBorderBottom(int bottomWidth) {
        this.borderInsets.bottom = bottomWidth;
    }

    public void setBorderRight(int rightWidth) {
        this.borderInsets.right = rightWidth;
    }

    public int getBorderTop() {
        return this.borderInsets.top;
    }

    public int getBorderLeft() {
        return this.borderInsets.left;
    }

    public int getBorderBottom() {
        return this.borderInsets.bottom;
    }

    public int getBorderRight() {
        return this.borderInsets.right;
    }

    public enum RenderType {
        COLOR,
        EFFECT,
        ERROR_EFFECT,
    }
}