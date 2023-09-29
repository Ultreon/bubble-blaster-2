package com.ultreon.bubbles.render.gui.border;

import com.ultreon.bubbles.BubbleBlasterConfig;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Insets;
import com.ultreon.bubbles.render.Renderer;

@SuppressWarnings("unused")
public class Border {
    private final Insets borderInsets;
    private boolean borderOpaque;
    protected Color color;
    protected RenderType renderType;
    protected int effectSpeed = BubbleBlasterConfig.DEFAULT_EFFECT_SPEEED.get();

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

    public void drawBorder(Renderer renderer, int x, int y, int width, int height) {
        Insets insets = getBorderInsets();

        // Draw rectangles around the component, but do not draw
        // in the component area itself.
        switch (renderType) {
            case COLOR -> {
                renderer.setColor(this.color);
                renderer.rect(x + insets.left, y, width - insets.left - insets.right, insets.top);
                renderer.rect(x, y, insets.left, height);
                renderer.rect(x + width - insets.right, y, insets.right, height);
                renderer.rect(x + insets.left, y + height - insets.bottom, width - insets.left - insets.right, insets.bottom);
            }
            case EFFECT -> {
                renderer.fillEffect(x + insets.left, y, width - insets.left - insets.right, insets.top, effectSpeed);
                renderer.fillEffect(x, y, insets.left, height, effectSpeed);
                renderer.fillEffect(x + width - insets.right, y, insets.right, height, effectSpeed);
                renderer.fillEffect(x + insets.left, y + height - insets.bottom, width - insets.left - insets.right, insets.bottom, effectSpeed);
            }
            case ERROR_EFFECT -> {
                renderer.fillErrorEffect(x + insets.left, y, width - insets.left - insets.right, insets.top, effectSpeed);
                renderer.fillErrorEffect(x, y, insets.left, height, effectSpeed);
                renderer.fillErrorEffect(x + width - insets.right, y, insets.right, height, effectSpeed);
                renderer.fillErrorEffect(x + insets.left, y + height - insets.bottom, width - insets.left - insets.right, insets.bottom, effectSpeed);
            }
        }
    }

    public boolean isBorderOpaque() {
        return borderOpaque;
    }

    public void setBorderOpaque(boolean borderOpaque) {
        this.borderOpaque = borderOpaque;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
    }

    public void setEffectSpeed(int effectSpeed) {
        this.effectSpeed = effectSpeed;
    }

    public int getEffectSpeed() {
        return effectSpeed;
    }

    public Insets getBorderInsets() {
        return borderInsets;
    }

    public void setBorderTop(int topWidth) {
        borderInsets.top = topWidth;
    }

    public void setBorderLeft(int leftWidth) {
        borderInsets.left = leftWidth;
    }

    public void setBorderBottom(int bottomWidth) {
        borderInsets.bottom = bottomWidth;
    }

    public void setBorderRight(int rightWidth) {
        borderInsets.right = rightWidth;
    }

    public int getBorderTop() {
        return borderInsets.top;
    }

    public int getBorderLeft() {
        return borderInsets.left;
    }

    public int getBorderBottom() {
        return borderInsets.bottom;
    }

    public int getBorderRight() {
        return borderInsets.right;
    }

    public enum RenderType {
        COLOR,
        EFFECT,
        ERROR_EFFECT,
    }
}