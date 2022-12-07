package com.ultreon.bubbles.render.gui;

import java.awt.*;
import java.io.Serializable;

public class QDimension extends Dimension implements Serializable, Cloneable {
    public double width;
    public double height;

    public QDimension(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(QDimension size) {
        this.width = size.width;
        this.height = size.height;
    }

    public QDimension getSize() {
        return new QDimension(width, height);
    }

    @Override
    public QDimension clone() {
        return (QDimension) super.clone();
    }
}
