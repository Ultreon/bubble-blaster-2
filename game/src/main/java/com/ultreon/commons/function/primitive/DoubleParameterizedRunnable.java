package com.ultreon.commons.function.primitive;

@FunctionalInterface
public interface DoubleParameterizedRunnable<A, B> {
    void run(A a, B b);
}
