package com.ultreon.commons.function.primitive;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BiInt2IntFunction extends BiFunction<Integer, Integer, Integer> {
    @Override
    @Deprecated
    default Integer apply(Integer a, Integer b) {
        return apply((int) a, (int) b);
    }

    int apply(int a, int b);

    static BiInt2IntFunction and() {
        return (x, y) -> x & y;
    }

    static BiInt2IntFunction or() {
        return (x, y) -> x | y;
    }

    static BiInt2IntFunction add() {
        return Integer::sum;
    }

    static BiInt2IntFunction sub() {
        return (x, y) -> x - y;
    }

    static BiInt2IntFunction mul() {
        return (x, y) -> x * y;
    }

    static BiInt2IntFunction div() {
        return (x, y) -> x / y;
    }

    static BiInt2IntFunction mod() {
        return (x, y) -> x % y;
    }

    static BiInt2IntFunction pow() {
        return (x, y) -> (int) Math.pow(x, y);
    }

    static BiInt2IntFunction atan2() {
        return (x, y) -> (int) Math.atan2(x, y);
    }

    static BiInt2IntFunction scalb() {
        return (x, y) -> (int) Math.scalb(x, y);
    }
}
