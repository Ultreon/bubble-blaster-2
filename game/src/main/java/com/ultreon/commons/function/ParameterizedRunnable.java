package com.ultreon.commons.function;

@Deprecated
@FunctionalInterface
public interface ParameterizedRunnable<T> {
    void run(T t);
}
