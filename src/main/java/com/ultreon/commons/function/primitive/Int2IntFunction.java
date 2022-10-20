package com.ultreon.commons.function.primitive;

import java.util.function.Function;

@FunctionalInterface
public interface Int2IntFunction extends Function<Integer, Integer> {
    @Override
    @Deprecated
    default Integer apply(Integer aInteger) {
        return apply((int) aInteger);
    }

    int apply(int x);

    static Int2IntFunction and(int amount) {
        return x -> x & amount;
    }

    static Int2IntFunction or(int amount) {
        return x -> x | amount;
    }

    static Int2IntFunction sub(int amount) {
        return x -> x - amount;
    }

    static Int2IntFunction mul(int amount) {
        return x -> x * amount;
    }

    static Int2IntFunction div(int amount) {
        return x -> x / amount;
    }

    static Int2IntFunction mod(int amount) {
        return x -> x % amount;
    }

    static Int2IntFunction pow(int amount) {
        return x -> (int) Math.pow(x, amount);
    }

    static Int2IntFunction sqrt() {
        return x -> (int) Math.sqrt(x);
    }

    static Int2IntFunction round() {
        return Math::round;
    }

    static Int2IntFunction asin() {
        return x -> (int) Math.asin(x);
    }

    static Int2IntFunction acos() {
        return x -> (int) Math.acos(x);
    }

    static Int2IntFunction atan() {
        return x -> (int) Math.atan(x);
    }

    static Int2IntFunction atan2(int y) {
        return x -> (int) Math.atan2(x, y);
    }

    static Int2IntFunction sin() {
        return x -> (int) Math.sin(x);
    }

    static Int2IntFunction cos() {
        return x -> (int) Math.cos(x);
    }

    static Int2IntFunction tan() {
        return x -> (int) Math.tan(x);
    }

    static Int2IntFunction sinh() {
        return x -> (int) Math.sinh(x);
    }

    static Int2IntFunction cosh() {
        return x -> (int) Math.cosh(x);
    }

    static Int2IntFunction tanh() {
        return x -> (int) Math.tanh(x);
    }

    static Int2IntFunction floor() {
        return x -> (int) Math.floor(x);
    }

    static Int2IntFunction ceil() {
        return x -> (int) Math.ceil(x);
    }

    static Int2IntFunction log() {
        return x -> (int) Math.log(x);
    }

    static Int2IntFunction log10() {
        return x -> (int) Math.log10(x);
    }

    static Int2IntFunction log1p() {
        return x -> (int) Math.log1p(x);
    }

    static Int2IntFunction minus() {
        return x -> -x;
    }

    static Int2IntFunction plus() {
        return x -> +x;
    }

    static Int2IntFunction ulp() {
        return f -> (int) Math.ulp(f);
    }

    static Int2IntFunction signum() {
        return f -> (int) Math.signum(f);
    }

    static Int2IntFunction scalb(int scaleFactor) {
        return x -> (int) Math.scalb(x, scaleFactor);
    }
}
