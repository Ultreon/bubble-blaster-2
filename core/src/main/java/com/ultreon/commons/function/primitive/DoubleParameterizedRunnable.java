package com.ultreon.commons.function.primitive;

@Deprecated
@FunctionalInterface
public interface DoubleParameterizedRunnable<A, B> {
    void run(A a, B b);
}
