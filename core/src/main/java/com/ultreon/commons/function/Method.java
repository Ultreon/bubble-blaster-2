package com.ultreon.commons.function;

@Deprecated
@FunctionalInterface
public interface Method<T> {
    Object call(T instance, Object... params);
}
