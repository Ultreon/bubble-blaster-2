package com.ultreon.bubbles.util.position;

import com.ultreon.bubbles.util.position.util.DimensionDouble;

import java.awt.*;

public class AWTWidgetRelativeSize extends RelativeSize {
    private Component component;
    private double relHeight;
    private double relWidth;

    private static DimensionDouble getSize(Component component, double relW, double relH) {
        double comW = component.getWidth();
        double comH = component.getHeight();

        return new DimensionDouble(comW + relW, comH + relH);
    }

    public AWTWidgetRelativeSize(Component component, double relW, double relH) {
        super(getSize(component, relW, relH));
        this.component = component;
    }

    @Override
    public void tick() {
        this.setSize(getSize(component, relWidth, relHeight));
    }

    public double getRelHeight() {
        return relHeight;
    }

    public double getRelWidth() {
        return relWidth;
    }

    public Component getComponent() {
        return component;
    }

    public void setRelHeight(double relHeight) {
        this.relHeight = relHeight;
        tick();
    }

    public void setRelWidth(double relWidth) {
        this.relWidth = relWidth;
        tick();
    }

    public void setComponent(Component component) {
        this.component = component;
        tick();
    }
}
