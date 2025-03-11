package dev.ultreon.bubbles.vector;

public interface VectorD<T> {
    T scl(T value);

    T add(T value);

    T sub(T value);

    T div(T value);
}
