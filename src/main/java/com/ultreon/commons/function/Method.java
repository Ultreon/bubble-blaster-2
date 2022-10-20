package com.ultreon.commons.function;

@FunctionalInterface
public interface Method<T> {
    Object call(T instance, Object... params);
}
