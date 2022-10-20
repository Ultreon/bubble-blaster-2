package com.ultreon.bubbles.util.position;

public class AbsoluteSize extends Size {
    public AbsoluteSize(double w, double h) {
        super(w, h);
    }

    @Override
    public boolean isAbsolute() {
        return true;
    }
}
