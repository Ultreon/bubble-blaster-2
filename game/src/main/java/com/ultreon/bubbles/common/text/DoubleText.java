package com.ultreon.bubbles.common.text;

public class DoubleText extends NumberText<Double> {
    public DoubleText(double value) {
        super(value);
    }

    double getDoubleValue() {
        return getValue();
    }
}
