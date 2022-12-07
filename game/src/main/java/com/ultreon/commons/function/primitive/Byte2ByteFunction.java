package com.ultreon.commons.function.primitive;

import java.util.function.Function;

@FunctionalInterface
public interface Byte2ByteFunction extends Function<Byte, Byte> {
    @Override
    @Deprecated
    default Byte apply(Byte aByte) {
        return apply((byte) aByte);
    }

    byte apply(byte x);

    static Byte2ByteFunction and(byte amount) {
        return x -> (byte) (x & amount);
    }

    static Byte2ByteFunction or(byte amount) {
        return x -> (byte) (x | amount);
    }

    static Byte2ByteFunction sub(byte amount) {
        return x -> (byte) (x - amount);
    }

    static Byte2ByteFunction mul(byte amount) {
        return x -> (byte) (x * amount);
    }

    static Byte2ByteFunction div(byte amount) {
        return x -> (byte) (x / amount);
    }

    static Byte2ByteFunction mod(byte amount) {
        return x -> (byte) (x % amount);
    }

    static Byte2ByteFunction pow(byte amount) {
        return x -> (byte) Math.pow(x, amount);
    }

    static Byte2ByteFunction sqrt() {
        return x -> (byte) Math.sqrt(x);
    }

    static Byte2ByteFunction round() {
        return a -> (byte) Math.round(a);
    }

    static Byte2ByteFunction asin() {
        return x -> (byte) Math.asin(x);
    }

    static Byte2ByteFunction acos() {
        return x -> (byte) Math.acos(x);
    }

    static Byte2ByteFunction atan() {
        return x -> (byte) Math.atan(x);
    }

    static Byte2ByteFunction atan2(byte y) {
        return x -> (byte) Math.atan2(x, y);
    }

    static Byte2ByteFunction sin() {
        return x -> (byte) Math.sin(x);
    }

    static Byte2ByteFunction cos() {
        return x -> (byte) Math.cos(x);
    }

    static Byte2ByteFunction tan() {
        return x -> (byte) Math.tan(x);
    }

    static Byte2ByteFunction sinh() {
        return x -> (byte) Math.sinh(x);
    }

    static Byte2ByteFunction cosh() {
        return x -> (byte) Math.cosh(x);
    }

    static Byte2ByteFunction tanh() {
        return x -> (byte) Math.tanh(x);
    }

    static Byte2ByteFunction floor() {
        return x -> (byte) Math.floor(x);
    }

    static Byte2ByteFunction ceil() {
        return x -> (byte) Math.ceil(x);
    }

    static Byte2ByteFunction log() {
        return x -> (byte) Math.log(x);
    }

    static Byte2ByteFunction log10() {
        return x -> (byte) Math.log10(x);
    }

    static Byte2ByteFunction log1p() {
        return x -> (byte) Math.log1p(x);
    }

    static Byte2ByteFunction minus() {
        return x -> (byte) -x;
    }

    static Byte2ByteFunction plus() {
        return x -> (byte) +x;
    }

    static Byte2ByteFunction ulp() {
        return f -> (byte) Math.ulp(f);
    }

    static Byte2ByteFunction signum() {
        return f -> (byte) Math.signum(f);
    }

    static Byte2ByteFunction scalb(int scaleFactor) {
        return x -> (byte) Math.scalb(x, scaleFactor);
    }
}
