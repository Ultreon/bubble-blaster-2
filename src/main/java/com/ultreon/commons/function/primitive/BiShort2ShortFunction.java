package com.ultreon.commons.function.primitive;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BiShort2ShortFunction extends BiFunction<Short, Short, Short> {
    @Override
    @Deprecated
    default Short apply(Short a, Short b) {
        return apply((short) a, (short) b);
    }

    short apply(short a, short b);

    static BiShort2ShortFunction and() {
        return (x, y) -> (short) (x & y);
    }

    static BiShort2ShortFunction or() {
        return (x, y) -> (short) (x | y);
    }

    static BiShort2ShortFunction add() {
        return (x, y) -> (short) (x + y);
    }

    static BiShort2ShortFunction sub() {
        return (x, y) -> (short) (x - y);
    }

    static BiShort2ShortFunction mul() {
        return (x, y) -> (short) (x * y);
    }

    static BiShort2ShortFunction div() {
        return (x, y) -> (short) (x / y);
    }

    static BiShort2ShortFunction mod() {
        return (x, y) -> (short) (x % y);
    }

    static BiShort2ShortFunction pow() {
        return (x, y) -> (short) Math.pow(x, y);
    }

    static BiShort2ShortFunction atan2() {
        return (x, y) -> (short) Math.atan2(x, y);
    }

    static BiShort2ShortFunction scalb() {
        return (x, y) -> (short) Math.scalb(x, y);
    }
}
