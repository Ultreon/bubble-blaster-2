package com.ultreon.commons.function.primitive;

import java.util.function.Function;

@FunctionalInterface
public interface Long2LongFunction extends Function<Long, Long> {
    @Override
    @Deprecated
    default Long apply(Long aLong) {
        return apply((long) aLong);
    }

    long apply(long x);

    static Long2LongFunction and(long amount) {
        return x -> x & amount;
    }

    static Long2LongFunction or(long amount) {
        return x -> x | amount;
    }

    static Long2LongFunction sub(long amount) {
        return x -> x - amount;
    }

    static Long2LongFunction mul(long amount) {
        return x -> x * amount;
    }

    static Long2LongFunction div(long amount) {
        return x -> x / amount;
    }

    static Long2LongFunction mod(long amount) {
        return x -> x % amount;
    }

    static Long2LongFunction pow(long amount) {
        return x -> (long) Math.pow(x, amount);
    }

    static Long2LongFunction sqrt() {
        return x -> (long) Math.sqrt(x);
    }

    static Long2LongFunction round() {
        return Math::round;
    }

    static Long2LongFunction asin() {
        return x -> (long) Math.asin(x);
    }

    static Long2LongFunction acos() {
        return x -> (long) Math.acos(x);
    }

    static Long2LongFunction atan() {
        return x -> (long) Math.atan(x);
    }

    static Long2LongFunction atan2(long y) {
        return x -> (long) Math.atan2(x, y);
    }

    static Long2LongFunction sin() {
        return x -> (long) Math.sin(x);
    }

    static Long2LongFunction cos() {
        return x -> (long) Math.cos(x);
    }

    static Long2LongFunction tan() {
        return x -> (long) Math.tan(x);
    }

    static Long2LongFunction sinh() {
        return x -> (long) Math.sinh(x);
    }

    static Long2LongFunction cosh() {
        return x -> (long) Math.cosh(x);
    }

    static Long2LongFunction tanh() {
        return x -> (long) Math.tanh(x);
    }

    static Long2LongFunction floor() {
        return x -> (long) Math.floor(x);
    }

    static Long2LongFunction ceil() {
        return x -> (long) Math.ceil(x);
    }

    static Long2LongFunction log() {
        return x -> (long) Math.log(x);
    }

    static Long2LongFunction log10() {
        return x -> (long) Math.log10(x);
    }

    static Long2LongFunction log1p() {
        return x -> (long) Math.log1p(x);
    }

    static Long2LongFunction minus() {
        return x -> -x;
    }

    static Long2LongFunction plus() {
        return x -> +x;
    }

    static Long2LongFunction ulp() {
        return f -> (long) Math.ulp(f);
    }

    static Long2LongFunction signum() {
        return f -> (long) Math.signum(f);
    }

    static Long2LongFunction scalb(int scaleFactor) {
        return x -> (long) Math.scalb(x, scaleFactor);
    }
}
