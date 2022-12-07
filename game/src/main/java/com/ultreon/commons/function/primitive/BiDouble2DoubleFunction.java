package com.ultreon.commons.function.primitive;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BiDouble2DoubleFunction extends BiFunction<Double, Double, Double> {
    @Override
    @Deprecated
    default Double apply(Double a, Double b) {
        return apply((double) a, (double) b);
    }

    double apply(double a, double b);

    static BiDouble2DoubleFunction add() {
        return Double::sum;
    }

    static BiDouble2DoubleFunction sub() {
        return (x, y) -> x - y;
    }

    static BiDouble2DoubleFunction mul() {
        return (x, y) -> x * y;
    }

    static BiDouble2DoubleFunction div() {
        return (x, y) -> x / y;
    }

    static BiDouble2DoubleFunction mod() {
        return (x, y) -> x % y;
    }

    static BiDouble2DoubleFunction pow() {
        return Math::pow;
    }

    static BiDouble2DoubleFunction atan2() {
        return Math::atan2;
    }

    static BiDouble2DoubleFunction scalb() {
        return (x, y) -> Math.scalb(x, (int) y);
    }
}
