package com.ultreon.commons.function.primitive;

import java.util.function.Function;

@FunctionalInterface
public interface Short2ShortFunction extends Function<Short, Short> {
    @Override
    @Deprecated
    default Short apply(Short aShort) {
        return apply((short) aShort);
    }

    short apply(short x);

    static Short2ShortFunction and(short amount) {
        return x -> (short) (x & amount);
    }

    static Short2ShortFunction or(short amount) {
        return x -> (short) (x | amount);
    }

    static Short2ShortFunction sub(short amount) {
        return x -> (short) (x - amount);
    }

    static Short2ShortFunction mul(short amount) {
        return x -> (short) (x * amount);
    }

    static Short2ShortFunction div(short amount) {
        return x -> (short) (x / amount);
    }

    static Short2ShortFunction mod(short amount) {
        return x -> (short) (x % amount);
    }

    static Short2ShortFunction pow(short amount) {
        return x -> (short) Math.pow(x, amount);
    }

    static Short2ShortFunction sqrt() {
        return x -> (short) Math.sqrt(x);
    }

    static Short2ShortFunction round() {
        return a -> (short) Math.round(a);
    }

    static Short2ShortFunction asin() {
        return x -> (short) Math.asin(x);
    }

    static Short2ShortFunction acos() {
        return x -> (short) Math.acos(x);
    }

    static Short2ShortFunction atan() {
        return x -> (short) Math.atan(x);
    }

    static Short2ShortFunction atan2(short y) {
        return x -> (short) Math.atan2(x, y);
    }

    static Short2ShortFunction sin() {
        return x -> (short) Math.sin(x);
    }

    static Short2ShortFunction cos() {
        return x -> (short) Math.cos(x);
    }

    static Short2ShortFunction tan() {
        return x -> (short) Math.tan(x);
    }

    static Short2ShortFunction sinh() {
        return x -> (short) Math.sinh(x);
    }

    static Short2ShortFunction cosh() {
        return x -> (short) Math.cosh(x);
    }

    static Short2ShortFunction tanh() {
        return x -> (short) Math.tanh(x);
    }

    static Short2ShortFunction floor() {
        return x -> (short) Math.floor(x);
    }

    static Short2ShortFunction ceil() {
        return x -> (short) Math.ceil(x);
    }

    static Short2ShortFunction log() {
        return x -> (short) Math.log(x);
    }

    static Short2ShortFunction log10() {
        return x -> (short) Math.log10(x);
    }

    static Short2ShortFunction log1p() {
        return x -> (short) Math.log1p(x);
    }

    static Short2ShortFunction minus() {
        return x -> (short) -x;
    }

    static Short2ShortFunction plus() {
        return x -> (short) +x;
    }

    static Short2ShortFunction ulp() {
        return f -> (short) Math.ulp(f);
    }

    static Short2ShortFunction signum() {
        return f -> (short) Math.signum(f);
    }

    static Short2ShortFunction scalb(int scaleFactor) {
        return x -> (short) Math.scalb(x, scaleFactor);
    }
}
