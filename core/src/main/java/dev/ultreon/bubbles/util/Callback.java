package dev.ultreon.bubbles.util;

@FunctionalInterface
public interface Callback<T> {
    void call(T t);
}
