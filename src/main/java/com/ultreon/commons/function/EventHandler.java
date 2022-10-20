package com.ultreon.commons.function;

@Deprecated
@FunctionalInterface
public interface EventHandler<T> {
    void run(T evt);
}
