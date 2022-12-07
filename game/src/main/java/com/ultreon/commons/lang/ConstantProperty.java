package com.ultreon.commons.lang;

public class ConstantProperty<T> implements IProperty<T> {
    private final T value;

    public ConstantProperty() {
        this.value = null;
    }

    public ConstantProperty(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        throw new UnsupportedOperationException("Can't set constant property.");
    }
}
