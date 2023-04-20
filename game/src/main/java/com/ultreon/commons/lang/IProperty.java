package com.ultreon.commons.lang;

@Deprecated
public interface IProperty<T> {
    T get();

    void set(T value);
}
