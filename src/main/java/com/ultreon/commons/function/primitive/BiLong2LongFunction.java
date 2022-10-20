package com.ultreon.commons.function.primitive;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BiLong2LongFunction extends BiFunction<Long, Long, Long> {
    @Override
    @Deprecated
    default Long apply(Long a, Long b) {
        return apply((long) a, (long) b);
    }

    long apply(long a, long b);

    static BiLong2LongFunction and() {
        return (x, y) -> x & y;
    }

    static BiLong2LongFunction or() {
        return (x, y) -> x | y;
    }

    static BiLong2LongFunction add() {
        return Long::sum;
    }

    static BiLong2LongFunction sub() {
        return (x, y) -> x - y;
    }

    static BiLong2LongFunction mul() {
        return (x, y) -> x * y;
    }

    static BiLong2LongFunction div() {
        return (x, y) -> x / y;
    }

    static BiLong2LongFunction mod() {
        return (x, y) -> x % y;
    }

    static BiLong2LongFunction pow() {
        return (x, y) -> (long) Math.pow(x, y);
    }

    static BiLong2LongFunction atan2() {
        return (x, y) -> (long) Math.atan2(x, y);
    }

    static BiLong2LongFunction scalb() {
        return (x, y) -> (long) Math.scalb(x, (int) y);
    }
}
