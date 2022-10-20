package com.ultreon.commons.function.primitive;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BiFloat2FloatFunction extends BiFunction<Float, Float, Float> {
    @Override
    @Deprecated
    default Float apply(Float a, Float b) {
        return apply((float) a, (float) b);
    }

    float apply(float a, float b);

    static BiFloat2FloatFunction add() {
        return Float::sum;
    }

    static BiFloat2FloatFunction sub() {
        return (x, y) -> x - y;
    }

    static BiFloat2FloatFunction mul() {
        return (x, y) -> x * y;
    }

    static BiFloat2FloatFunction div() {
        return (x, y) -> x / y;
    }

    static BiFloat2FloatFunction mod() {
        return (x, y) -> x % y;
    }

    static BiFloat2FloatFunction pow() {
        return (x, y) -> (float) Math.pow(x, y);
    }

    static BiFloat2FloatFunction atan2() {
        return (x, y) -> (float) Math.atan2(x, y);
    }

    static BiFloat2FloatFunction scalb() {
        return (x, y) -> Math.scalb(x, (int) y);
    }
}
