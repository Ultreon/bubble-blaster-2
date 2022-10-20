package com.ultreon.bubbles.util.position;

import java.awt.geom.Point2D;

public abstract class PercentagePosition extends Position {
    private double rawX;
    private double rawY;
    private double percentY;
    private double percentX;

    public PercentagePosition(double X, double Y, double percentX, double percentY) {
        super(X * percentX, Y * percentY);
        this.rawX = X;
        this.rawY = Y;
        this.percentX = percentX;
        this.percentY = percentY;
    }

    public PercentagePosition(Point2D point, double percentX, double percentY) {
        super(point.getX() * percentX, point.getY() * percentY);
        this.rawX = point.getX();
        this.rawY = point.getY();
        this.percentX = percentX;
        this.percentY = percentY;
    }

    @Override
    public boolean isAbsolute() {
        return false;
    }

    public abstract void tick();

    public double getPercentX() {
        return percentX;
    }

    public double getPercentY() {
        return percentY;
    }

    public void setPercentX(double percentX) {
        this.percentX = percentX;
        this.setPointX(rawX * percentX);
    }

    public void setPercentY(double percentY) {
        this.percentY = percentY;
        this.setPointY(rawY * percentY);
    }

    public double getRawX() {
        return rawX;
    }

    public double getRawY() {
        return rawY;
    }

    public void setRawX(double rawX) {
        this.rawX = rawX;
        setPointX(rawX * percentX);
    }

    public void setRawY(double rawY) {
        this.rawY = rawY;
        setPointY(rawY * percentY);
    }
}
