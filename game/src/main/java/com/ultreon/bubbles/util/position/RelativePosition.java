package com.ultreon.bubbles.util.position;

import java.awt.geom.Point2D;

public abstract class RelativePosition extends Position {
    public RelativePosition(double x, double y) {
        super(x, y);
    }

    public RelativePosition(Point2D point) {
        super(point.getX(), point.getY());
    }

    @Override
    public boolean isAbsolute() {
        return false;
    }

    public abstract void tick();

    public abstract double getRelX();

    public abstract double getRelY();

    public abstract void setRelX(double x);

    public abstract void setRelY(double y);
}
