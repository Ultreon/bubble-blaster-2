package com.ultreon.commons.function;

@FunctionalInterface
public interface ParameterizedRunnable<T> {
    void run(T t);
}
