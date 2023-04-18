package com.ultreon.commons.function;

@Deprecated
@FunctionalInterface
public interface Applier<T, R> {
    R apply(T obj);
}
