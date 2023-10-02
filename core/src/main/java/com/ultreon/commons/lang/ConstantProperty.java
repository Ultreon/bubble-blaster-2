package com.ultreon.commons.lang;

@Deprecated
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
        return this.value;
    }

    @Override
    public void set(T value) {
        throw new UnsupportedOperationException("Can't set constant property.");
    }
}
