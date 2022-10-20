package com.ultreon.bubbles.common.text;

public class LongText extends NumberText<Long> {
    public LongText(long value) {
        super(value);
    }

    long getLongValue() {
        return getValue();
    }
}
