package com.ultreon.commons.lang;

public class Property<T> implements IProperty<T> {
    private T value;

    public Property() {
        this.value = null;
    }

    public Property(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }
}
