package com.ultreon.commons.function;

@Deprecated
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
    T get() throws E;
}
