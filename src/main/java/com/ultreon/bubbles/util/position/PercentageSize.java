package com.ultreon.bubbles.util.position;

import java.awt.geom.Dimension2D;

public abstract class PercentageSize extends Size {
    private double rawHeight;
    private double rawWidth;
    private double percentHeight;
    private double percentWidth;

    public PercentageSize(double w, double h, double percentWidth, double percentHeight) {
        super(w * percentWidth, h * percentHeight);
        this.rawWidth = w;
        this.rawHeight = h;
        this.percentWidth = percentWidth;
        this.percentHeight = percentHeight;
    }

    public PercentageSize(Dimension2D dimension, double percentWidth, double percentHeight) {
        super(dimension.getWidth() * percentWidth, dimension.getHeight() * percentHeight);
        this.rawWidth = dimension.getWidth();
        this.rawHeight = dimension.getHeight();
        this.percentWidth = percentWidth;
        this.percentHeight = percentHeight;
    }

    @Override
    public boolean isAbsolute() {
        return false;
    }

    public abstract void tick();

    public double getPercentWidth() {
        return percentWidth;
    }

    public double getPercentHeight() {
        return percentHeight;
    }

    public void setPercentWidth(double percentWidth) {
        this.percentWidth = percentWidth;
        this.setSize(rawWidth * percentWidth, getHeight());
    }

    public void setPercentHeight(double percentHeight) {
        this.percentHeight = percentHeight;
        this.setSize(getWidth(), rawHeight * percentHeight);
    }

    public double getRawWidth() {
        return rawWidth;
    }

    public double getRawHeight() {
        return rawHeight;
    }

    public void setRawWidth(double rawWidth) {
        this.rawWidth = rawWidth;
        this.setSize(rawWidth * percentWidth, getHeight());
    }

    public void setRawHeight(double rawHeight) {
        this.rawHeight = rawHeight;
        this.setSize(getWidth(), rawHeight * percentHeight);
    }
}
