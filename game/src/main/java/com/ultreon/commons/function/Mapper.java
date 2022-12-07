package com.ultreon.commons.function;

@FunctionalInterface
public interface Mapper<A, B> {
    B map(A value);
}
