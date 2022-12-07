package com.ultreon.commons.function.primitive;

import java.util.function.Function;

@FunctionalInterface
public interface Double2DoubleFunction extends Function<Double, Double> {
    @Override
    @Deprecated
    default Double apply(Double aDouble) {
        return apply((double) aDouble);
    }

    double apply(double f);

    static Double2DoubleFunction add(double amount) {
        return x -> x + amount;
    }

    static Double2DoubleFunction sub(double amount) {
        return x -> x - amount;
    }

    static Double2DoubleFunction mul(double amount) {
        return x -> x * amount;
    }

    static Double2DoubleFunction div(double amount) {
        return x -> x / amount;
    }

    static Double2DoubleFunction mod(double amount) {
        return x -> x % amount;
    }

    static Double2DoubleFunction pow(double amount) {
        return x -> Math.pow(x, amount);
    }

    static Double2DoubleFunction sqrt() {
        return Math::sqrt;
    }

    static Double2DoubleFunction round() {
        return x -> (double) Math.round(x);
    }

    static Double2DoubleFunction asin() {
        return Math::asin;
    }

    static Double2DoubleFunction acos() {
        return Math::acos;
    }

    static Double2DoubleFunction atan() {
        return Math::atan;
    }

    static Double2DoubleFunction atan2(double y) {
        return x -> Math.atan2(x, y);
    }

    static Double2DoubleFunction sin() {
        return Math::sin;
    }

    static Double2DoubleFunction cos() {
        return Math::cos;
    }

    static Double2DoubleFunction tan() {
        return Math::tan;
    }

    static Double2DoubleFunction sinh() {
        return Math::sinh;
    }

    static Double2DoubleFunction cosh() {
        return Math::cosh;
    }

    static Double2DoubleFunction tanh() {
        return Math::tanh;
    }

    static Double2DoubleFunction floor() {
        return Math::floor;
    }

    static Double2DoubleFunction ceil() {
        return Math::ceil;
    }

    static Double2DoubleFunction log() {
        return Math::log;
    }

    static Double2DoubleFunction log10() {
        return Math::log10;
    }

    static Double2DoubleFunction log1p() {
        return Math::log1p;
    }

    static Double2DoubleFunction ulp() {
        return Math::ulp;
    }

    static Double2DoubleFunction signum() {
        return Math::signum;
    }

    static Double2DoubleFunction minus() {
        return x -> -x;
    }

    static Double2DoubleFunction plus() {
        return x -> +x;
    }

    static Double2DoubleFunction scalb(int scaleFactor) {
        return x -> Math.scalb(x, scaleFactor);
    }
}
