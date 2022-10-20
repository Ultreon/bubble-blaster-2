package com.ultreon.bubbles.common.text;

public class FloatText extends NumberText<Float> {
    public FloatText(float value) {
        super(value);
    }

    float getFloatValue() {
        return getValue();
    }
}
