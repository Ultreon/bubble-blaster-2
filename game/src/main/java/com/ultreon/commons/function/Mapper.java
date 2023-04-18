package com.ultreon.commons.function;

@Deprecated
@FunctionalInterface
public interface Mapper<A, B> {
    B map(A value);
}
