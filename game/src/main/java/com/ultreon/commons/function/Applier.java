package com.ultreon.commons.function;

@FunctionalInterface
public interface Applier<T, R> {
    R apply(T obj);
}
