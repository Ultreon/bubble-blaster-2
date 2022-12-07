package com.ultreon.bubbles.util.position;

import java.awt.geom.Dimension2D;
import java.io.Serializable;

public abstract class Size extends Dimension2D implements Serializable {
    private double height;
    private double width;

    public Size(double w, double h) {
        this.width = w;
        this.height = h;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public abstract boolean isAbsolute();
}
