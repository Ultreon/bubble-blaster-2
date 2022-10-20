package com.ultreon.bubbles.common.text;

public class NumberText<T extends Number> extends TextObject {
    private final T value;

    public NumberText(T value) {
        this.value = value;
    }

    @Override
    public String getText() {
        return getValue().toString();
    }

    public T getValue() {
        return value;
    }
}
