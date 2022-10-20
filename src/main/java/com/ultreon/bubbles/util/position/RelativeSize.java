package com.ultreon.bubbles.util.position;

import com.ultreon.bubbles.util.position.util.DimensionDouble;

public abstract class RelativeSize extends Size {
    public RelativeSize(double w, double h) {
        super(w, h);
    }

    public RelativeSize(DimensionDouble size) {
        super(size.getWidth(), size.getHeight());
    }

    @Override
    public boolean isAbsolute() {
        return false;
    }

    public abstract void tick();

    public abstract double getRelWidth();

    public abstract double getRelHeight();

    public abstract void setRelWidth(double w);

    public abstract void setRelHeight(double h);
}
