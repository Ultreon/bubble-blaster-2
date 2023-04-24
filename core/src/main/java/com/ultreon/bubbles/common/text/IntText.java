package com.ultreon.bubbles.common.text;

public class IntText extends NumberText<Integer> {
    public IntText(int value) {
        super(value);
    }

    int getIntValue() {
        return getValue();
    }
}
